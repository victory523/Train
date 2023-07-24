import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Observable, of, throwError } from 'rxjs';
import { AppComponent } from './app.component';
import { WithingsService } from './withings.service';
import { CommonComponentsModule } from './common-components/common-components.module';
import { NotificationService } from './common-components/notification.service';
import { NotificationsComponent } from './common-components/notifications/notifications.component';
import { MainComponent } from './common-components/main/main.component';
import { HeaderComponent } from './common-components/header/header.component';
import { LoaderComponent } from './common-components/loader/loader.component';
import { HeadingComponent } from './common-components/heading/heading.component';

@Component({
  selector: 'app-weight',
  template: '87.6',
})
class MockWeightComponent {}

@Component({
  selector: 'app-notifications',
})
class MockNotificationsComponent {}

async function setup({ $sync }: { $sync: Observable<void> } = { $sync: of() }) {
  const showNotification = jasmine.createSpy();
  const mockNotificationService = jasmine.createSpyObj('NotificationService', {
    showNotification,
  });
  const mockWithingsService = jasmine.createSpyObj('WithingsService', {
    sync: $sync,
  });
  await TestBed.configureTestingModule({
    declarations: [
      AppComponent,
      MockWeightComponent,
      MockNotificationsComponent,
      MainComponent,
      HeaderComponent,
      HeadingComponent,
      LoaderComponent,
    ],
    providers: [
      { provide: WithingsService, useValue: mockWithingsService },
      { provide: NotificationService, useValue: mockNotificationService },
    ],
  }).compileComponents();

  const fixture = TestBed.createComponent(AppComponent);
  fixture.detectChanges();

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement,
    mockNotificationService,
  };
}

describe('AppComponent', () => {
  it('should render loading state', async () => {
    const { element } = await setup();
    expect(element.querySelector('app-loading')).toBeDefined();
  });

  it('renders weight', async () => {
    const { element } = await setup({ $sync: of(undefined) });
    expect(element.querySelector('#main')?.textContent).toEqual('87.6');
  });

  it('renders error state', async () => {
    const { mockNotificationService } = await setup({
      $sync: throwError(() => {}),
    });
    expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
      'Unable to sync with Withings',
      'error'
    );
  });
});

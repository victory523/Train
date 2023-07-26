import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Subject } from 'rxjs';
import { AppComponent } from './app.component';
import { CommonComponentsModule } from './common-components/common-components.module';
import { NotificationService } from './common-components/notification.service';
import { WithingsService } from './withings.service';

@Component({
  selector: 'app-weight',
  template: '87.6',
})
class MockWeightComponent {}

async function setup() {
  const syncSubject = new Subject<void>();
  const mockWithingsService: jasmine.SpyObj<WithingsService> =
    jasmine.createSpyObj(['sync']);
  mockWithingsService.sync.and.returnValue(syncSubject.asObservable());

  await TestBed.configureTestingModule({
    declarations: [AppComponent, MockWeightComponent],
    providers: [
      { provide: WithingsService, useValue: mockWithingsService },
      NotificationService,
    ],
    imports: [CommonComponentsModule, NoopAnimationsModule],
  }).compileComponents();

  const fixture = TestBed.createComponent(AppComponent);
  fixture.detectChanges();

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement,
    syncSubject
  };
}

describe('AppComponent', () => {
  it('should render header', async () => {
    const { element } = await setup();
    expect(element.querySelector('app-header')?.textContent).toBe('Workout');
  });

  it('should render loading state', async () => {
    const { element } = await setup();
    expect(element.querySelector('app-loading')).toBeDefined();
  });

  it('renders weight', async () => {
    const { element, fixture, syncSubject } = await setup();
    syncSubject.next(undefined);
    fixture.detectChanges();
    expect(element.querySelector('#main')?.textContent).toEqual('87.6');
  });

  it('renders error notification', async () => {
    const { element, fixture, syncSubject } = await setup();
    syncSubject.error({});
    fixture.detectChanges();
    expect(element.querySelector('app-notification')?.textContent).toBe('Unable to sync with Withings')
    expect(element.querySelector('app-notification')?.classList.contains('error')).toBe(true)
  });
});

import { Component, Directive, Input } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Subject, of } from 'rxjs';
import { AppComponent } from './app.component';
import { NotificationService } from './common-components/notification.service';
import { BackupService } from './backup/backup.service';
import { RelativeTimePipe } from './utils/relative-time.pipe';
import { provideHttpClient } from '@angular/common/http';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Directive({
  standalone: true,
  selector: '[routerLink]',
})
class MockRouterLink {
  @Input()
  routerLink?: string;
}

@Component({
  standalone: true,
  selector: 'router-outlet',
  template: '',
})
class MockRouterOutlet {}

async function setup() {
  const mockBackupService: jasmine.SpyObj<BackupService> = jasmine.createSpyObj(
    ['getLastBackupTime']
  );
  mockBackupService.getLastBackupTime.and.returnValue(
    of(new Date(Date.now() - 5 * 60 * 1000))
  );

  await TestBed.configureTestingModule({
    imports: [RelativeTimePipe],
    providers: [
      provideNoopAnimations(),
      provideHttpClient(),
      NotificationService,
      { provide: BackupService, useValue: mockBackupService },
    ],
  }).compileComponents();

  TestBed.overrideComponent(AppComponent, {
    remove: {
      imports: [RouterOutlet, RouterLink, RouterLinkActive],
    },
    add: {
      imports: [MockRouterOutlet, MockRouterLink],
    },
  });

  const fixture = TestBed.createComponent(AppComponent);
  fixture.detectChanges();

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement,
  };
}

describe('AppComponent', () => {
  it('should render header', async () => {
    const { element } = await setup();
    expect(element.querySelector('header h1')?.textContent).toBe('W6');
  });

  it('renders last backup time', async () => {
    const { element, fixture } = await setup();
    expect(
      Array.from(element.querySelectorAll('a')).find((e) =>
        e.textContent?.includes('Last backup')
      )?.textContent
    ).toEqual('Last backup 5 minutes ago');
  });
});

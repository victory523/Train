import { Component, Directive, Input } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Subject } from 'rxjs';
import { AppComponent } from './app.component';
import { NotificationService } from './common-components/notification.service';
import { BackupService, LastBackup } from './services/backup.service';
import { WithingsService } from './services/withings.service';
import { RelativeTimePipe } from './utils/relative-time.pipe';
import { WeightService } from './services/weight.service';
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
  const syncSubject = new Subject<void>();
  const mockWithingsService: jasmine.SpyObj<WithingsService> =
    jasmine.createSpyObj(['sync']);
  mockWithingsService.sync.and.returnValue(syncSubject.asObservable());

  const lastBackupSubject = new Subject<LastBackup>();
  const mockBackupService: jasmine.SpyObj<BackupService> = jasmine.createSpyObj(
    ['getLastBackupTime']
  );
  mockBackupService.getLastBackupTime.and.returnValue(lastBackupSubject);

  await TestBed.configureTestingModule({
    imports: [RelativeTimePipe],
    providers: [
      provideNoopAnimations(),
      provideHttpClient(),
      WeightService,
      { provide: WithingsService, useValue: mockWithingsService },
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
    syncSubject,
    lastBackupSubject,
  };
}

describe('AppComponent', () => {
  it('should render header', async () => {
    const { element } = await setup();
    expect(element.querySelector('header h1')?.textContent).toBe('W6');
  });

  it('should render loading state', async () => {
    const { element } = await setup();
    expect(element.querySelector('[aria-busy="true"]')).toBeDefined();
  });

  it('renders error notification on Withings sync failure', async () => {
    const { element, fixture, syncSubject } = await setup();
    syncSubject.error({});
    fixture.detectChanges();
    expect(element.querySelector('li[role="alert"]')?.textContent?.trim()).toBe(
      'Unable to sync with Withings'
    );
    expect(
      element.querySelector('li[role="alert"]')?.classList.contains('error')
    ).toBe(true);
  });

  it('renders last backup time', async () => {
    const { element, fixture, lastBackupSubject } = await setup();
    lastBackupSubject.next({ time: new Date(Date.now() - 5 * 60 * 1000) });
    fixture.detectChanges();
    expect(
      Array.from(element.querySelectorAll('a')).find((e) =>
        e.textContent?.includes('Last backup')
      )?.textContent
    ).toEqual('Last backup 5 minutes ago');
  });

  it('renders error notification on last backup time fetching failure', async () => {
    const { element, fixture, lastBackupSubject } = await setup();
    lastBackupSubject.error({});
    fixture.detectChanges();
    expect(element.querySelector('li[role="alert"]')?.textContent?.trim()).toBe(
      'Unable to fetch last backup time'
    );
    expect(
      element.querySelector('li[role="alert"]')?.classList.contains('error')
    ).toBe(true);
  });

  it('renders error notification on last backup time error message', async () => {
    const { element, fixture, lastBackupSubject } = await setup();
    lastBackupSubject.next({
      time: new Date(),
      errorMessage: 'last backup time error message',
    });
    fixture.detectChanges();
    expect(element.querySelector('li[role="alert"]')?.textContent?.trim()).toBe(
      'last backup time error message'
    );
    expect(
      element.querySelector('li[role="alert"]')?.classList.contains('error')
    ).toBe(true);
  });
});

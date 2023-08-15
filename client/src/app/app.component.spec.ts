import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Subject } from 'rxjs';
import { AppComponent } from './app.component';
import { CommonComponentsModule } from './common-components/common-components.module';
import { NotificationService } from './common-components/notification.service';
import { WithingsService } from './withings.service';
import { BackupService, LastBackup } from './backup.service';
import { RelativeTimePipe } from './utils/relative-time.pipe';

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

  const lastBackupSubject = new Subject<LastBackup>();
  const mockBackupService: jasmine.SpyObj<BackupService> = jasmine.createSpyObj(['getLastBackupTime'])
  mockBackupService.getLastBackupTime.and.returnValue(lastBackupSubject)

  await TestBed.configureTestingModule({
    declarations: [AppComponent, MockWeightComponent, RelativeTimePipe],
    providers: [
      { provide: WithingsService, useValue: mockWithingsService },
      NotificationService,
      { provide: BackupService, useValue: mockBackupService },
    ],
    imports: [CommonComponentsModule, NoopAnimationsModule],
  }).compileComponents();

  const fixture = TestBed.createComponent(AppComponent);
  fixture.detectChanges();

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement,
    syncSubject,
    lastBackupSubject
  };
}

describe('AppComponent', () => {
  it('should render header', async () => {
    const { element } = await setup();
    expect(element.querySelector('header h1')?.textContent).toBe('Workout');
  });

  it('should render loading state', async () => {
    const { element } = await setup();
    expect(element.querySelector('[aria-busy="true"]')).toBeDefined();
  });

  it('renders weight', async () => {
    const { element, fixture, syncSubject } = await setup();
    syncSubject.next(undefined);
    fixture.detectChanges();
    expect(element.querySelector('.main')?.textContent).toEqual('87.6');
  });

  it('renders error notification on Withings sync failure', async () => {
    const { element, fixture, syncSubject } = await setup();
    syncSubject.error({});
    fixture.detectChanges();
    expect(element.querySelector('li[role="alert"]')?.textContent?.trim()).toBe('Unable to sync with Withings')
    expect(element.querySelector('li[role="alert"]')?.classList.contains('error')).toBe(true)
  });

  it('renders last backup time', async () => {
    const { element, fixture, lastBackupSubject } = await setup();
    lastBackupSubject.next({ time: new Date(Date.now() - 5 * 60 * 1000) });
    fixture.detectChanges();
    expect(element.querySelector('header [role="status"]')?.textContent).toEqual('Last backup 5 minutes ago');
  })

  it('renders error notification on last backup time fetching failure', async () => {
    const { element, fixture, lastBackupSubject } = await setup();
    lastBackupSubject.error({});
    fixture.detectChanges();
    expect(element.querySelector('li[role="alert"]')?.textContent?.trim()).toBe('Unable to fetch last backup time')
    expect(element.querySelector('li[role="alert"]')?.classList.contains('error')).toBe(true)
  });

  it('renders error notification on last backup time error message', async () => {
    const { element, fixture, lastBackupSubject } = await setup();
    lastBackupSubject.next({ time: new Date(), errorMessage: 'last backup time error message' });
    fixture.detectChanges();
    expect(element.querySelector('li[role="alert"]')?.textContent?.trim()).toBe('last backup time error message')
    expect(element.querySelector('li[role="alert"]')?.classList.contains('error')).toBe(true)
  });
});

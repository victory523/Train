import { TestBed } from '@angular/core/testing';

import { Subject } from 'rxjs';
import { NotificationService } from '../common-components/notification.service';
import { RideService, RideStats } from '../services/ride.service';
import { RideComponent } from './ride.component';

async function setup({ period }: { period?: number } = {}) {
  const todayRideStatsSubject = new Subject<RideStats>();
  const periodRideStatsSubject = new Subject<RideStats>();
  const mockRideService: jasmine.SpyObj<RideService> = jasmine.createSpyObj([
    'getRideStats',
  ]);
  mockRideService.getRideStats.and.callFake((period) =>
    period === 1
      ? todayRideStatsSubject.asObservable()
      : periodRideStatsSubject.asObservable()
  );
  const mockNotificationService: jasmine.SpyObj<NotificationService> =
    jasmine.createSpyObj(['showNotification']);
  await TestBed.configureTestingModule({
    providers: [
      { provide: RideService, useValue: mockRideService },
      { provide: NotificationService, useValue: mockNotificationService },
    ],
  }).compileComponents();

  const fixture = TestBed.createComponent(RideComponent);
  fixture.componentInstance.period = period;
  fixture.detectChanges();

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement,
    mockNotificationService,
    mockRideService,
    todayRideStatsSubject,
    periodRideStatsSubject,
  };
}

describe('RideComponent', () => {
  it('renders loading state', async () => {
    const { element } = await setup();
    expect(element.querySelector('[aria-busy="true"]')).toBeDefined();
  });

  it('renders loading state if fetching today stats', async () => {
    const { element, periodRideStatsSubject, fixture } = await setup();
    periodRideStatsSubject.next({});
    fixture.detectChanges();
    expect(element.querySelector('[aria-busy="true"]')).toBeDefined();
  });

  it('renders loading state if fetching period stats', async () => {
    const { element, todayRideStatsSubject, fixture } = await setup();
    todayRideStatsSubject.next({});
    fixture.detectChanges();
    expect(element.querySelector('[aria-busy="true"]')).toBeDefined();
  });

  it('renders today ride stats', async () => {
    const { element, fixture, todayRideStatsSubject, periodRideStatsSubject } =
      await setup();
    todayRideStatsSubject.next({
      calories: 646,
      elevationGain: 408,
      distance: 11747.7,
      time: 3074,
    });
    periodRideStatsSubject.next({});
    fixture.detectChanges();
    const valueElements = element.querySelectorAll('h2 + *');
    expect(valueElements[0].textContent?.trim()).toEqual('646');
    expect(valueElements[1].textContent?.trim()).toEqual('408 m');
    expect(valueElements[2].textContent?.trim()).toEqual('12 km');
    expect(valueElements[3].textContent?.trim()).toEqual('51 min');
  });

  it('renders period ride stats', async () => {
    const { element, fixture, todayRideStatsSubject, periodRideStatsSubject } =
      await setup();
    todayRideStatsSubject.next({});
    periodRideStatsSubject.next({
      calories: 646 * 4,
      elevationGain: 408 * 4,
      distance: 11747.7 * 4,
      time: 3074 * 4,
    });
    fixture.detectChanges();
    const valueElements = element.querySelectorAll('h2 + * + *');
    expect(valueElements[0].textContent?.trim()).toEqual('2584');
    expect(valueElements[1].textContent?.trim()).toEqual('1632 m');
    expect(valueElements[2].textContent?.trim()).toEqual('47 km');
    expect(valueElements[3].textContent?.trim()).toEqual('205 min');
  });

  it('renders - if no today stats values are returned', async () => {
    const { element, fixture, todayRideStatsSubject, periodRideStatsSubject } =
      await setup();
    todayRideStatsSubject.next({});
    periodRideStatsSubject.next({});
    fixture.detectChanges();
    const valueElements = element.querySelectorAll('h2 + *');
    expect(valueElements[0].textContent?.trim()).toEqual('-');
    expect(valueElements[1].textContent?.trim()).toEqual('-');
    expect(valueElements[2].textContent?.trim()).toEqual('-');
    expect(valueElements[3].textContent?.trim()).toEqual('-');
  });

  it('renders - if no period stats values are returned', async () => {
    const { element, fixture, todayRideStatsSubject, periodRideStatsSubject } =
      await setup();
    todayRideStatsSubject.next({});
    periodRideStatsSubject.next({});
    fixture.detectChanges();
    const valueElements = element.querySelectorAll('h2 + * + *');
    expect(valueElements[0].textContent?.trim()).toEqual('-');
    expect(valueElements[1].textContent?.trim()).toEqual('-');
    expect(valueElements[2].textContent?.trim()).toEqual('-');
    expect(valueElements[3].textContent?.trim()).toEqual('-');
  });

  it('renders error state if fetching today stats fails', async () => {
    const {
      mockNotificationService,
      fixture,
      todayRideStatsSubject,
      periodRideStatsSubject,
    } = await setup();
    todayRideStatsSubject.error({});
    periodRideStatsSubject.next({});
    fixture.detectChanges();
    expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
      'Unable to fetch ride stats',
      'error'
    );
  });

  it('renders error state if fetching period stats fails', async () => {
    const {
      mockNotificationService,
      fixture,
      todayRideStatsSubject,
      periodRideStatsSubject,
    } = await setup();
    todayRideStatsSubject.next({});
    periodRideStatsSubject.error({});
    fixture.detectChanges();
    expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
      'Unable to fetch ride stats',
      'error'
    );
  });

  it('fetches weight meausrements with week period', async () => {
    const {
      fixture,
      todayRideStatsSubject,
      periodRideStatsSubject,
      mockRideService,
    } = await setup({
      period: 7,
    });
    todayRideStatsSubject.next({});
    periodRideStatsSubject.next({});
    fixture.detectChanges();
    expect(mockRideService.getRideStats).toHaveBeenCalledWith(7);
  });
});

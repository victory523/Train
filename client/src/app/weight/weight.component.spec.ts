import { TestBed } from '@angular/core/testing';

import { Subject } from 'rxjs';
import { NotificationService } from '../common-components/notification.service';
import { WeightComponent } from './weight.component';
import { Directive, Input } from '@angular/core';
import { EChartsOption } from 'echarts';
import { NgxEchartsDirective } from 'ngx-echarts';
import { By } from '@angular/platform-browser';
import { WeightMeasurement, WeightService } from '../services/weight.service';

@Directive({
  selector: '[echarts]',
})
class MockECharts {
  @Input()
  options?: EChartsOption;

  @Input()
  initOpts?: NgxEchartsDirective['initOpts'];
}

async function setup() {
  const weightMeasurementSubject = new Subject<WeightMeasurement[]>();
  const mockWeightService: jasmine.SpyObj<WeightService> = jasmine.createSpyObj(
    ['getWeight', 'getTodayWeight']
  );
  mockWeightService.getWeight.and.returnValue(
    weightMeasurementSubject.asObservable()
  );
  const mockNotificationService: jasmine.SpyObj<NotificationService> =
    jasmine.createSpyObj(['showNotification']);
  await TestBed.configureTestingModule({
    declarations: [WeightComponent, MockECharts],
    providers: [
      { provide: WeightService, useValue: mockWeightService },
      { provide: NotificationService, useValue: mockNotificationService },
    ],
  }).compileComponents();

  const fixture = TestBed.createComponent(WeightComponent);
  fixture.detectChanges();

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement,
    mockNotificationService,
    mockWeightService,
    weightSubject: weightMeasurementSubject,
  };
}

describe('WeightComponent', () => {
  it('renders loading state', async () => {
    const { element } = await setup();
    expect(element.querySelector('[aria-busy="true"]')).toBeDefined();
  });

  it('renders weight', async () => {
    const { element, fixture, weightSubject, mockWeightService } =
      await setup();
    weightSubject.next([]);
    mockWeightService.getTodayWeight.and.returnValue(108.9);
    fixture.detectChanges();
    expect(element.querySelector('[role="status"]')?.textContent).toEqual(
      '108.9'
    );
  });

  it('renders question mark if no weight is returned', async () => {
    const { element, fixture, weightSubject, mockWeightService } =
      await setup();
    weightSubject.next([]);
    mockWeightService.getTodayWeight.and.returnValue(undefined);
    fixture.detectChanges();
    expect(element.querySelector('[role="status"]')?.textContent).toEqual('-');
  });

  it('renders error state', async () => {
    const { mockNotificationService, fixture, weightSubject } = await setup();
    weightSubject.error({});
    fixture.detectChanges();
    expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
      'Unable to fetch weight',
      'error'
    );
  });

  it('selected week period by default', async () => {
    const { fixture, weightSubject } = await setup();
    weightSubject.next([]);
    fixture.detectChanges();
    expect(
      fixture.debugElement.queryAll(By.css('[app-button-group] button'))[0]
        .attributes['aria-pressed']
    ).toBe('true');
  });

  it('updates period selection on click', async () => {
    const { fixture, weightSubject } = await setup();
    weightSubject.next([]);
    fixture.detectChanges();
    fixture.debugElement
      .queryAll(By.css('[app-button-group] button'))[1]
      .triggerEventHandler('click');
    weightSubject.next([]);
    fixture.detectChanges();
    expect(
      fixture.debugElement.queryAll(By.css('[app-button-group] button'))[0]
        .attributes['aria-pressed']
    ).toBe('false');
    expect(
      fixture.debugElement.queryAll(By.css('[app-button-group] button'))[1]
        .attributes['aria-pressed']
    ).toBe('true');
  });

  it('fetches weight meausrements with week period', async () => {
    const { fixture, weightSubject, mockWeightService } = await setup();
    weightSubject.next([]);
    fixture.detectChanges();
    expect(mockWeightService.getWeight).toHaveBeenCalledWith(7);
  });

  it('fetches weight meausrements with month period', async () => {
    const { fixture, weightSubject, mockWeightService } = await setup();
    weightSubject.next([]);
    fixture.detectChanges();
    mockWeightService.getWeight.calls.reset();
    fixture.debugElement
      .queryAll(By.css('[app-button-group] button'))[1]
      .triggerEventHandler('click');
    expect(mockWeightService.getWeight).toHaveBeenCalledWith(30);
  });

  it('fetches weight meausrements with year period', async () => {
    const { fixture, weightSubject, mockWeightService } = await setup();
    weightSubject.next([]);
    fixture.detectChanges();
    mockWeightService.getWeight.calls.reset();
    fixture.debugElement
      .queryAll(By.css('[app-button-group] button'))[2]
      .triggerEventHandler('click');
    expect(mockWeightService.getWeight).toHaveBeenCalledWith(365);
  });

  it('fetches weight meausrements with all time period', async () => {
    const { fixture, weightSubject, mockWeightService } = await setup();
    weightSubject.next([]);
    fixture.detectChanges();
    mockWeightService.getWeight.calls.reset();
    fixture.debugElement
      .queryAll(By.css('[app-button-group] button'))[3]
      .triggerEventHandler('click');
    expect(mockWeightService.getWeight).toHaveBeenCalledWith(undefined);
  });

  it('renders weight chart', async () => {
    const { fixture, weightSubject } = await setup();
    const measurements: WeightMeasurement[] = [
      { date: new Date('2020-05-05T00:00:00.000Z'), weight: 108.9 },
      { date: new Date('2020-05-07T00:00:00.000Z'), weight: 108.3 },
      { date: new Date('2020-05-10T00:00:00.000Z'), weight: 107.8 },
    ];
    weightSubject.next(measurements);
    fixture.detectChanges();
    expect(
      fixture.debugElement
        .query(By.directive(MockECharts))
        .injector.get(MockECharts).options?.dataset
    ).toEqual({
      source: [
        ['date', 'weight'],
        [measurements[0].date, measurements[0].weight],
        [measurements[1].date, measurements[1].weight],
        [measurements[2].date, measurements[2].weight],
      ],
    });
  });
});

import { TestBed } from '@angular/core/testing';

import { Directive, Input } from '@angular/core';
import { By } from '@angular/platform-browser';
import { EChartsOption } from 'echarts';
import { NgxEchartsDirective, NgxEchartsModule } from 'ngx-echarts';
import { Subject } from 'rxjs';
import { NotificationService } from '../common-components/notification.service';
import { WeightMeasurement, WeightService } from '../services/weight.service';
import { WeightComponent } from './weight.component';

@Directive({
  standalone: true,
  selector: '[echarts]',
})
class MockECharts {
  @Input()
  options?: EChartsOption;

  @Input()
  initOpts?: NgxEchartsDirective['initOpts'];
}

async function setup({ period }: { period?: number } = {}) {
  const weightMeasurementSubject = new Subject<WeightMeasurement[]>();
  const mockWeightService: jasmine.SpyObj<WeightService> = jasmine.createSpyObj(
    ['getWeight', 'getTodayWeight', 'getDiff']
  );
  mockWeightService.getWeight.and.returnValue(
    weightMeasurementSubject.asObservable()
  );
  const mockNotificationService: jasmine.SpyObj<NotificationService> =
    jasmine.createSpyObj(['showNotification']);
  await TestBed.configureTestingModule({
    providers: [
      { provide: WeightService, useValue: mockWeightService },
      { provide: NotificationService, useValue: mockNotificationService },
    ],
  }).compileComponents();

  TestBed.overrideComponent(WeightComponent, {
    remove: {
      imports: [NgxEchartsModule],
    },
    add: {
      imports: [MockECharts],
    },
  });

  const fixture = TestBed.createComponent(WeightComponent);
  fixture.componentInstance.period = period;
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
    mockWeightService.getTodayWeight.and.returnValue({
      date: new Date(),
      weight: 108.9,
      fatMassWeight: 21.3,
      fatRatio: 31,
    });
    fixture.detectChanges();
    const valueElements = element.querySelectorAll('h2 + *');
    expect(valueElements[0].textContent?.trim()).toEqual('108.9 kg');
    expect(valueElements[1].textContent?.trim()).toEqual('21.3 kg');
    expect(valueElements[2].textContent?.trim()).toEqual('31 %');
  });

  it('renders - if no weight is returned', async () => {
    const { element, fixture, weightSubject, mockWeightService } =
      await setup();
    weightSubject.next([]);
    mockWeightService.getTodayWeight.and.returnValue(undefined);
    fixture.detectChanges();
    const valueElements = element.querySelectorAll('h2 + *');
    expect(valueElements[0].textContent?.trim()).toEqual('-');
    expect(valueElements[1].textContent?.trim()).toEqual('-');
    expect(valueElements[2].textContent?.trim()).toEqual('-');
  });

  it('renders weight diff', async () => {
    const { element, fixture, weightSubject, mockWeightService } =
      await setup();
    weightSubject.next([]);
    mockWeightService.getDiff.and.returnValue({
      date: new Date(),
      weight: -0.00132,
      fatMassWeight: 0.05,
      fatRatio: 0.07894,
    });
    fixture.detectChanges();
    const valueElements = element.querySelectorAll('h2 + * + *');
    expect(valueElements[0].textContent?.trim()).toEqual('↓ 0.1 %');
    expect(valueElements[1].textContent?.trim()).toEqual('↑ 5 %');
    expect(valueElements[2].textContent?.trim()).toEqual('↑ 7.9 %');
  });

  it('renders - if no weight diff is returned', async () => {
    const { element, fixture, weightSubject, mockWeightService } =
      await setup();
    weightSubject.next([]);
    mockWeightService.getDiff.and.returnValue(undefined);
    fixture.detectChanges();
    const valueElements = element.querySelectorAll('h2 + * + *');
    expect(valueElements[0].textContent?.trim()).toEqual('-');
    expect(valueElements[1].textContent?.trim()).toEqual('-');
    expect(valueElements[2].textContent?.trim()).toEqual('-');
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

  it('fetches weight meausrements with week period', async () => {
    const { fixture, weightSubject, mockWeightService } = await setup({
      period: 7,
    });
    weightSubject.next([]);
    fixture.detectChanges();
    expect(mockWeightService.getWeight).toHaveBeenCalledWith(7);
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

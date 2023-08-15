import { TestBed } from '@angular/core/testing';

import { Subject } from 'rxjs';
import { CommonComponentsModule } from '../common-components/common-components.module';
import { NotificationService } from '../common-components/notification.service';
import { WeightMeasurement, WeightService } from '../weight.service';
import { WeightComponent } from './weight.component';
import { Directive, Input } from '@angular/core';
import { EChartsOption } from 'echarts';
import { NgxEchartsDirective } from 'ngx-echarts';

@Directive({
  selector: '[echarts]'
})
class MockECharts {
  @Input()
  options?: EChartsOption

  @Input()
  initOpts?: NgxEchartsDirective['initOpts']
}

async function setup() {
  const weightMeasurementSubject = new Subject<WeightMeasurement[]>();
  const mockWeightService: jasmine.SpyObj<WeightService> = jasmine.createSpyObj(
    ['getWeight']
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
    imports: [CommonComponentsModule],
  }).compileComponents();

  const fixture = TestBed.createComponent(WeightComponent);
  fixture.detectChanges();

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement,
    mockNotificationService,
    weightSubject: weightMeasurementSubject,
  };
}

describe('WeightComponent', () => {
  it('renders loading state', async () => {
    const { element } = await setup();
    expect(element.querySelector('[aria-busy="true"]')).toBeDefined();
  });

  it('renders weight', async () => {
    const { element, fixture, weightSubject } = await setup();
    weightSubject.next([
      {
        date: new Date('2020-05-05T00:00:00.000Z'),
        weight: 108.9,
      },
    ]);
    fixture.detectChanges();
    expect(element.querySelector('[role="status"]')?.textContent).toEqual('108.9');
  });

  it('renders question mark if no weight is returned', async () => {
    const { element, fixture, weightSubject } = await setup();
    weightSubject.next([]);
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
});

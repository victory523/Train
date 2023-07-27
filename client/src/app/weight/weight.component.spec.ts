import { TestBed } from '@angular/core/testing';

import { Subject } from 'rxjs';
import { CommonComponentsModule } from '../common-components/common-components.module';
import { NotificationService } from '../common-components/notification.service';
import { WeightService } from '../weight.service';
import { WeightComponent } from './weight.component';

async function setup() {
  const weightSubject = new Subject<number | undefined>();
  const mockWeightService: jasmine.SpyObj<WeightService> = jasmine.createSpyObj(
    ['getWeight']
  );
  mockWeightService.getWeight.and.returnValue(weightSubject.asObservable());
  const mockNotificationService: jasmine.SpyObj<NotificationService> =
    jasmine.createSpyObj(['showNotification']);
  await TestBed.configureTestingModule({
    declarations: [WeightComponent],
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
    weightSubject,
  };
}

describe('WeightComponent', () => {
  it('renders loading state', async () => {
    const { element } = await setup();
    expect(element.querySelector('app-loader')).toBeDefined();
  });

  it('renders weight', async () => {
    const { element, fixture, weightSubject } = await setup();
    weightSubject.next(87.6);
    fixture.detectChanges();
    expect(element.querySelector('app-badge')?.textContent).toEqual('87.6');
  });

  it('renders question mark if no weight is returned', async () => {
    const { element, fixture, weightSubject } = await setup();
    weightSubject.next(undefined);
    fixture.detectChanges();
    expect(element.querySelector('app-badge')?.textContent).toEqual('-');
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

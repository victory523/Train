import { TestBed } from '@angular/core/testing';

import { Observable, of, throwError } from 'rxjs';
import { BadgeComponent } from '../common-components/badge/badge.component';
import { HeadingComponent } from '../common-components/heading/heading.component';
import { LoaderComponent } from '../common-components/loader/loader.component';
import { NotificationService } from '../common-components/notification.service';
import { WeightService } from '../weight.service';
import { WeightComponent } from './weight.component';

async function setup(
  { $getWeight }: { $getWeight?: Observable<number | undefined> } = {
    $getWeight: of(),
  }
) {
  const mockWeightService = jasmine.createSpyObj('WeightService', {
    getWeight: $getWeight,
  });
  const showNotification = jasmine.createSpy();
  const mockNotificationService = jasmine.createSpyObj('NotificationService', {
    showNotification,
  });
  await TestBed.configureTestingModule({
    declarations: [
      WeightComponent,
      LoaderComponent,
      HeadingComponent,
      BadgeComponent,
    ],
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
  };
}

describe('WeightComponent', () => {
  it('renders loading state', async () => {
    const { element } = await setup();
    expect(element.querySelector('app-loader')).toBeDefined();
  });

  it('renders weight', async () => {
    const { element } = await setup({ $getWeight: of(87.6) });
    expect(element.textContent).toEqual(' Weight87.6');
  });

  it('renders question mark if no weight is returned', async () => {
    const { element } = await setup({ $getWeight: of(undefined) });
    expect(element.textContent).toEqual(' Weight?');
  });

  it('renders error state', async () => {
    const { mockNotificationService } = await setup({
      $getWeight: throwError(() => {}),
    });
    expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
      'Unable to fetch weight',
      'error'
    );
  });
});

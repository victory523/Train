import { TestBed } from '@angular/core/testing';

import { Observable, of, throwError } from 'rxjs';
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
  await TestBed.configureTestingModule({
    declarations: [WeightComponent],
    providers: [{ provide: WeightService, useValue: mockWeightService }],
  }).compileComponents();

  const fixture = TestBed.createComponent(WeightComponent);
  fixture.detectChanges();

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement,
  };
}

describe('WeightComponent', () => {
  it('renders loading state', async () => {
    const { element } = await setup();
    expect(element.textContent).toEqual('Loading...');
  });

  it('renders weight', async () => {
    const { element } = await setup({ $getWeight: of(87.6) });
    expect(element.textContent).toEqual('87.6');
  });

  it('renders question mark if no weight is returned', async () => {
    const { element } = await setup({ $getWeight: of(undefined) });
    expect(element.textContent).toEqual('?');
  });

  it('renders error state', async () => {
    const { element } = await setup({ $getWeight: throwError(() => {}) });
    expect(element.textContent).toEqual('Error occured');
  });
});

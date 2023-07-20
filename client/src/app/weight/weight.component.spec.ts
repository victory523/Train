import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WeightComponent } from './weight.component';
import { WeightService } from '../weight.service';
import { of } from 'rxjs';

async function setup() {
  const mockWeightService = jasmine.createSpyObj('WeightService', {
    getWeight: of(),
  });
  await TestBed.configureTestingModule({
    declarations: [WeightComponent],
    providers: [{ provide: WeightService, useValue: mockWeightService }],
  }).compileComponents();

  const fixture = TestBed.createComponent(WeightComponent);
  fixture.detectChanges();

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement
  };
}

describe('WeightComponent', () => {
  it('should create', async () => {
    const { element } = await setup();
    expect(element.textContent).toEqual('Loading...')
  });
});

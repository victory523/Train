import { TestBed } from '@angular/core/testing';

import { Component, Input } from '@angular/core';
import { WeightComponent } from '../weight/weight.component';
import { HomeComponent } from './home.component';
import { By } from '@angular/platform-browser';
import { RideComponent } from '../ride/ride.component';

@Component({
  standalone: true,
  selector: 'app-weight',
  template: 'weight'
})
class MockWeightComponent {}

@Component({
  standalone: true,
  selector: 'app-ride',
  template: 'ride'
})
class MockRideComponent {}

async function setup() {
  TestBed.configureTestingModule({
    imports: [HomeComponent],
  });
  TestBed.overrideComponent(HomeComponent, {
    remove: {
      imports: [WeightComponent, RideComponent],
    },
    add: {
      imports: [MockWeightComponent, MockRideComponent],
    },
  });
  const fixture = TestBed.createComponent(HomeComponent);

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement,
  };
}

describe('HomeComponent', () => {
  it('renders weight', async () => {
    const { element } = await setup();
    expect(element.textContent).toContain('weight')
  });

  it('renders ride with passed period', async () => {
    const { element } = await setup();
    expect(element.textContent).toContain('ride')
  });
});

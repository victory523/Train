import { TestBed } from '@angular/core/testing';

import { Component, Input } from '@angular/core';
import { WeightComponent } from '../weight/weight.component';
import { HomeComponent } from './home.component';
import { By } from '@angular/platform-browser';
import { RideComponent } from '../ride/ride.component';

@Component({
  standalone: true,
  selector: 'app-weight',
})
class MockWeightComponent {
  @Input()
  period: number | undefined;
}

@Component({
  standalone: true,
  selector: 'app-ride',
})
class MockRideComponent {
  @Input()
  period: number | undefined;
}

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
  it('renders weight with passed period', async () => {
    const { fixture } = await setup();
    fixture.componentInstance.period = 7;
    fixture.detectChanges();
    expect(
      fixture.debugElement.query(By.directive(MockWeightComponent))
        .componentInstance.period
    ).toBe(7);
  });

  it('renders ride with passed period', async () => {
    const { fixture } = await setup();
    fixture.componentInstance.period = 7;
    fixture.detectChanges();
    expect(
      fixture.debugElement.query(By.directive(MockRideComponent))
        .componentInstance.period
    ).toBe(7);
  });
});

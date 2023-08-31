import { TestBed } from '@angular/core/testing';

import { Component } from '@angular/core';
import { WeightComponent } from '../weight/weight.component';
import { HomeComponent } from './home.component';

@Component({
  standalone: true,
  selector: 'app-weight',
  template: '87.6',
})
class MockWeightComponent {}

async function setup() {
  TestBed.configureTestingModule({
    imports: [HomeComponent],
  });
  TestBed.overrideComponent(HomeComponent, {
    remove: {
      imports: [WeightComponent],
    },
    add: {
      imports: [MockWeightComponent],
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
    const { element, fixture } = await setup();
    fixture.detectChanges();
    expect(element?.textContent).toEqual('87.6');
  });
});

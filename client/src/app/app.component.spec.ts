import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Observable, of, throwError } from 'rxjs';
import { AppComponent } from './app.component';
import { WithingsService } from './withings.service';

@Component({
  selector: 'app-weight',
  template: '87.6',
})
class MockWeightComponent {}

async function setup({ $sync }: { $sync: Observable<void> } = { $sync: of() }) {
  const mockWithingsService = jasmine.createSpyObj('WithingsService', {
    sync: $sync,
  });
  await TestBed.configureTestingModule({
    declarations: [AppComponent, MockWeightComponent],
    providers: [{ provide: WithingsService, useValue: mockWithingsService }],
  }).compileComponents();

  const fixture = TestBed.createComponent(AppComponent);
  fixture.detectChanges();

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement,
  };
}

describe('AppComponent', () => {
  it('should render loading state', async () => {
    const { element } = await setup();
    expect(element.textContent).toEqual('Loading...');
  });

  it('renders weight', async () => {
    const { element } = await setup({ $sync: of(undefined) });
    expect(element.textContent).toEqual('87.6');
  });

  it('renders error state', async () => {
    const { element } = await setup({ $sync: throwError(() => {}) });
    expect(element.textContent).toEqual('Error occured');
  });
});

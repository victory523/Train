import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AppComponent } from './app.component';
import { WithingsService } from './withings.service';

async function setup() {
  const mockWithingsService = jasmine.createSpyObj('WithingsService', {
    sync: of(),
  });
  await TestBed.configureTestingModule({
    declarations: [AppComponent],
    providers: [{ provide: WithingsService, useValue: mockWithingsService }],
  }).compileComponents();

  const fixture = TestBed.createComponent(AppComponent);
  fixture.detectChanges();

  return {
    fixture,
    element: fixture.nativeElement as HTMLElement
  };
}

describe('AppComponent', () => {
  it('should render loading state', async () => {
    const { element } = await setup();
    expect(element.textContent).toEqual('Loading...');
  });
});

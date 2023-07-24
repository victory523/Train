import { TestBed } from '@angular/core/testing';

import { HeadingComponent } from '../heading/heading.component';
import { HeaderComponent } from './header.component';

async function setup() {
  await TestBed.configureTestingModule({
    declarations: [HeaderComponent, HeadingComponent],
  }).compileComponents();

  const fixture = TestBed.createComponent(HeaderComponent);

  return {
    fixture,
    component: fixture.componentInstance,
    element: fixture.nativeElement as HTMLElement,
  };
}

describe('HeaderComponent', () => {
  it('render title', async () => {
    const { fixture, component, element } = await setup();
    component.title = 'test title';
    fixture.detectChanges();
    expect(element.querySelector('app-heading')?.textContent).toBe(
      'test title'
    );
  });
});

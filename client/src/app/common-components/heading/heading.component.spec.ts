import { TestBed } from '@angular/core/testing';

import { HeadingComponent } from './heading.component';

async function setup() {
  await TestBed.configureTestingModule({
    declarations: [HeadingComponent],
  }).compileComponents();

  const fixture = TestBed.createComponent(HeadingComponent);

  return {
    fixture,
    component: fixture.componentInstance,
    element: fixture.nativeElement as HTMLElement,
  };
}

describe('HeadingComponent', () => {
  it('defaults to level 1', async () => {
    const { element, fixture } = await setup();
    fixture.detectChanges();
    expect(element.className).toContain('level1');
  });

  [1, 2, 3, 4, 5, 6].forEach((level) => {
    it(`renderes level ${level}`, async () => {
      const { component, element, fixture } = await setup();
      component.level = level;
      fixture.detectChanges();
      expect(element.className).toContain(`level${level}`);
    });
  });
});

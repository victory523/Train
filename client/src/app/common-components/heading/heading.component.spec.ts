import { TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { HeadingComponent } from './heading.component';

async function setup({
  template,
}: {
  template?: string;
} = {}) {
  @Component({
    template,
  })
  class TestComponent {}

  await TestBed.configureTestingModule({
    declarations: [TestComponent, HeadingComponent],
  }).compileComponents();

  const fixture = TestBed.createComponent(TestComponent);
  const debugElement = fixture.debugElement.children[0];
  fixture.detectChanges();

  return {
    fixture,
    nativeElement: debugElement.nativeElement as HTMLElement,
  };
}

describe('HeadingComponent', () => {
  it('renders content', async () => {
    const { nativeElement } = await setup({ template: '<app-heading>test text</app-heading>'});
    expect(nativeElement.textContent).toContain('test text');
  });

  it('defaults to level 1', async () => {
    const { nativeElement } = await setup({ template: '<app-heading></app-heading>'});
    expect(nativeElement.className).toContain('level1');
  });

  [1, 2, 3, 4, 5, 6].forEach((level) => {
    it(`renderes level ${level}`, async () => {
      const { nativeElement } = await setup({ template: `<app-heading level="${level}"></app-heading>`});
      expect(nativeElement.className).toContain(`level${level}`);
    });
  });
});

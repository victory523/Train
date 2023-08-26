import { TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { HeadingComponent } from './heading.component';

async function setup({
  template,
}: {
  template?: string;
} = {}) {
  @Component({
    standalone: true,
    imports: [HeadingComponent],
    template,
  })
  class TestComponent {}

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
    const { nativeElement } = await setup({ template: '<h1 app-heading>test text</h1>'});
    expect(nativeElement.textContent).toContain('test text');
  });
});

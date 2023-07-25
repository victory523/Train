import { TestBed } from '@angular/core/testing';

import { Component } from '@angular/core';
import { MainComponent } from './main.component';

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
    declarations: [MainComponent, TestComponent],
  }).compileComponents();

  const fixture = TestBed.createComponent(TestComponent);
  const debugElement = fixture.debugElement.children[0];
  fixture.detectChanges();

  return {
    fixture,
    nativeElement: debugElement.nativeElement as HTMLElement,
  };
}

describe('MainComponent', () => {
  it('renders content', async () => {
    const { nativeElement } = await setup({
      template: '<app-main>test text</app-main>',
    });
    expect(nativeElement.textContent).toBe('test text');
  });
});

import { TestBed } from '@angular/core/testing';

import { Component } from '@angular/core';
import { HeaderMenuComponent } from './header-menu.component';

async function setup({
  template,
}: {
  template?: string;
} = {}) {
  @Component({
    standalone: true,
    imports: [HeaderMenuComponent],
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

describe('HeaderMenuComponent', () => {
  it('render content', async () => {
    const { nativeElement } = await setup({
      template: '<a app-header-menu>test text</a>',
    });
    expect(nativeElement.textContent).toBe('test text');
  });
});

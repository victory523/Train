import { TestBed } from '@angular/core/testing';

import { HeadingComponent } from '../heading/heading.component';
import { HeaderComponent } from './header.component';
import { Component } from '@angular/core';
import { HeaderMenuComponent } from '../header-menu/header-menu.component';

async function setup({
  template,
}: {
  template?: string;
} = {}) {
  @Component({
    standalone: true,
    imports: [HeaderComponent, HeadingComponent, HeaderMenuComponent],
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

describe('HeaderComponent', () => {
  it('render content', async () => {
    const { nativeElement } = await setup({
      template: '<header app-header>test text</header>',
    });
    expect(nativeElement.textContent).toBe('test text');
  });
});

import { TestBed } from '@angular/core/testing';

import { Component } from '@angular/core';
import { HeaderMenuComponent } from './header-menu.component';

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
    declarations: [HeaderMenuComponent, TestComponent],
  }).compileComponents();

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
      template: '<app-header-menu>test text</app-header-menu>',
    });
    expect(nativeElement.textContent).toBe('test text');
  });

  it('renders href', async () => {
    const { nativeElement } = await setup({
      template: '<app-header-menu href="/test/url"></app-header-menu>',
    });
    expect(nativeElement.getAttribute('href')).toBe('/test/url');
  });
});

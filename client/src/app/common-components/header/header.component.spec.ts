import { TestBed } from '@angular/core/testing';

import { HeadingComponent } from '../heading/heading.component';
import { HeaderComponent } from './header.component';
import { Component } from '@angular/core';

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
    declarations: [HeaderComponent, TestComponent, HeadingComponent],
  }).compileComponents();

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
      template: '<app-header>test text</app-header>',
    });
    expect(nativeElement.textContent).toBe('test text');
  });

  it('render title', async () => {
    const { nativeElement } = await setup({
      template: '<app-header title="test title"></app-header>',
    });
    expect(nativeElement.querySelector('app-heading')?.textContent).toBe(
      'test title'
    );
  });
});

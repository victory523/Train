import { TestBed } from '@angular/core/testing';

import { Component } from '@angular/core';
import { BadgeComponent } from './badge.component';

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
    declarations: [BadgeComponent, TestComponent],
  }).compileComponents();

  const fixture = TestBed.createComponent(TestComponent);
  const debugElement = fixture.debugElement.children[0];
  fixture.detectChanges();

  return {
    fixture,
    nativeElement: debugElement.nativeElement as HTMLElement,
  };
}

describe('BadgeComponent', () => {
  it('renders content', async () => {
    const { nativeElement } = await setup({
      template: '<span app-badge>test text</span>',
    });
    expect(nativeElement.textContent).toBe('test text');
  });
});

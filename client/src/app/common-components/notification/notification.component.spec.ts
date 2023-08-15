import { TestBed } from '@angular/core/testing';

import { Component } from '@angular/core';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { NotificationComponent } from './notification.component';

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
    declarations: [NotificationComponent, TestComponent],
    imports: [NoopAnimationsModule],
  }).compileComponents();

  const fixture = TestBed.createComponent(TestComponent);
  const debugElement = fixture.debugElement.children[0];
  fixture.detectChanges();

  return {
    fixture,
    nativeElement: debugElement.nativeElement as HTMLElement,
  };
}

describe('NotificationComponent', () => {
  it('renders content', async () => {
    const { nativeElement } = await setup({
      template: '<li app-notification>Test notification</li>',
    });
    expect(nativeElement.textContent).toBe('Test notification');
  });

  it('defaults to success type', async () => {
    const { nativeElement } = await setup({
      template: '<li app-notification></li>',
    });

    expect(nativeElement.classList.contains('success')).toBe(true);
  });

  it('renders success type', async () => {
    const { nativeElement } = await setup({
      template: '<li app-notification type="success"></li>',
    });

    expect(nativeElement.classList.contains('success')).toBe(true);
  });

  it('renders error type', async () => {
    const { nativeElement } = await setup({
      template: '<li app-notification type="error"></li>',
    });

    expect(nativeElement.classList.contains('error')).toBe(true);
  });
});

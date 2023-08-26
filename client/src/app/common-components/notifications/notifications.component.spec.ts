import { TestBed } from '@angular/core/testing';

import { Component } from '@angular/core';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { NotificationService } from '../notification.service';
import { NotificationsComponent } from './notifications.component';

async function setup({
  template,
}: {
  template?: string;
} = {}) {
  @Component({
    standalone: true,
    imports: [NotificationsComponent],
    template,
  })
  class TestComponent {}

  await TestBed.configureTestingModule({
    providers: [NotificationService, provideNoopAnimations()],
  }).compileComponents();

  const fixture = TestBed.createComponent(TestComponent);
  const debugElement = fixture.debugElement.children[0];
  fixture.detectChanges();

  const notificationService = TestBed.inject(NotificationService);

  return {
    fixture,
    nativeElement: debugElement.nativeElement as HTMLElement,
    notificationService,
  };
}

describe('NotificationsComponent', () => {
  it('should render notifications added by notification service', async () => {
    const { nativeElement, fixture, notificationService } = await setup({
      template: '<ul app-notifications></ul>',
    });
    notificationService.showNotification('test notification');
    fixture.detectChanges();
    const notifications = Array.from(
      nativeElement.querySelectorAll('li[role="alert"]')
    );
    expect(notifications).toHaveSize(1);
    expect(notifications[0].textContent?.trim()).toBe('test notification');
  });

  it('should render multiple notifications added by notification service', async () => {
    const { nativeElement, fixture, notificationService } = await setup({
      template: '<ul app-notifications></ul>',
    });
    notificationService.showNotification('test notification 1');
    notificationService.showNotification('test notification 2', 'error');
    notificationService.showNotification('test notification 3', 'success');
    notificationService.showNotification('test notification 4', 'error');
    fixture.detectChanges();
    const notifications = Array.from(
      nativeElement.querySelectorAll('li[role="alert"]')
    );
    expect(notifications).toHaveSize(4);
    expect(notifications[0].textContent?.trim()).toBe('test notification 1');
    expect(notifications[0].classList.contains('success')).toBe(true);
    expect(notifications[1].textContent?.trim()).toBe('test notification 2');
    expect(notifications[1].classList.contains('error')).toBe(true);
    expect(notifications[2].textContent?.trim()).toBe('test notification 3');
    expect(notifications[2].classList.contains('success')).toBe(true);
    expect(notifications[3].textContent?.trim()).toBe('test notification 4');
    expect(notifications[3].classList.contains('error')).toBe(true);
  });
});

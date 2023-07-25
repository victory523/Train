import { TestBed } from '@angular/core/testing';

import { Notification, NotificationService } from './notification.service';

function setup() {
  TestBed.configureTestingModule({});

  const service = TestBed.inject(NotificationService);
  return { service };
}

describe('NotificationService', () => {
  it('stores notifications', () => {
    const notifications: Notification[] = [];
    const { service } = setup();
    service.$notifications.subscribe((notification) =>
      notifications.push(notification)
    );
    service.showNotification('test notification');
    expect(notifications).toHaveSize(1);
    expect(notifications[0]).toEqual({
      type: 'success',
      message: 'test notification',
    });
  });

  it('stores multiple notifications', () => {
    const notifications: Notification[] = [];
    const { service } = setup();
    service.$notifications.subscribe((notification) =>
      notifications.push(notification)
    );
    service.showNotification('test notification 1');
    service.showNotification('test notification 2', 'error');
    service.showNotification('test notification 3', 'success');
    service.showNotification('test notification 4', 'error');
    expect(notifications).toHaveSize(4);
    expect(notifications[0]).toEqual({
      type: 'success',
      message: 'test notification 1',
    });
    expect(notifications[1]).toEqual({
      type: 'error',
      message: 'test notification 2',
    });
    expect(notifications[2]).toEqual({
      type: 'success',
      message: 'test notification 3',
    });
    expect(notifications[3]).toEqual({
      type: 'error',
      message: 'test notification 4',
    });
  });
});

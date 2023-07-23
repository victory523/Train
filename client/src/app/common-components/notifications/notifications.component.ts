import { AfterViewInit, Component, ElementRef } from '@angular/core';
import { Notification, NotificationService } from '../notification.service';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css'],
})
export class NotificationsComponent implements AfterViewInit {
  notifications: Notification[] = [];
  #height = 0;

  constructor(
    private elementRef: ElementRef,
    private notificationService: NotificationService
  ) {}

  ngAfterViewInit(): void {
    const element = this.elementRef.nativeElement;
    const observer = new ResizeObserver((entries) => {
      entries.forEach((entry) => {
        const initialOffset = this.#height - entry.contentRect.height;
        this.#height = entry.contentRect.height;
        if (initialOffset < 0) {
          element.animate(
            [
              { transform: `translateY(${initialOffset}px)` },
              { transform: 'translateY(0)' },
            ],
            {
              duration: 150,
              easing: 'ease-out',
            }
          );
        }
      });
    });

    observer.observe(element);

    this.notificationService.$notifications.subscribe((notification) =>
      this.addNotification(notification)
    );
  }

  addNotification(newNotification: Notification) {
    this.notifications = [...this.notifications, newNotification];
  }

  removeNotification(notificationToRemove: Notification) {
    this.notifications = this.notifications.filter(
      (notification) => notification !== notificationToRemove
    );
  }
}

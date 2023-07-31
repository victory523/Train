import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export type NotificationType = 'success' | 'error'

export type Notification = {
  type: NotificationType,
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  #notifications = new Subject<Notification>()
  $notifications = this.#notifications.asObservable();

  showNotification(message: string, type: NotificationType = 'success') {
    this.#notifications.next({ type, message });
  }
}

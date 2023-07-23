import {
  animate,
  sequence,
  style,
  transition,
  trigger
} from '@angular/animations';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NotificationType } from '../notification.service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css'],
  animations: [
    trigger('toast', [
      transition(':enter', [
        sequence([
          style({
            opacity: 0,
            transform: 'translateY(-5vh)',
          }),
          animate(
            '0.3s ease',
            style({
              opacity: 1,
              transform: 'none',
            })
          ),
          animate(
            '0.3s 3s ease',
            style({
              opacity: 0,
            })
          ),
        ]),
      ]),
    ]),
  ],
  host: {
    '[class]': 'type',
    '[@toast]': 'toastState',
    '(@toast.done)': 'animationEnd($event)',
  },
})
export class NotificationComponent {
  @Input() type: NotificationType = 'success';
  @Output() settled = new EventEmitter();

  animationEnd(_event: AnimationEvent) {
    this.settled.emit();
  }
}

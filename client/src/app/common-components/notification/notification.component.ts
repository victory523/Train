import {
  animate,
  sequence,
  style,
  transition,
  trigger,
} from '@angular/animations';
import {
  Attribute,
  Component,
  EventEmitter,
  HostBinding,
  HostListener,
  Input,
  Output,
} from '@angular/core';
import { NotificationType } from '../notification.service';

@Component({
  selector: '[app-notification]',
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
})
export class NotificationComponent {
  @Input() type: NotificationType = 'success';
  @Output() settled = new EventEmitter();
  @HostBinding('@toast') toastState = null;

  @HostBinding('class')
  get class() {
    return this.type;
  }

  @HostListener('@toast.done')
  animationEnd(_event: AnimationEvent) {
    this.settled.emit();
  }
}

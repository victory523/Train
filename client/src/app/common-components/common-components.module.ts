import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { BadgeComponent } from './badge/badge.component';
import { HeaderComponent } from './header/header.component';
import { HeadingComponent } from './heading/heading.component';
import { LoaderComponent } from './loader/loader.component';
import { MainComponent } from './main/main.component';
import { NotificationComponent } from './notification/notification.component';
import { NotificationsComponent } from './notifications/notifications.component';
import { HeaderMenuComponent } from './header-menu/header-menu.component';

@NgModule({
  declarations: [
    HeaderComponent,
    HeadingComponent,
    MainComponent,
    BadgeComponent,
    NotificationComponent,
    NotificationsComponent,
    LoaderComponent,
    HeaderMenuComponent,
  ],
  imports: [CommonModule],
  exports: [
    HeaderComponent,
    HeadingComponent,
    MainComponent,
    BadgeComponent,
    NotificationComponent,
    NotificationsComponent,
    LoaderComponent,
    HeaderMenuComponent
  ],
})
export class CommonComponentsModule {}

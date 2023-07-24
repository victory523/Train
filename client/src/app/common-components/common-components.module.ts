import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header/header.component';
import { HeadingComponent } from './heading/heading.component';
import { MainComponent } from './main/main.component';
import { BadgeComponent } from './badge/badge.component';
import { NotificationComponent } from './notification/notification.component';
import { NotificationsComponent } from './notifications/notifications.component';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoaderComponent } from './loader/loader.component';

@NgModule({
  declarations: [
    HeaderComponent,
    HeadingComponent,
    MainComponent,
    BadgeComponent,
    NotificationComponent,
    NotificationsComponent,
    LoaderComponent,
  ],
  imports: [CommonModule, BrowserModule, BrowserAnimationsModule],
  exports: [
    HeaderComponent,
    HeadingComponent,
    MainComponent,
    BadgeComponent,
    NotificationComponent,
    NotificationsComponent,
    LoaderComponent
  ],
})
export class CommonComponentsModule {}

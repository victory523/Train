import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header/header.component';
import { HeadingComponent } from './heading/heading.component';
import { MainComponent } from './main/main.component';
import { BadgeComponent } from './badge/badge.component';



@NgModule({
  declarations: [
    HeaderComponent,
    HeadingComponent,
    MainComponent,
    BadgeComponent
  ],
  imports: [
    CommonModule
  ],
  exports: [
    HeaderComponent,
    HeadingComponent,
    MainComponent,
    BadgeComponent
  ]
})
export class CommonComponentsModule { }

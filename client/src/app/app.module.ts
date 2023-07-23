import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WeightComponent } from './weight/weight.component';
import { MainComponent } from './main/main.component';
import { HeaderComponent } from './header/header.component';
import { HeadingComponent } from './heading/heading.component';

@NgModule({
  declarations: [
    AppComponent,
    WeightComponent,
    MainComponent,
    HeaderComponent,
    HeadingComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [{
    provide: Location,
    useValue: window.location
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }

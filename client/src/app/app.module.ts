import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WeightComponent } from './weight/weight.component';

@NgModule({
  declarations: [
    AppComponent,
    WeightComponent
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

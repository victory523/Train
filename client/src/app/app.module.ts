import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WeightComponent } from './weight/weight.component';
import { CommonComponentsModule } from './common-components/common-components.module';

@NgModule({
  declarations: [
    AppComponent,
    WeightComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    CommonComponentsModule
  ],
  providers: [{
    provide: Location,
    useValue: window.location
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }

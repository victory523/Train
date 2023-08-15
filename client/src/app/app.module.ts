import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WeightComponent } from './weight/weight.component';
import { CommonComponentsModule } from './common-components/common-components.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RelativeTimePipe } from './utils/relative-time.pipe';
import { NgxEchartsModule } from 'ngx-echarts';

@NgModule({
  declarations: [
    AppComponent,
    WeightComponent,
    RelativeTimePipe,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    CommonComponentsModule,
    BrowserAnimationsModule,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts')
    })
  ],
  providers: [{ provide: Location, useValue: window.location }],
  bootstrap: [AppComponent]
})
export class AppModule { }

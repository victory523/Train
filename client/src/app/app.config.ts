import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';

import { HTTP_INTERCEPTORS, provideHttpClient } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { NGX_ECHARTS_CONFIG } from 'ngx-echarts';
import { routes } from './app.routes';
import { NotificationService } from './common-components/notification.service';
import { TimezoneInterceptor } from './http-interceptors/timezone-interceptor';
import { BackupService } from './services/backup.service';
import { WeightService } from './services/weight.service';
import { WithingsService } from './services/withings.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideAnimations(),
    provideRouter(routes),
    provideHttpClient(),
    { provide: Location, useValue: window.location },
    { provide: HTTP_INTERCEPTORS, useClass: TimezoneInterceptor, multi: true },
    {
      provide: NGX_ECHARTS_CONFIG,
      useFactory: () => ({ echarts: () => import('echarts') }),
    },
    WeightService,
    WithingsService,
    NotificationService,
    BackupService,
  ],
};

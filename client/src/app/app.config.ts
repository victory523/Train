import { ApplicationConfig, Provider } from '@angular/core';
import { provideRouter } from '@angular/router';

import { provideHttpClient } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { NGX_ECHARTS_CONFIG } from 'ngx-echarts';
import { routes } from './app.routes';
import { NotificationService } from './common-components/notification.service';
import { provideTimezoneInterceptor } from './http-interceptors/timezone-interceptor';
import { BackupService } from './services/backup.service';
import { WeightService } from './services/weight.service';
import { WithingsService } from './services/withings.service';
import * as echarts from 'echarts';

function provideECharts(): Provider {
  return {
    provide: NGX_ECHARTS_CONFIG,
    useFactory: () => ({ echarts }),
  };
}

function provideLocation(): Provider {
  return { provide: Location, useValue: window.location };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideAnimations(),
    provideRouter(routes),
    provideHttpClient(),
    provideLocation(),
    provideTimezoneInterceptor(),
    provideECharts(),
    WeightService,
    WithingsService,
    NotificationService,
    BackupService,
  ],
};

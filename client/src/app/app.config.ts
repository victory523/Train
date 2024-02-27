import { ApplicationConfig, Provider } from '@angular/core';
import { provideRouter } from '@angular/router';

import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import * as echarts from 'echarts';
import { NGX_ECHARTS_CONFIG } from 'ngx-echarts';
import { routes } from './app.routes';
import { NotificationService } from './common-components/notification.service';
import { oAuthLoginInterceptor } from './http-interceptors/oauth-login-interceptor';
import { timezoneInterceptor } from './http-interceptors/timezone-interceptor';
import { BackupService } from './backup/backup.service';
import { RideService } from './ride/ride.service';
import { StravaService } from './strava/strava.service';
import { WeightService } from './weight/weight.service';
import { WithingsService } from './withings/withings.service';
import { AuthService } from './auth/auth.service';

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
    provideHttpClient(
      withInterceptors([timezoneInterceptor, oAuthLoginInterceptor])
    ),
    provideLocation(),
    provideECharts(),
    AuthService,
    StravaService,
    RideService,
    WeightService,
    WithingsService,
    NotificationService,
    BackupService,
  ],
};

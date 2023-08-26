import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'week',
    pathMatch: 'full',
  },
  {
    path: 'week',
    component: HomeComponent,
    data: { period: 7 },
  },
  {
    path: 'month',
    component: HomeComponent,
    data: { period: 30 },
  }, {
    path: 'year',
    component: HomeComponent,
    data: { period: 365 },
  }, {
    path: 'all-time',
    component: HomeComponent,
  },
];

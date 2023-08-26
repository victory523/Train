import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';

export enum RouterTokens {
  WEEK = 'week',
  MONTH = 'month',
  YEAR = 'year',
  ALL_TIME = 'all-time',
}

export const routes: Routes = [
  {
    path: '',
    redirectTo: RouterTokens.WEEK,
    pathMatch: 'full',
  },
  {
    path: RouterTokens.WEEK,
    component: HomeComponent,
    data: { period: 7 },
  },
  {
    path: RouterTokens.MONTH,
    component: HomeComponent,
    data: { period: 30 },
  },
  {
    path: RouterTokens.YEAR,
    component: HomeComponent,
    data: { period: 365 },
  },
  {
    path: RouterTokens.ALL_TIME,
    component: HomeComponent,
  },
];

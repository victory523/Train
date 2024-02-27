import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { SigninComponent } from './auth/signin.component';
import { SigninRedirectCallbackComponent } from './auth/signin-redirect-callback.component';
import { hasRole } from './auth/hasRole';

export enum RouterTokens {
  SIGNIN = 'signin',
  SIGNIN_REDIRECT_CALLBACK = 'signin-redirect-callback',
  WEEK = '',
  MONTH = 'month',
  YEAR = 'year',
  ALL_TIME = 'all-time',
}

export const routes: Routes = [
  {
    path: RouterTokens.SIGNIN,
    component: SigninComponent,
  },
  {
    path: RouterTokens.SIGNIN_REDIRECT_CALLBACK,
    component: SigninRedirectCallbackComponent,
  },
  {
    path: RouterTokens.WEEK,
    component: HomeComponent,
    pathMatch: 'full',
    data: { period: 7 },
    canActivate: [() => hasRole('user')],
  },
  {
    path: RouterTokens.MONTH,
    component: HomeComponent,
    data: { period: 30 },
    canActivate: [() => hasRole('user')],
  },
  {
    path: RouterTokens.YEAR,
    component: HomeComponent,
    data: { period: 365 },
    canActivate: [() => hasRole('user')],
  },
  {
    path: RouterTokens.ALL_TIME,
    component: HomeComponent,
    canActivate: [() => hasRole('user')],
  },
];

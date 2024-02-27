import { delay, http, HttpResponse } from 'msw';

export const authMocks = [
  http.get('/auth/user-info', async () => {
    await delay(600);

    const isSignedIn = !!sessionStorage.getItem('signedIn');

    if (!isSignedIn) {
      return new HttpResponse(undefined, { status: 400 });
    }

    return HttpResponse.json({
      name: 'Igor',
      sub: '123',
      groups: ['user', 'admin'],
    });
  }),
  http.post('/auth/authorize', () =>
    HttpResponse.json({ authorizationUrl: '/signin-redirect-callback' })
  ),
  http.post('/auth/get-token', () => {
    sessionStorage.setItem('signedIn', 'true');
    return new HttpResponse();
  }),
  http.post('/auth/logout', () => {
    sessionStorage.removeItem('signedIn');
    return new HttpResponse();
  }),
];

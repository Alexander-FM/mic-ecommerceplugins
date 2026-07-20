export const environment = {
  production: false,
  apiUrl: 'http://127.0.0.1:80',
  oauth: {
    authorizationEndpoint: 'http://127.0.0.1:9001/oauth2/authorize',
    tokenUrl: 'http://127.0.0.1:9001/oauth2/token',
    redirectUri: 'http://localhost:4200/auth/callback',
    clientId: 'maintenance-spa'
  }
};

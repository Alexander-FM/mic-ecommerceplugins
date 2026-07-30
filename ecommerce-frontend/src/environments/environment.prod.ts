const BASE_URL = 'http://localhost:8080';
export const environment = {
  production: true,
  apiUrl: BASE_URL,
  oauth: {
    authorizationEndpoint: `${BASE_URL}/oauth2/authorize`,
    tokenUrl: `${BASE_URL}/oauth2/token`,
    logoutEndpoint: `${BASE_URL}/connect/logout`,
    redirectUri: 'http://localhost:4200/auth/callback',
    clientId: 'maintenance-spa'
  }
};
const BASE_URL = 'http://localhost:8080';
export const environment = {
  production: false,
  apiUrl: BASE_URL,
  oauth: {
    authorizationEndpoint: `http://localhost:9000/oauth2/authorize`,
    tokenUrl: `${BASE_URL}/oauth2/token`,
    logoutEndpoint: `http://localhost:9000/connect/logout`,
    redirectUri: 'http://localhost:4200/auth/callback',
    clientId: 'ecommerce-spa'
  }
};

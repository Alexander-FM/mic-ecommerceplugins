const BASE_URL = 'https://api-aromadeuva.codesoftperu.com';
export const environment = {
  production: true,
  apiUrl: BASE_URL,
  oauth: {
    authorizationEndpoint: `${BASE_URL}/oauth2/authorize`,
    tokenUrl: `${BASE_URL}/oauth2/token`,
    logoutEndpoint: `${BASE_URL}/connect/logout`,
    redirectUri: 'https://aromadeuva.codesoftperu.com/auth/callback',
    clientId: 'maintenance-spa'
  }
};
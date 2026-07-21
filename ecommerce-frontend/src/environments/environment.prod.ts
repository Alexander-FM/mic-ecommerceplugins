const BASE_URL = 'http://appmic-e-commerceplugins-gateway';
export const environment = {
  production: true,
  apiUrl: BASE_URL,
  oauth: {
    authorizationEndpoint: `${BASE_URL}/oauth2/authorize`,
    tokenUrl: `${BASE_URL}/oauth2/token`,
    redirectUri: 'http://ecommerce-frontend/auth/callback',
    clientId: 'maintenance-spa'
  }
};
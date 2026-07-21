const BASE_URL = 'https://gentleman-robbie-minimal-websites.trycloudflare.com';
export const environment = {
  production: true,
  apiUrl: BASE_URL, // Asumiendo que el port-forward del gateway es al puerto 8089
  oauth: {
    // Apuntamos al GATEWAY, no al microservicio interno
    authorizationEndpoint: `${BASE_URL}/oauth2/authorize`,
    tokenUrl: `${BASE_URL}/oauth2/token`,
    redirectUri: 'https://pack-colleges-camps-academic.trycloudflare.com/auth/callback',
    clientId: 'maintenance-spa'
  }
};
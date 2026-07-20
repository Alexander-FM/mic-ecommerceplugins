export const environment = {
  production: true,
  apiUrl: 'http://localhost:8089', // Asumiendo que el port-forward del gateway es al puerto 8089
  oauth: {
    // Apuntamos al GATEWAY, no al microservicio interno
    authorizationEndpoint: 'http://localhost:8089/oauth2/authorize',
    tokenUrl: 'http://localhost:8089/oauth2/token',
    redirectUri: 'http://localhost:4200/auth/callback',
    clientId: 'maintenance-spa'
  }
};
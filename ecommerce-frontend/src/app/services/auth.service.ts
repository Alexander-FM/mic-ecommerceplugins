import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { TokenResponse, AuthUser, AuthState } from '../models/auth.models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly AUTHORIZATION_ENDPOINT = environment.oauth.authorizationEndpoint;
  private readonly TOKEN_URL = environment.oauth.tokenUrl;
  private readonly REDIRECT_URI = environment.oauth.redirectUri;
  private readonly CLIENT_ID = environment.oauth.clientId;
  private readonly STORAGE_KEY_CODE_VERIFIER = 'pkce_code_verifier';
  private readonly STORAGE_KEY_STATE = 'pkce_state';
  private readonly STORAGE_KEY_TOKEN = 'auth_token';
  private readonly STORAGE_KEY_REFRESH = 'auth_refresh_token';
  private readonly STORAGE_KEY_ID = 'auth_id_token';
  private readonly STORAGE_KEY_EXPIRES = 'auth_expires_in';

  private authStateSubject = new BehaviorSubject<AuthState>(this.getInitialState());
  public authState$ = this.authStateSubject.asObservable();

  constructor(private http: HttpClient) {
    this.restoreAuthState();
  }

  private getInitialState(): AuthState {
    return {
      isAuthenticated: false,
      token: null,
      refreshToken: null,
      idToken: null,
      expiresIn: null,
      user: null
    };
  }

  /**
   * Inicia el flujo de autenticación OAuth 2.0
   */
  async initiateOAuthFlow(): Promise<void> {
    const codeVerifier = this.generateCodeVerifier();
    const codeChallenge = await this.generateCodeChallenge(codeVerifier);
    const state = this.generateRandomString(32);

    console.log('🔄 Iniciando flujo OAuth 2.0 PKCE...');
    console.log('🔑 Code Verifier generado (len=' + codeVerifier.length + '):', codeVerifier.substring(0, 20) + '...');
    console.log('🔐 Code Challenge (SHA-256, Base64URL):', codeChallenge);
    console.log('🛡️ State generado:', state);

    sessionStorage.setItem(this.STORAGE_KEY_CODE_VERIFIER, codeVerifier);
    sessionStorage.setItem(this.STORAGE_KEY_STATE, state);

    console.log('✅ PKCE data guardado en sessionStorage');

    const params = new URLSearchParams({
      response_type: 'code',
      client_id: this.CLIENT_ID,
      redirect_uri: this.REDIRECT_URI,
      scope: 'openid profile read write',
      code_challenge: codeChallenge,
      code_challenge_method: 'S256',
      state: state
    });

    const authUrl = `${this.AUTHORIZATION_ENDPOINT}?${params.toString()}`;
    console.log('🌐 Redirecting a:', authUrl);
    window.location.href = authUrl;
  }

  /**
   * Intercambia el código por un token (llamado desde el callback)
   */
  exchangeCodeForToken(code: string): Observable<TokenResponse> {
    const codeVerifier = sessionStorage.getItem(this.STORAGE_KEY_CODE_VERIFIER);
    if (!codeVerifier) {
      console.error('❌ PKCE code verifier not found in sessionStorage');
      throw new Error('PKCE code verifier not found. Restart login flow.');
    }

    console.log('🔑 Code Verifier length:', codeVerifier.length);
    console.log('🔑 Code Verifier (primeros 20 chars):', codeVerifier.substring(0, 20));

    const body = new URLSearchParams();
    body.set('code', code);
    body.set('grant_type', 'authorization_code');
    body.set('redirect_uri', this.REDIRECT_URI);
    body.set('client_id', this.CLIENT_ID);
    body.set('code_verifier', codeVerifier);

    console.log('🌐 POST a:', this.TOKEN_URL);
    console.log('📦 Parámetros enviados:');
    console.log('  - code:', code.substring(0, 20) + '...');
    console.log('  - grant_type:', 'authorization_code');
    console.log('  - redirect_uri:', this.REDIRECT_URI);
    console.log('  - client_id:', this.CLIENT_ID);
    console.log('  - code_verifier:', codeVerifier);

    return this.http.post<TokenResponse>(this.TOKEN_URL, body.toString(), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
  }

  /**
   * Procesa la respuesta del token y actualiza el estado
   */
  processTokenResponse(response: TokenResponse): void {
    console.log('🔄 Procesando token response...');

    // Guardar tokens en localStorage
    localStorage.setItem(this.STORAGE_KEY_TOKEN, response.access_token);
    localStorage.setItem(this.STORAGE_KEY_REFRESH, response.refresh_token);
    localStorage.setItem(this.STORAGE_KEY_ID, response.id_token);
    localStorage.setItem(this.STORAGE_KEY_EXPIRES, response.expires_in.toString());

    console.log('💾 Tokens guardados en localStorage');

    // Extraer información del token
    const user = this.decodeToken(response.access_token);

    // Actualizar el estado
    const newState: AuthState = {
      isAuthenticated: true,
      token: response.access_token,
      refreshToken: response.refresh_token,
      idToken: response.id_token,
      expiresIn: response.expires_in,
      user: user
    };

    console.log('📊 Nuevo estado:', newState);
    this.authStateSubject.next(newState);
    console.log('✅ Estado actualizado en authStateSubject');
  }

  /**
   * Restaura el estado de autenticación desde localStorage
   */
  private restoreAuthState(): void {
    console.log('🔄 Restaurando estado de autenticación desde localStorage...');

    const token = localStorage.getItem(this.STORAGE_KEY_TOKEN);
    const refreshToken = localStorage.getItem(this.STORAGE_KEY_REFRESH);
    const idToken = localStorage.getItem(this.STORAGE_KEY_ID);
    const expiresIn = localStorage.getItem(this.STORAGE_KEY_EXPIRES);

    if (token) {
      console.log('✅ Token encontrado en localStorage');
    } else {
      console.log('⚠️  No hay token en localStorage');
    }

    if (token && this.isTokenValid(token, expiresIn)) {
      const user = this.decodeToken(token);
      console.log('📝 Usuario desde token restaurado:', user);

      const state: AuthState = {
        isAuthenticated: true,
        token,
        refreshToken,
        idToken,
        expiresIn: expiresIn ? parseInt(expiresIn, 10) : null,
        user
      };

      this.authStateSubject.next(state);
      console.log('✅ Estado de autenticación restaurado correctamente');
    } else {
      console.log('❌ Token inválido o no encontrado, limpiando estado');
      this.logout();
    }
  }

  /**
   * Verifica si el token es válido
   */
  private isTokenValid(token: string, expiresIn: string | null): boolean {
    if (!token) {
      return false;
    }

    // Aquí podrías validar la expiración del token
    // Por ahora, asumimos que es válido si existe
    return true;
  }

  /**
   * Decodifica el JWT para extraer la información del usuario
   */
  private decodeToken(token: string): AuthUser | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        console.error('❌ Token format invalid: expected 3 parts, got', parts.length);
        return null;
      }

      const decoded = JSON.parse(atob(parts[1]));
      console.log('✅ Token decodificado correctamente:', decoded);
      console.log('ℹ️  Roles encontrados:', decoded.roles);
      console.log('ℹ️  Usuario (sub):', decoded.sub);
      return decoded;
    } catch (error) {
      console.error('❌ Error decoding token:', error);
      return null;
    }
  }

  private generateCodeVerifier(): string {
    return this.generateRandomString(64);
  }

  private async generateCodeChallenge(codeVerifier: string): Promise<string> {
    const data = new TextEncoder().encode(codeVerifier);
    const digest = await window.crypto.subtle.digest('SHA-256', data);
    return this.base64UrlEncode(new Uint8Array(digest));
  }

  private base64UrlEncode(buffer: Uint8Array): string {
    let binary = '';
    for (let i = 0; i < buffer.byteLength; i += 1) {
      binary += String.fromCharCode(buffer[i]);
    }

    return btoa(binary)
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=+$/, '');
  }

  private generateRandomString(length: number): string {
    const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';
    const randomValues = new Uint8Array(length);
    window.crypto.getRandomValues(randomValues);
    let result = '';
    for (let i = 0; i < randomValues.length; i += 1) {
      result += charset[randomValues[i] % charset.length];
    }
    return result;
  }

  getStoredState(): string | null {
    return sessionStorage.getItem(this.STORAGE_KEY_STATE);
  }

  clearPkceState(): void {
    sessionStorage.removeItem(this.STORAGE_KEY_CODE_VERIFIER);
    sessionStorage.removeItem(this.STORAGE_KEY_STATE);
  }

  /**
   * Obtiene el token actual
   */
  getToken(): string | null {
    return this.authStateSubject.value.token;
  }

  /**
   * Obtiene el estado actual de autenticación
   */
  getAuthState(): AuthState {
    return this.authStateSubject.value;
  }

  /**
   * Verifica si el usuario está autenticado
   */
  isAuthenticated(): boolean {
    return this.authStateSubject.value.isAuthenticated;
  }

  /**
   * Cierra la sesión y limpia el localStorage
   */
  logout(): void {
    localStorage.removeItem(this.STORAGE_KEY_TOKEN);
    localStorage.removeItem(this.STORAGE_KEY_REFRESH);
    localStorage.removeItem(this.STORAGE_KEY_ID);
    localStorage.removeItem(this.STORAGE_KEY_EXPIRES);

    this.authStateSubject.next(this.getInitialState());

    // Redirigir al servidor de Autorización para cerrar la sesión (borrar la cookie JSESSIONID)
    window.location.href = environment.apiUrl + '/logout';
  }
}

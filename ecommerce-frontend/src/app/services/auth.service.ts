import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { TokenResponse, AuthUser, AuthState } from '../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly AUTHORIZATION_ENDPOINT = 'http://127.0.0.1:9001/oauth2/authorize';
  private readonly TOKEN_URL = 'http://127.0.0.1:9001/oauth2/token';
  private readonly REDIRECT_URI = 'http://localhost:4200/auth/callback';
  private readonly CLIENT_ID = 'maintenance-spa';
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

    sessionStorage.setItem(this.STORAGE_KEY_CODE_VERIFIER, codeVerifier);
    sessionStorage.setItem(this.STORAGE_KEY_STATE, state);

    const params = new URLSearchParams({
      response_type: 'code',
      client_id: this.CLIENT_ID,
      redirect_uri: this.REDIRECT_URI,
      scope: 'openid profile read write',
      code_challenge: codeChallenge,
      code_challenge_method: 'S256',
      state: state
    });

    window.location.href = `${this.AUTHORIZATION_ENDPOINT}?${params.toString()}`;
  }

  /**
   * Intercambia el código por un token (llamado desde el callback)
   */
  exchangeCodeForToken(code: string): Observable<TokenResponse> {
    const codeVerifier = sessionStorage.getItem(this.STORAGE_KEY_CODE_VERIFIER);
    if (!codeVerifier) {
      throw new Error('PKCE code verifier not found. Restart login flow.');
    }

    const body = new URLSearchParams();
    body.set('code', code);
    body.set('grant_type', 'authorization_code');
    body.set('redirect_uri', this.REDIRECT_URI);
    body.set('client_id', this.CLIENT_ID);
    body.set('code_verifier', codeVerifier);

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
    // Guardar tokens en localStorage
    localStorage.setItem(this.STORAGE_KEY_TOKEN, response.access_token);
    localStorage.setItem(this.STORAGE_KEY_REFRESH, response.refresh_token);
    localStorage.setItem(this.STORAGE_KEY_ID, response.id_token);
    localStorage.setItem(this.STORAGE_KEY_EXPIRES, response.expires_in.toString());

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

    this.authStateSubject.next(newState);
  }

  /**
   * Restaura el estado de autenticación desde localStorage
   */
  private restoreAuthState(): void {
    const token = localStorage.getItem(this.STORAGE_KEY_TOKEN);
    const refreshToken = localStorage.getItem(this.STORAGE_KEY_REFRESH);
    const idToken = localStorage.getItem(this.STORAGE_KEY_ID);
    const expiresIn = localStorage.getItem(this.STORAGE_KEY_EXPIRES);

    if (token && this.isTokenValid(token, expiresIn)) {
      const user = this.decodeToken(token);
      const state: AuthState = {
        isAuthenticated: true,
        token,
        refreshToken,
        idToken,
        expiresIn: expiresIn ? parseInt(expiresIn, 10) : null,
        user
      };
      this.authStateSubject.next(state);
    } else {
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
        return null;
      }

      const decoded = JSON.parse(atob(parts[1]));
      return decoded;
    } catch (error) {
      console.error('Error decoding token:', error);
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
  }
}

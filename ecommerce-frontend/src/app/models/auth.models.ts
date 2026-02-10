export interface OAuthCodeResponse {
  rpta: number;
  message: string;
  body: {
    code: string;
  };
}

export interface TokenResponse {
  access_token: string;
  refresh_token: string;
  id_token: string;
  token_type: string;
  expires_in: number;
  scope?: string;
}

export interface AuthUser {
  sub: string;
  email?: string;
  [key: string]: any;
}

export interface AuthState {
  isAuthenticated: boolean;
  token: string | null;
  refreshToken: string | null;
  idToken: string | null;
  expiresIn: number | null;
  user: AuthUser | null;
}

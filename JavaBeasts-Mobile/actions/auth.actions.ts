import { AuthRequest, AuthResponse } from '@/interfaces/auth.interface';

import { javabeastsApi } from './api';

export const loginAction = async (authData: AuthRequest): Promise<AuthResponse> => {
  const { data } = await javabeastsApi.post<AuthResponse>('/auth/login', authData);
  return data;
};

export const registerAction = async (authData: AuthRequest): Promise<AuthResponse> => {
  const { data } = await javabeastsApi.post<AuthResponse>('/auth/register', authData);
  return data;
};

export const logoutAction = async (username: string): Promise<AuthResponse> => {
  const { data } = await javabeastsApi.post<AuthResponse>(
    `/auth/logout?username=${encodeURIComponent(username)}`
  );
  return data;
};

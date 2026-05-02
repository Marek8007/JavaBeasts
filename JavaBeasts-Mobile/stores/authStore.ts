import * as SecureStore from 'expo-secure-store';
import { create } from 'zustand';

import { AuthResponse } from '@/interfaces/auth.interface';

interface AuthStore {
  user: AuthResponse | null;
  isAuthenticated: boolean;
  login: (user: AuthResponse) => Promise<void>;
  logout: () => Promise<void>;
}

export const useAuthStore = create<AuthStore>((set) => ({
  user: null,
  isAuthenticated: false,

  login: async (user) => {
    await SecureStore.setItemAsync('user', JSON.stringify(user));
    set({ user, isAuthenticated: true });
  },

  logout: async () => {
    await SecureStore.deleteItemAsync('user');
    set({ user: null, isAuthenticated: false });
  },
}));

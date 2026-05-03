import * as SecureStore from 'expo-secure-store';
import { create } from 'zustand';

import { AuthResponse } from '@/interfaces/auth.interface';

interface AuthStore {
  user: AuthResponse | null;
  isAuthenticated: boolean;
  hasLoadedSession: boolean;
  login: (user: AuthResponse) => Promise<void>;
  loadSession: () => Promise<void>;
  logout: () => Promise<void>;
}

export const useAuthStore = create<AuthStore>((set) => ({
  user: null,
  isAuthenticated: false,
  hasLoadedSession: false,

  login: async (user) => {
    await SecureStore.setItemAsync('user', JSON.stringify(user));
    set({ user, isAuthenticated: true });
  },

  loadSession: async () => {
    const storedUser = await SecureStore.getItemAsync('user');

    if (!storedUser) {
      set({ user: null, isAuthenticated: false, hasLoadedSession: true });
      return;
    }

    try {
      const user = JSON.parse(storedUser) as AuthResponse;
      set({ user, isAuthenticated: true, hasLoadedSession: true });
    } catch {
      await SecureStore.deleteItemAsync('user');
      set({ user: null, isAuthenticated: false, hasLoadedSession: true });
    }
  },

  logout: async () => {
    await SecureStore.deleteItemAsync('user');
    set({ user: null, isAuthenticated: false, hasLoadedSession: true });
  },
}));

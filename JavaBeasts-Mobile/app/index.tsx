import { Redirect } from 'expo-router';

import { useAuthStore } from '@/stores/authStore';

export default function AppIndexScreen() {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const hasLoadedSession = useAuthStore((state) => state.hasLoadedSession);

  if (!hasLoadedSession) {
    return null;
  }

  if (isAuthenticated) {
    return <Redirect href="/(tabs)" />;
  }

  return <Redirect href="/(auth)/login" />;
}

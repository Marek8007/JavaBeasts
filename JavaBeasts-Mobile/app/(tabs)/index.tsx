import { logoutAction } from '@/actions/auth.actions';
import { useAuthStore } from '@/stores/authStore';
import { Ionicons } from '@expo/vector-icons';
import { router } from 'expo-router';
import { Alert, Pressable, SafeAreaView, StyleSheet, Text, View } from 'react-native';

export default function HomeScreen() {
  const user = useAuthStore((state) => state.user);
  const clearSession = useAuthStore((state) => state.logout);

  async function handleLogout() {
    try {
      if (user) {
        await logoutAction(user.username);
      }
    } catch {
      Alert.alert('Aviso', 'No se pudo cerrar sesion en el backend, pero se limpiara la sesion local.');
    } finally {
      await clearSession();
      router.replace('/(auth)/login');
    }
  }

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.screen}>
        <View style={styles.header}>
          <Text style={styles.brand}>JavaBeasts</Text>
          <Text style={styles.subtitle}>Mando movil conectado al backend.</Text>
        </View>

        <View style={styles.panel}>
          <View style={styles.iconBadge}>
            <Ionicons name="person-circle-outline" size={42} color="#1e4f8f" />
          </View>
          <Text style={styles.panelTitle}>{user?.username ?? 'Jugador'}</Text>
          <Text style={styles.stats}>
            Victorias {user?.matchesWon ?? 0} - Derrotas {user?.matchesLost ?? 0}
          </Text>
          <Text style={styles.message}>Sesion lista para gestionar equipos y entrar al lobby.</Text>

          <Pressable
            onPress={handleLogout}
            style={({ pressed }) => [styles.secondaryButton, pressed && styles.pressed]}>
            <Text style={styles.secondaryButtonText}>Cerrar sesion</Text>
          </Pressable>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#f3f6fb',
  },
  screen: {
    flex: 1,
    justifyContent: 'center',
    paddingHorizontal: 24,
  },
  header: {
    gap: 8,
    marginBottom: 24,
  },
  brand: {
    color: '#172033',
    fontSize: 34,
    fontWeight: '800',
  },
  subtitle: {
    color: '#5d6678',
    fontSize: 16,
  },
  panel: {
    alignItems: 'center',
    backgroundColor: '#ffffff',
    borderRadius: 8,
    gap: 12,
    padding: 22,
    shadowColor: '#172033',
    shadowOffset: { width: 0, height: 10 },
    shadowOpacity: 0.08,
    shadowRadius: 18,
    elevation: 3,
  },
  iconBadge: {
    backgroundColor: '#e8edf5',
    borderRadius: 8,
    padding: 10,
  },
  panelTitle: {
    color: '#172033',
    fontSize: 26,
    fontWeight: '800',
  },
  stats: {
    color: '#5d6678',
    fontSize: 15,
  },
  message: {
    color: '#42506a',
    fontSize: 14,
    lineHeight: 20,
    textAlign: 'center',
  },
  secondaryButton: {
    alignItems: 'center',
    borderColor: '#1e4f8f',
    borderRadius: 8,
    borderWidth: 1,
    justifyContent: 'center',
    marginTop: 8,
    minHeight: 48,
    paddingHorizontal: 18,
    width: '100%',
  },
  secondaryButtonText: {
    color: '#1e4f8f',
    fontSize: 15,
    fontWeight: '800',
  },
  pressed: {
    opacity: 0.82,
  },
});

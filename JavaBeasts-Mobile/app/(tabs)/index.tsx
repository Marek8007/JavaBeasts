import { logoutAction } from '@/actions/auth.actions';
import { useAuthStore } from '@/stores/authStore';
import { Ionicons } from '@expo/vector-icons';
import { router } from 'expo-router';
import { Alert, Pressable, SafeAreaView, Text, View } from 'react-native';

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
    <SafeAreaView className="flex-1 bg-beasts-soft">
      <View className="flex-1 justify-center px-6">
        <View className="mb-6 gap-2">
          <Text className="text-[34px] font-extrabold text-beasts-ink">JavaBeasts</Text>
          <Text className="text-base text-beasts-muted">Mando movil conectado al backend.</Text>
        </View>

        <View className="items-center gap-3 rounded-lg bg-white p-[22px] shadow-lg shadow-beasts-ink/10">
          <View className="rounded-lg bg-[#e8edf5] p-2.5">
            <Ionicons name="person-circle-outline" size={42} color="#1e4f8f" />
          </View>
          <Text className="text-[26px] font-extrabold text-beasts-ink">{user?.username ?? 'Jugador'}</Text>
          <Text className="text-[15px] text-beasts-muted">
            Victorias {user?.matchesWon ?? 0} - Derrotas {user?.matchesLost ?? 0}
          </Text>
          <Text className="text-center text-sm leading-5 text-[#42506a]">
            Sesion lista para gestionar equipos y entrar al lobby.
          </Text>

          <Pressable
            className="mt-2 min-h-12 w-full items-center justify-center rounded-lg border border-beasts-blue px-[18px] active:opacity-80"
            onPress={handleLogout}>
            <Text className="text-[15px] font-extrabold text-beasts-blue">Cerrar sesion</Text>
          </Pressable>
        </View>
      </View>
    </SafeAreaView>
  );
}

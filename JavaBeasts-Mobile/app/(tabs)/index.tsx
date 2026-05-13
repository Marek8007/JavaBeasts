import { getProfileAction, logoutAction } from '@/actions/auth.actions';
import { leaveRoomAction } from '@/actions/lobby-socket.actions';
import { useAuthStore } from '@/stores/authStore';
import { useLobbyStore } from '@/stores/lobbyStore';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { router } from 'expo-router';
import { useCallback } from 'react';
import { Alert, Pressable, SafeAreaView, Text, View } from 'react-native';

export default function HomeScreen() {
  const user = useAuthStore((state) => state.user);
  const updateUser = useAuthStore((state) => state.updateUser);
  const clearSession = useAuthStore((state) => state.logout);
  const roomStatus = useLobbyStore((state) => state.roomStatus);
  const clearRoom = useLobbyStore((state) => state.clearRoom);

  async function handleLogout() {
    try {
      if (user) {
        if (roomStatus) {
          try {
            await leaveRoomAction(roomStatus.roomCode, user.username);
          } catch {
            Alert.alert('Aviso', 'No se pudo salir de la sala en el backend, pero se limpiara la sesion local.');
          } finally {
            clearRoom();
          }
        }

        try {
          await logoutAction(user.username);
        } catch {
          Alert.alert('Aviso', 'No se pudo cerrar sesion en el backend, pero se limpiara la sesion local.');
          clearRoom();
        }
      }
    } finally {
      await clearSession();
      router.replace('/(auth)/login');
    }
  }

  useFocusEffect(
    useCallback(() => {
      if (!user?.username) {
        return;
      }

      void (async () => {
        try {
          const refreshedUser = await getProfileAction(user.username);
          await updateUser(refreshedUser);
        } catch {
          // Keep the cached user values if the refresh fails.
        }
      })();
    }, [user?.username, updateUser])
  );

  return (
    <SafeAreaView className="flex-1 bg-beasts-soft">
      <View className="flex-1 justify-center px-6">
        <View className="mb-6 gap-2">
          <Text className="text-[34px] font-extrabold text-beasts-ink">JavaBeasts</Text>
          <Text className="text-base text-beasts-muted">Mando movil conectado al backend.</Text>
        </View>

        <View className="items-center gap-3 rounded-lg bg-beasts-panel p-[22px] shadow-lg shadow-beasts-ink/10">
          <View className="rounded-lg bg-[#1e3a8a] p-2.5">
            <Ionicons name="person-circle-outline" size={42} color="#1d4ed8" />
          </View>
          <Text className="text-[26px] font-extrabold text-beasts-ink">{user?.username ?? 'Jugador'}</Text>
          <Text className="text-[15px] text-beasts-muted">
            Victorias {user?.matchesWon ?? 0} - Derrotas {user?.matchesLost ?? 0}
          </Text>
          <Text className="text-center text-sm leading-5 text-[#cbd5e1]">
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

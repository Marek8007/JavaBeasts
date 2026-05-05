import { joinRoomAction } from '@/actions/lobby-socket.actions';
import { RoomStatusPayload } from '@/interfaces/lobby-socket.interface';
import { useAuthStore } from '@/stores/authStore';
import { Ionicons } from '@expo/vector-icons';
import { useState } from 'react';
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  SafeAreaView,
  Text,
  TextInput,
  View,
} from 'react-native';

const ROOM_CODE_LENGTH = 6;

export default function RoomJoinScreen() {
  const user = useAuthStore((state) => state.user);
  const [roomCode, setRoomCode] = useState('');
  const [joining, setJoining] = useState(false);
  const [roomStatus, setRoomStatus] = useState<RoomStatusPayload | null>(null);
  const [error, setError] = useState('');

  function updateRoomCode(value: string) {
    setRoomCode(value.replace(/\D/g, '').slice(0, ROOM_CODE_LENGTH));
    setRoomStatus(null);
    setError('');
  }

  async function joinRoom() {
    if (joining) {
      return;
    }

    const trimmedCode = roomCode.trim();

    if (!user) {
      setError('Inicia sesion antes de entrar a una sala.');
      return;
    }

    if (!trimmedCode) {
      setError('Introduce el codigo de sala.');
      return;
    }

    if (trimmedCode.length !== ROOM_CODE_LENGTH) {
      setError('El codigo de sala debe tener 6 digitos.');
      return;
    }

    setJoining(true);
    setError('');

    try {
      const status = await joinRoomAction(trimmedCode, user.username);
      setRoomStatus(status);
    } catch (requestError: any) {
      setRoomStatus(null);
      setError(requestError?.message ? String(requestError.message) : 'No se pudo entrar en la sala.');
    } finally {
      setJoining(false);
    }
  }

  return (
    <SafeAreaView className="flex-1 bg-beasts-soft">
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        className="flex-1"
        keyboardVerticalOffset={24}>
        <View className="flex-1 px-[18px] pt-6">
          <View className="mb-[18px]">
            <Text className="text-3xl font-extrabold text-beasts-ink">Sala</Text>
            <Text className="mt-1 max-w-[300px] text-sm leading-5 text-beasts-muted">
              Entra con el codigo que aparece en la pantalla principal.
            </Text>
          </View>

          <View className="rounded-lg bg-white p-[18px] shadow-lg shadow-beasts-ink/10">
            <View className="mb-5 flex-row items-center gap-3">
              <View className="h-12 w-12 items-center justify-center rounded-lg bg-beasts-blue">
                <Ionicons name="ticket-outline" size={24} color="#ffffff" />
              </View>
              <View className="flex-1">
                <Text className="text-xl font-extrabold text-beasts-ink">Codigo de sala</Text>
                <Text className="mt-1 text-sm text-beasts-muted">{user?.username ?? 'Jugador'}</Text>
              </View>
            </View>

            <TextInput
              className="min-h-[62px] rounded-lg border border-beasts-line bg-[#f8fafc] px-4 text-center text-2xl font-extrabold tracking-[8px] text-beasts-ink"
              inputMode="numeric"
              keyboardType="number-pad"
              maxLength={ROOM_CODE_LENGTH}
              onChangeText={updateRoomCode}
              placeholder="000000"
              placeholderTextColor="#a8b0bf"
              returnKeyType="done"
              textContentType="oneTimeCode"
              value={roomCode}
            />

            {error ? (
              <View className="mt-3 rounded-lg border border-[#f7d6bf] bg-[#fff4ed] px-3.5 py-3">
                <Text className="text-sm font-semibold leading-5 text-beasts-warning">{error}</Text>
              </View>
            ) : null}

            {roomStatus ? (
              <View className="mt-3 gap-3 rounded-lg border border-[#cde7d8] bg-[#f0fdf4] px-3.5 py-3">
                <View className="flex-row items-center gap-2">
                  <Ionicons name="checkmark-circle-outline" size={20} color="#15803d" />
                  <Text className="flex-1 text-sm font-semibold leading-5 text-[#15803d]">
                    Dentro de la sala {roomStatus.roomCode}
                  </Text>
                </View>
                <View className="gap-2">
                  <LobbyPlayerLine label="Jugador 1" username={roomStatus.playerOne?.username} />
                  <LobbyPlayerLine label="Jugador 2" username={roomStatus.playerTwo?.username} />
                </View>
              </View>
            ) : null}

            <Pressable
              className={`mt-5 min-h-12 items-center justify-center rounded-lg bg-beasts-blue px-[18px] active:opacity-80 ${
                joining ? 'opacity-65' : ''
              }`}
              disabled={joining}
              onPress={() => void joinRoom()}>
              {joining ? (
                <ActivityIndicator color="#ffffff" />
              ) : (
                <Text className="text-[15px] font-extrabold text-white">Unirse</Text>
              )}
            </Pressable>
          </View>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

function LobbyPlayerLine({ label, username }: { label: string; username?: string | null }) {
  return (
    <View className="flex-row items-center justify-between rounded-lg bg-white px-3 py-2">
      <Text className="text-xs font-extrabold uppercase text-beasts-muted">{label}</Text>
      <View className="flex-row items-center gap-1.5">
        {username ? (
          <>
            <Ionicons name="person-circle-outline" size={18} color="#15803d" />
            <Text className="max-w-[150px] text-sm font-extrabold text-beasts-ink" numberOfLines={1}>
              {username}
            </Text>
          </>
        ) : (
          <>
            <Ionicons name="ellipse-outline" size={16} color="#68758a" />
            <Text className="text-sm font-semibold text-beasts-muted">Libre</Text>
          </>
        )}
      </View>
    </View>
  );
}

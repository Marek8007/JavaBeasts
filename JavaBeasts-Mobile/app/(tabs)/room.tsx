import { useAuthStore } from '@/stores/authStore';
import { Ionicons } from '@expo/vector-icons';
import { useState } from 'react';
import {
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
  const [validatedCode, setValidatedCode] = useState('');
  const [error, setError] = useState('');

  function updateRoomCode(value: string) {
    setRoomCode(value.replace(/\D/g, '').slice(0, ROOM_CODE_LENGTH));
    setValidatedCode('');
    setError('');
  }

  function validateRoomCode() {
    const trimmedCode = roomCode.trim();

    if (!trimmedCode) {
      setError('Introduce el codigo de sala.');
      return;
    }

    if (trimmedCode.length !== ROOM_CODE_LENGTH) {
      setError('El codigo de sala debe tener 6 digitos.');
      return;
    }

    setError('');
    setValidatedCode(trimmedCode);
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

            {validatedCode ? (
              <View className="mt-3 flex-row items-center gap-2 rounded-lg border border-[#cde7d8] bg-[#f0fdf4] px-3.5 py-3">
                <Ionicons name="checkmark-circle-outline" size={20} color="#15803d" />
                <Text className="flex-1 text-sm font-semibold leading-5 text-[#15803d]">
                  Codigo preparado: {validatedCode}
                </Text>
              </View>
            ) : null}

            <Pressable
              className="mt-5 min-h-12 items-center justify-center rounded-lg bg-beasts-blue px-[18px] active:opacity-80"
              onPress={validateRoomCode}>
              <Text className="text-[15px] font-extrabold text-white">Unirse</Text>
            </Pressable>
          </View>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

import {
  getRoomStatusAction,
  joinRoomAction,
  leaveRoomAction,
  setReadyAction,
} from '@/actions/lobby-socket.actions';
import { getTeamsByUserAction } from '@/actions/team.actions';
import { TeamResponse } from '@/interfaces/team.interface';
import { useAuthStore } from '@/stores/authStore';
import { useLobbyStore } from '@/stores/lobbyStore';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { useCallback, useEffect, useState } from 'react';
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
const ROOM_STATUS_REFRESH_MS = 2500;

export default function RoomJoinScreen() {
  const user = useAuthStore((state) => state.user);
  const roomStatus = useLobbyStore((state) => state.roomStatus);
  const setRoomStatus = useLobbyStore((state) => state.setRoomStatus);
  const [roomCode, setRoomCode] = useState('');
  const [joining, setJoining] = useState(false);
  const [leaving, setLeaving] = useState(false);
  const [settingReady, setSettingReady] = useState(false);
  const [teamsLoading, setTeamsLoading] = useState(false);
  const [activeTeam, setActiveTeam] = useState<TeamResponse | null>(null);
  const [error, setError] = useState('');
  const currentPlayer = [roomStatus?.playerOne, roomStatus?.playerTwo].find(
    (slot) => slot?.username === user?.username
  );
  const ready = Boolean(currentPlayer?.ready);
  const joined = Boolean(roomStatus);

  const loadActiveTeam = useCallback(async () => {
    if (!user) {
      setActiveTeam(null);
      return;
    }

    setTeamsLoading(true);

    try {
      const teams = await getTeamsByUserAction(user.userId);
      setActiveTeam(teams.find((team) => team.active) ?? null);
    } catch {
      setActiveTeam(null);
    } finally {
      setTeamsLoading(false);
    }
  }, [user]);

  useFocusEffect(
    useCallback(() => {
      void loadActiveTeam();
    }, [loadActiveTeam])
  );

  useEffect(() => {
    if (!roomStatus) {
      return;
    }

    let mounted = true;

    const refreshRoomStatus = async () => {
      try {
        const status = await getRoomStatusAction();

        if (!mounted) {
          return;
        }

        setRoomStatus(status);
      } catch {
        // Keep the last known state; the next interval can recover.
      }
    };

    const intervalId = setInterval(() => {
      void refreshRoomStatus();
    }, ROOM_STATUS_REFRESH_MS);

    return () => {
      mounted = false;
      clearInterval(intervalId);
    };
  }, [roomStatus, setRoomStatus]);

  function updateRoomCode(value: string) {
    if (joined) {
      return;
    }

    setRoomCode(value.replace(/\D/g, '').slice(0, ROOM_CODE_LENGTH));
    setRoomStatus(null);
    setError('');
  }

  async function joinRoom() {
    if (joining || joined) {
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

  async function leaveRoom() {
    if (!user || !roomStatus || leaving) {
      return;
    }

    if (ready) {
      setError('Desmarca listo antes de salir de la sala.');
      return;
    }

    setLeaving(true);
    setError('');

    try {
      await leaveRoomAction(roomStatus.roomCode, user.username);
      setRoomStatus(null);
    } catch (requestError: any) {
      setError(requestError?.message ? String(requestError.message) : 'No se pudo salir de la sala.');
    } finally {
      setLeaving(false);
    }
  }

  async function toggleReady() {
    if (!user || !roomStatus || settingReady) {
      return;
    }

    if (!ready && !activeTeam) {
      setError('Selecciona un equipo activo antes de marcar listo.');
      return;
    }

    setSettingReady(true);
    setError('');

    try {
      const status = await setReadyAction(roomStatus.roomCode, user.username, !ready);
      setRoomStatus(status);
    } catch (requestError: any) {
      setError(requestError?.message ? String(requestError.message) : 'No se pudo cambiar el estado de listo.');
    } finally {
      setSettingReady(false);
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
              {joined
                ? 'Permanece en la sala mientras se prepara la partida.'
                : 'Entra con el codigo que aparece en la pantalla principal.'}
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
              className={`min-h-[62px] rounded-lg border px-4 text-center text-2xl font-extrabold tracking-[8px] text-beasts-ink ${
                joined ? 'border-[#cde7d8] bg-[#f0fdf4]' : 'border-beasts-line bg-[#f8fafc]'
              }`}
              editable={!joined}
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

            <View className="mt-3 rounded-lg border border-beasts-line bg-[#f8fafc] px-3.5 py-3">
              <View className="flex-row items-center justify-between gap-3">
                <View className="flex-row flex-1 items-center gap-2">
                  <Ionicons name="albums-outline" size={19} color="#1e4f8f" />
                  <Text className="text-sm font-extrabold text-beasts-ink">Equipo activo</Text>
                </View>
                {teamsLoading ? <ActivityIndicator color="#1e4f8f" /> : null}
              </View>
              <Text className={`mt-2 text-sm leading-5 ${activeTeam ? 'text-[#42506a]' : 'text-beasts-warning'}`}>
                {activeTeam
                  ? activeTeam.name
                  : 'No tienes ningun equipo activo seleccionado.'}
              </Text>
            </View>

            {roomStatus ? (
              <View className="mt-3 gap-3 rounded-lg border border-[#cde7d8] bg-[#f0fdf4] px-3.5 py-3">
                <View className="flex-row items-center gap-2">
                  <Ionicons name="checkmark-circle-outline" size={20} color="#15803d" />
                  <Text className="flex-1 text-sm font-semibold leading-5 text-[#15803d]">
                    Dentro de la sala {roomStatus.roomCode}
                  </Text>
                </View>
                <View
                  className={`flex-row items-center gap-2 rounded-lg px-3 py-2 ${
                    roomStatus.canStart ? 'bg-[#dcfce7]' : 'bg-white'
                  }`}>
                  <Ionicons
                    name={roomStatus.canStart ? 'flash-outline' : 'hourglass-outline'}
                    size={18}
                    color={roomStatus.canStart ? '#15803d' : '#68758a'}
                  />
                  <Text
                    className={`flex-1 text-sm font-semibold leading-5 ${
                      roomStatus.canStart ? 'text-[#15803d]' : 'text-beasts-muted'
                    }`}>
                    {roomStatus.canStart
                      ? 'Ambos jugadores estan listos.'
                      : 'Esperando a que ambos jugadores marquen listo.'}
                  </Text>
                </View>
                <View className="gap-2">
                  <LobbyPlayerLine
                    label="Jugador 1"
                    ready={roomStatus.playerOne?.ready}
                    username={roomStatus.playerOne?.username}
                  />
                  <LobbyPlayerLine
                    label="Jugador 2"
                    ready={roomStatus.playerTwo?.ready}
                    username={roomStatus.playerTwo?.username}
                  />
                </View>
                <Pressable
                  className={`min-h-11 flex-row items-center justify-center gap-2 rounded-lg px-4 active:opacity-80 ${
                    ready ? 'bg-[#15803d]' : 'border border-[#15803d] bg-white'
                  } ${settingReady || (!ready && !activeTeam) ? 'opacity-45' : ''}`}
                  disabled={settingReady || (!ready && !activeTeam)}
                  onPress={() => void toggleReady()}>
                  {settingReady ? (
                    <ActivityIndicator color={ready ? '#ffffff' : '#15803d'} />
                  ) : (
                    <>
                      <Ionicons
                        name={ready ? 'checkmark-circle' : 'checkmark-circle-outline'}
                        size={20}
                        color={ready ? '#ffffff' : '#15803d'}
                      />
                      <Text className={`text-sm font-extrabold ${ready ? 'text-white' : 'text-[#15803d]'}`}>
                        {ready ? 'Listo' : 'Marcar listo'}
                      </Text>
                    </>
                  )}
                </Pressable>
                <Pressable
                  className={`min-h-11 items-center justify-center rounded-lg border border-[#bb3e03] bg-white px-4 active:opacity-80 ${
                    leaving || ready ? 'opacity-45' : ''
                  }`}
                  disabled={leaving || ready}
                  onPress={() => void leaveRoom()}>
                  {leaving ? (
                    <ActivityIndicator color="#bb3e03" />
                  ) : (
                    <Text className="text-sm font-extrabold text-[#bb3e03]">Salir de la sala</Text>
                  )}
                </Pressable>
              </View>
            ) : null}

            <Pressable
              className={`mt-5 min-h-12 items-center justify-center rounded-lg bg-beasts-blue px-[18px] active:opacity-80 ${
                joining || joined ? 'opacity-45' : ''
              }`}
              disabled={joining || joined}
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

function LobbyPlayerLine({
  label,
  ready,
  username,
}: {
  label: string;
  ready?: boolean | null;
  username?: string | null;
}) {
  return (
    <View className="flex-row items-center justify-between rounded-lg bg-white px-3 py-2">
      <Text className="text-xs font-extrabold uppercase text-beasts-muted">{label}</Text>
      <View className="flex-row items-center gap-1.5">
        {username ? (
          <>
            <Ionicons
              name={ready ? 'checkmark-circle' : 'person-circle-outline'}
              size={18}
              color={ready ? '#15803d' : '#68758a'}
            />
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

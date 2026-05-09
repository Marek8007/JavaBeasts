import { getBattleSnapshotAction, submitBattleActionAction } from '@/actions/battle.actions';
import {
  BattleActionSubmissionResponse,
  BattleCreatureSnapshotResponse,
  BattleSnapshotResponse,
} from '@/interfaces/battle.interface';
import { useAuthStore } from '@/stores/authStore';
import { useLobbyStore } from '@/stores/lobbyStore';
import { Ionicons } from '@expo/vector-icons';
import { router } from 'expo-router';
import { useEffect, useMemo, useState } from 'react';
import {
  ActivityIndicator,
  Pressable,
  RefreshControl,
  SafeAreaView,
  ScrollView,
  Text,
  View,
} from 'react-native';

const BATTLE_REFRESH_MS = 2500;

export default function BattleScreen() {
  const user = useAuthStore((state) => state.user);
  const roomStatus = useLobbyStore((state) => state.roomStatus);
  const [snapshot, setSnapshot] = useState<BattleSnapshotResponse | null>(null);
  const [actionState, setActionState] = useState<BattleActionSubmissionResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const currentPlayerIsPlayerOne = user?.username === snapshot?.playerOne.username;
  const currentPlayer = useMemo(() => {
    if (!snapshot || !user) {
      return null;
    }

    return snapshot.playerOne.username === user.username ? snapshot.playerOne : snapshot.playerTwo;
  }, [snapshot, user]);
  const rivalPlayer = useMemo(() => {
    if (!snapshot || !user) {
      return null;
    }

    return snapshot.playerOne.username === user.username ? snapshot.playerTwo : snapshot.playerOne;
  }, [snapshot, user]);
  const currentPlayerActionSubmitted = actionState
    ? currentPlayerIsPlayerOne
      ? actionState.playerOneActionSubmitted
      : actionState.playerTwoActionSubmitted
    : false;

  async function loadSnapshot(showRefresh = false) {
    if (!roomStatus?.roomCode) {
      return;
    }

    if (showRefresh) {
      setRefreshing(true);
    } else if (!snapshot) {
      setLoading(true);
    }

    try {
      const nextSnapshot = await getBattleSnapshotAction(roomStatus.roomCode);
      setSnapshot(nextSnapshot);
      setMessage(nextSnapshot.message ?? '');
      setActionState((currentActionState) => {
        if (!currentActionState) {
          return currentActionState;
        }

        return currentActionState.turnNumber < nextSnapshot.turnNumber ? null : currentActionState;
      });
      setError('');
    } catch (requestError: any) {
      setError(requestError?.message ? String(requestError.message) : 'No se pudo cargar el combate.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }

  useEffect(() => {
    if (!roomStatus?.canStart) {
      router.replace('/(tabs)/room');
      return;
    }

    void loadSnapshot();

    const intervalId = setInterval(() => {
      void loadSnapshot();
    }, BATTLE_REFRESH_MS);

    return () => {
      clearInterval(intervalId);
    };
  }, [roomStatus?.canStart, roomStatus?.roomCode]);

  async function submitAttack(moveSlot: number) {
    if (!user || !roomStatus || actionLoading || currentPlayerActionSubmitted) {
      return;
    }

    setActionLoading(true);
    setError('');

    try {
      const response = await submitBattleActionAction(roomStatus.roomCode, user.username, 'ATTACK', moveSlot);
      setActionState(response);
      setSnapshot(response.snapshot);
      setMessage(response.message);
    } catch (requestError: any) {
      setError(requestError?.message ? String(requestError.message) : 'No se pudo enviar la accion.');
    } finally {
      setActionLoading(false);
    }
  }

  return (
    <SafeAreaView className="flex-1 bg-beasts-soft">
      <ScrollView
        className="flex-1"
        contentContainerClassName="px-[18px] pb-8 pt-6"
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadSnapshot(true)} />}>
        <View className="mb-5 flex-row items-center justify-between gap-3">
          <View className="flex-1">
            <Text className="text-3xl font-extrabold text-beasts-ink">Combate</Text>
            <Text className="mt-1 text-sm font-semibold text-beasts-muted">
              Sala {roomStatus?.roomCode ?? '------'}
            </Text>
          </View>
          <View className="h-12 w-12 items-center justify-center rounded-lg bg-beasts-blue">
            <Ionicons name="flash-outline" size={24} color="#ffffff" />
          </View>
        </View>

        {error ? (
          <View className="mb-4 rounded-lg border border-[#f7d6bf] bg-[#fff4ed] px-3.5 py-3">
            <Text className="text-sm font-semibold leading-5 text-beasts-warning">{error}</Text>
          </View>
        ) : null}

        {loading ? (
          <View className="min-h-[320px] items-center justify-center rounded-lg bg-white">
            <ActivityIndicator color="#1e4f8f" />
            <Text className="mt-3 text-sm font-semibold text-beasts-muted">Cargando combate...</Text>
          </View>
        ) : snapshot && currentPlayer && rivalPlayer ? (
          <View className="gap-4">
            <View className="rounded-lg bg-white p-4 shadow-lg shadow-beasts-ink/10">
              <Text className="text-xs font-extrabold uppercase text-beasts-muted">Turno</Text>
              <Text className="mt-1 text-4xl font-extrabold text-beasts-ink">{snapshot.turnNumber}</Text>
              <Text className="mt-3 text-sm font-semibold leading-5 text-beasts-muted">
                {message
                  ? message
                  : currentPlayerActionSubmitted
                    ? actionState?.turnReadyToResolve
                      ? 'Ambos jugadores han enviado accion.'
                      : 'Accion enviada. Esperando al rival.'
                    : 'Elige una accion para este turno.'}
              </Text>
            </View>

            <View className="gap-3">
              <BattleCreatureCard
                accentColor="#15803d"
                creature={currentPlayer.activeJaBea}
                label="Tu JaBea"
                teamName={currentPlayer.teamName}
                username={currentPlayer.username}
              />
              <BattleCreatureCard
                accentColor="#bb3e03"
                creature={rivalPlayer.activeJaBea}
                label="Rival"
                teamName={rivalPlayer.teamName}
                username={rivalPlayer.username}
              />
            </View>

            <View className="gap-3">
              {[0, 1, 2].map((moveSlot) => (
                <Pressable
                  key={`attack-${moveSlot}`}
                  className={`min-h-[92px] flex-row items-center justify-between rounded-lg bg-beasts-blue px-5 active:opacity-80 ${
                    actionLoading || currentPlayerActionSubmitted ? 'opacity-45' : ''
                  }`}
                  disabled={actionLoading || currentPlayerActionSubmitted}
                  onPress={() => void submitAttack(moveSlot)}>
                  <View>
                    <Text className="text-xs font-extrabold uppercase text-white/75">Accion</Text>
                    <Text className="mt-1 text-2xl font-extrabold text-white">Ataque {moveSlot + 1}</Text>
                  </View>
                  {actionLoading ? (
                    <ActivityIndicator color="#ffffff" />
                  ) : (
                    <Ionicons name="arrow-forward-circle" size={34} color="#ffffff" />
                  )}
                </Pressable>
              ))}
            </View>
          </View>
        ) : (
          <View className="rounded-lg bg-white px-4 py-5">
            <Text className="text-sm font-semibold leading-5 text-beasts-muted">
              No hay una partida activa cargada para este usuario.
            </Text>
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

function BattleCreatureCard({
  accentColor,
  creature,
  label,
  teamName,
  username,
}: {
  accentColor: string;
  creature: BattleCreatureSnapshotResponse;
  label: string;
  teamName: string;
  username: string;
}) {
  return (
    <View className="rounded-lg bg-white p-4 shadow-lg shadow-beasts-ink/10">
      <View className="flex-row items-center justify-between gap-3">
        <View className="flex-1">
          <Text className="text-xs font-extrabold uppercase text-beasts-muted">{label}</Text>
          <Text className="mt-1 text-lg font-extrabold text-beasts-ink">{creature.name}</Text>
          <Text className="mt-1 text-sm font-semibold text-beasts-muted" numberOfLines={1}>
            {username} · {teamName}
          </Text>
        </View>
        <View className="h-11 w-11 items-center justify-center rounded-lg" style={{ backgroundColor: accentColor }}>
          <Ionicons name="paw-outline" size={24} color="#ffffff" />
        </View>
      </View>
      <View className="mt-4">
        <View className="flex-row items-center justify-between">
          <Text className="text-xs font-extrabold uppercase text-beasts-muted">Vida</Text>
          <Text className="text-sm font-extrabold text-beasts-ink">
            {creature.currentHealth} / {creature.maxHealth}
          </Text>
        </View>
        <View className="mt-2 h-3 overflow-hidden rounded-full bg-[#e8edf5]">
          <View
            className="h-full rounded-full"
            style={{
              backgroundColor: accentColor,
              width: `${Math.max(0, Math.min(100, (creature.currentHealth / creature.maxHealth) * 100))}%`,
            }}
          />
        </View>
      </View>
    </View>
  );
}

import { getProfileAction } from '@/actions/auth.actions';
import { getBattleSnapshotAction, resetBattleAction, submitBattleActionAction } from '@/actions/battle.actions';
import { getTeamCompositionAction } from '@/actions/team-composition.actions';
import { getJaBeaImage } from '@/constants/jabea-images';
import {
  BattleActionSubmissionResponse,
  BattleCreatureSnapshotResponse,
  BattleMoveSnapshotResponse,
  BattleSnapshotResponse,
} from '@/interfaces/battle.interface';
import { MoveSummaryResponse } from '@/interfaces/team-composition.interface';
import { useAuthStore } from '@/stores/authStore';
import { useLobbyStore } from '@/stores/lobbyStore';
import { Ionicons } from '@expo/vector-icons';
import { ComponentProps } from 'react';
import { router } from 'expo-router';
import { useEffect, useMemo, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  Image,
  Pressable,
  RefreshControl,
  SafeAreaView,
  ScrollView,
  Text,
  View,
} from 'react-native';

const WAITING_REFRESH_MS = 2500;

interface SwitchOption {
  slot: number;
  name: string;
  typeName?: string | null;
  currentHealth: number;
  maxHealth: number;
}

type IoniconName = ComponentProps<typeof Ionicons>['name'];

const TYPE_COLORS: Record<string, string> = {
  Agua: '#1d4ed8',
  Electrico: '#ca8a04',
  Fuego: '#c2410c',
  Normal: '#64748b',
  Planta: '#15803d',
};

export default function BattleScreen() {
  const user = useAuthStore((state) => state.user);
  const updateUser = useAuthStore((state) => state.updateUser);
  const roomStatus = useLobbyStore((state) => state.roomStatus);
  const setRoomStatus = useLobbyStore((state) => state.setRoomStatus);
  const [snapshot, setSnapshot] = useState<BattleSnapshotResponse | null>(null);
  const [actionState, setActionState] = useState<BattleActionSubmissionResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);
  const [resettingBattle, setResettingBattle] = useState(false);
  const [configuredMoves, setConfiguredMoves] = useState<BattleMoveSnapshotResponse[]>([]);
  const [switchOptions, setSwitchOptions] = useState<SwitchOption[]>([]);
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
  const waitingForTurnResolution = Boolean(actionState && currentPlayerActionSubmitted && !actionState.turnResolved);
  const attackMoves = currentPlayer?.activeJaBea.moves?.length
    ? currentPlayer.activeJaBea.moves
    : configuredMoves;
  const activeCreatureFainted = Boolean(
    currentPlayer?.activeJaBea.currentHealth !== undefined && currentPlayer.activeJaBea.currentHealth <= 0
  );
  const resultLabel = snapshot?.finished
    ? snapshot.winnerUsername
      ? snapshot.winnerUsername === user?.username
        ? 'Victoria'
        : 'Derrota'
      : 'Empate'
    : null;

  async function loadPlayerTeamOptions(nextSnapshot: BattleSnapshotResponse) {
    if (!user) {
      setConfiguredMoves([]);
      setSwitchOptions([]);
      return;
    }

    const player =
      nextSnapshot.playerOne.username === user.username ? nextSnapshot.playerOne : nextSnapshot.playerTwo;

    if (player.activeJaBea.moves?.length) {
      setConfiguredMoves([]);
    }

    try {
      const composition = await getTeamCompositionAction(player.userId, player.teamId);
      const activeSlot = composition.slots.find((slot) => slot.slot === player.activeJaBea.slot);
      const member = activeSlot?.member;

      if (member && !player.activeJaBea.moves?.length) {
        setConfiguredMoves([
          toBattleMove(0, member.uniqueMove),
          toBattleMove(1, member.move1),
          toBattleMove(2, member.move2),
        ].filter((move): move is BattleMoveSnapshotResponse => Boolean(move)));
      }

      setSwitchOptions(toSwitchOptions(player.teamCreatures ?? [], player.activeJaBea.slot));
    } catch {
      setConfiguredMoves([]);
      setSwitchOptions(toSwitchOptions(player.teamCreatures ?? [], player.activeJaBea.slot));
    }
  }

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
      void loadPlayerTeamOptions(nextSnapshot);
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
  }, [roomStatus?.canStart, roomStatus?.roomCode]);

  useEffect(() => {
    if (!waitingForTurnResolution) {
      return;
    }

    const intervalId = setInterval(() => {
      void loadSnapshot();
    }, WAITING_REFRESH_MS);

    return () => {
      clearInterval(intervalId);
    };
  }, [waitingForTurnResolution, roomStatus?.roomCode]);

  async function submitAttack(moveSlot: number) {
    if (!user || !roomStatus || actionLoading || currentPlayerActionSubmitted || snapshot?.finished) {
      return;
    }

    setActionLoading(true);
    setError('');

    try {
      const response = await submitBattleActionAction(roomStatus.roomCode, user.username, 'ATTACK', moveSlot);
      setActionState(response);
      setSnapshot(response.snapshot);
      setMessage(response.message);
      void loadPlayerTeamOptions(response.snapshot);
    } catch (requestError: any) {
      setError(requestError?.message ? String(requestError.message) : 'No se pudo enviar la accion.');
    } finally {
      setActionLoading(false);
    }
  }

  async function submitSwitch(switchSlot: number) {
    if (!user || !roomStatus || actionLoading || currentPlayerActionSubmitted || snapshot?.finished) {
      return;
    }

    setActionLoading(true);
    setError('');

    try {
      const response = await submitBattleActionAction(roomStatus.roomCode, user.username, 'SWITCH', undefined, switchSlot);
      setActionState(response);
      setSnapshot(response.snapshot);
      setMessage(response.message);
      void loadPlayerTeamOptions(response.snapshot);
    } catch (requestError: any) {
      setError(requestError?.message ? String(requestError.message) : 'No se pudo cambiar de JaBea.');
    } finally {
      setActionLoading(false);
    }
  }

  function confirmSurrender() {
    if (!user || !roomStatus || actionLoading || snapshot?.finished) {
      return;
    }

    Alert.alert('Rendirse', 'Quieres rendirte y dar la victoria al rival?', [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Rendirse',
        style: 'destructive',
        onPress: () => void submitSurrender(),
      },
    ]);
  }

  async function submitSurrender() {
    if (!user || !roomStatus || actionLoading || snapshot?.finished) {
      return;
    }

    setActionLoading(true);
    setError('');

    try {
      const response = await submitBattleActionAction(roomStatus.roomCode, user.username, 'SURRENDER');
      setActionState(response);
      setSnapshot(response.snapshot);
      setMessage(response.message);
    } catch (requestError: any) {
      setError(requestError?.message ? String(requestError.message) : 'No se pudo rendir la partida.');
    } finally {
      setActionLoading(false);
    }
  }

  async function resetBattle() {
    if (!roomStatus?.roomCode || resettingBattle) {
      return;
    }

    setResettingBattle(true);
    setError('');

    try {
      const status = await resetBattleAction(roomStatus.roomCode);
      if (user?.username) {
        const refreshedUser = await getProfileAction(user.username);
        await updateUser(refreshedUser);
      }
      setRoomStatus(status);
      setSnapshot(null);
      setActionState(null);
      setMessage('');
      router.replace('/(tabs)/room');
    } catch (requestError: any) {
      setError(requestError?.message ? String(requestError.message) : 'No se pudo volver a la sala.');
    } finally {
      setResettingBattle(false);
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
          <Pressable
            className={`h-12 w-12 items-center justify-center rounded-lg bg-[#bb3e03] active:opacity-80 ${
              actionLoading || snapshot?.finished ? 'opacity-45' : ''
            }`}
            disabled={actionLoading || snapshot?.finished}
            onPress={confirmSurrender}>
            <Ionicons name="flag-outline" size={24} color="#ffffff" />
          </Pressable>
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
              <Text className="text-xs font-extrabold uppercase text-beasts-muted">
                {snapshot.finished ? 'Resultado' : 'Turno'}
              </Text>
              <Text className={`mt-1 text-4xl font-extrabold ${snapshot.finished ? 'text-[#15803d]' : 'text-beasts-ink'}`}>
                {resultLabel ?? snapshot.turnNumber}
              </Text>
              {snapshot.finished && snapshot.winnerUsername ? (
                <Text className="mt-2 text-sm font-extrabold text-beasts-ink">
                  Ganador: {snapshot.winnerUsername}
                </Text>
              ) : null}
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

            {snapshot.finished ? (
              <View className="gap-3 rounded-lg border border-[#cde7d8] bg-[#f0fdf4] px-4 py-4">
                <Text className="text-sm font-semibold leading-5 text-[#15803d]">
                  El combate ha terminado. Puedes volver a la sala para preparar otra partida.
                </Text>
                <Pressable
                  className={`min-h-11 items-center justify-center rounded-lg bg-[#15803d] px-4 active:opacity-80 ${
                    resettingBattle ? 'opacity-65' : ''
                  }`}
                  disabled={resettingBattle}
                  onPress={() => void resetBattle()}>
                  {resettingBattle ? (
                    <ActivityIndicator color="#ffffff" />
                  ) : (
                    <Text className="text-sm font-extrabold text-white">Volver a sala</Text>
                  )}
                </Pressable>
              </View>
            ) : activeCreatureFainted ? (
              <View className="rounded-lg border border-[#f7d6bf] bg-[#fff4ed] px-4 py-4">
                <Text className="text-sm font-semibold leading-5 text-beasts-warning">
                  Tu JaBea esta debilitado. Elige un sustituto para continuar.
                </Text>
              </View>
            ) : null}

            {!snapshot.finished ? (
              <>
              <View className="gap-3">
                {!activeCreatureFainted && attackMoves.length ? attackMoves.map((move) => (
                <Pressable
                  key={`attack-${move.slot}`}
                  className={`min-h-[92px] flex-row items-center justify-between rounded-lg bg-beasts-blue px-5 active:opacity-80 ${
                    actionLoading || currentPlayerActionSubmitted || snapshot.finished ? 'opacity-45' : ''
                  }`}
                  disabled={actionLoading || currentPlayerActionSubmitted || snapshot.finished}
                  onPress={() => void submitAttack(move.slot)}>
                  <View>
                    <Text className="text-2xl font-extrabold text-white">{move.name}</Text>
                    <Text className="mt-1 text-sm font-semibold text-white/80">
                      {formatMoveInfo(move)}
                    </Text>
                    {move.specialEffect ? (
                      <Text className="mt-1 max-w-[230px] text-xs font-semibold text-white/70" numberOfLines={1}>
                        {move.specialEffect}
                      </Text>
                    ) : null}
                  </View>
                  {actionLoading ? (
                    <ActivityIndicator color="#ffffff" />
                  ) : (
                    <Ionicons name="arrow-forward-circle" size={34} color="#ffffff" />
                  )}
                </Pressable>
              )) : !activeCreatureFainted ? (
                <View className="rounded-lg border border-[#f7d6bf] bg-[#fff4ed] px-4 py-4">
                  <Text className="text-sm font-semibold leading-5 text-beasts-warning">
                    No se pudieron cargar los movimientos de este JaBea.
                  </Text>
                </View>
              ) : null}
              </View>

              <View className="gap-3">
                {switchOptions.map((option) => (
                <Pressable
                  key={`switch-${option.slot}`}
                  className={`min-h-[72px] flex-row items-center justify-between rounded-lg border border-[#7c3aed] bg-white px-5 active:opacity-80 ${
                    actionLoading || currentPlayerActionSubmitted || snapshot.finished || option.currentHealth <= 0
                      ? 'opacity-45'
                      : ''
                  }`}
                  disabled={actionLoading || currentPlayerActionSubmitted || snapshot.finished || option.currentHealth <= 0}
                  onPress={() => void submitSwitch(option.slot)}>
                  <View>
                    <View className="flex-row items-center gap-3">
                      <Image
                        className="h-10 w-10"
                        resizeMode="contain"
                        source={getJaBeaImage(option.name)}
                      />
                      <View>
                        <Text className="text-lg font-extrabold text-[#7c3aed]">{option.name}</Text>
                        <Text className="mt-1 text-sm font-semibold text-beasts-muted">
                          Slot {option.slot}{option.typeName ? ` · ${option.typeName}` : ''}
                        </Text>
                        <Text className="mt-1 text-xs font-bold text-beasts-muted">
                          Vida {option.currentHealth} / {option.maxHealth}
                          {option.currentHealth <= 0 ? ' · Debilitado' : ''}
                        </Text>
                      </View>
                    </View>
                  </View>
                  {actionLoading ? (
                    <ActivityIndicator color="#7c3aed" />
                  ) : (
                    <Ionicons name="swap-horizontal" size={28} color="#7c3aed" />
                  )}
                </Pressable>
                ))}
              </View>
              </>
            ) : null}
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

function toSwitchOptions(slots: BattleCreatureSnapshotResponse[], activeSlot?: number | null): SwitchOption[] {
  return slots
    .filter((slot) => slot.slot !== activeSlot)
    .map((slot) => ({
      slot: slot.slot,
      name: slot.name ?? `Slot ${slot.slot}`,
      typeName: slot.moves?.[0]?.typeName ?? null,
      currentHealth: slot.currentHealth ?? 0,
      maxHealth: slot.maxHealth ?? 0,
    }));
}

function toBattleMove(slot: number, move?: MoveSummaryResponse | null): BattleMoveSnapshotResponse | null {
  if (!move) {
    return null;
  }

  return {
    slot,
    moveId: move.moveId,
    name: move.name,
    typeId: move.typeId,
    typeName: move.typeName,
    damage: move.damage,
    accuracy: move.accuracy,
    specialEffect: move.specialEffect ?? null,
  };
}

function formatMoveInfo(move: BattleMoveSnapshotResponse) {
  const typeLabel = move.typeName ?? 'Sin tipo';
  return `${typeLabel} · Daño ${move.damage} · Precision ${move.accuracy}%`;
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
  const typeName = creature.moves?.[0]?.typeName ?? 'Tipo';
  const typeIcon = getTypeIcon(typeName);
  const typeColor = TYPE_COLORS[typeName] ?? accentColor;

  return (
    <View className="rounded-lg bg-white p-4 shadow-lg shadow-beasts-ink/10">
      <View className="flex-row items-center justify-between gap-3">
        <Image
          className="mr-1 h-20 w-20"
          resizeMode="contain"
          source={getJaBeaImage(creature.name)}
        />
        <View className="flex-1">
          <Text className="text-xs font-extrabold uppercase text-beasts-muted">{label}</Text>
          <Text className="mt-1 text-lg font-extrabold text-beasts-ink">{creature.name}</Text>
          <Text className="mt-1 text-sm font-semibold text-beasts-muted" numberOfLines={1}>
            {username} · {teamName}
          </Text>
        </View>
        <View className="min-h-11 min-w-11 items-center justify-center rounded-lg px-2" style={{ backgroundColor: typeColor }}>
          <Ionicons name={typeIcon} size={20} color="#ffffff" />
          <Text className="mt-0.5 text-[9px] font-extrabold uppercase text-white" numberOfLines={1}>
            {typeName}
          </Text>
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

function getTypeIcon(typeName?: string | null): IoniconName {
  switch (typeName) {
    case 'Agua':
      return 'water-outline';
    case 'Electrico':
      return 'flash-outline';
    case 'Fuego':
      return 'flame-outline';
    case 'Planta':
      return 'leaf-outline';
    default:
      return 'radio-button-off-outline';
  }
}

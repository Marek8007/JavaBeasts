import { getJaBeaAvailableMovesAction, getJaBeasCatalogAction } from '@/actions/jabea.actions';
import {
  deleteTeamSlotAction,
  getTeamCompositionAction,
  upsertTeamSlotAction,
} from '@/actions/team-composition.actions';
import {
  JaBeaAvailableMovesResponse,
  JaBeaCatalogResponse,
  JaBeaMoveResponse,
} from '@/interfaces/jabea.interface';
import { TeamCompositionResponse, TeamSlotResponse } from '@/interfaces/team-composition.interface';
import { useAuthStore } from '@/stores/authStore';
import { Ionicons } from '@expo/vector-icons';
import { router, useLocalSearchParams } from 'expo-router';
import { ComponentProps, useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  FlatList,
  Modal,
  Pressable,
  RefreshControl,
  SafeAreaView,
  ScrollView,
  Text,
  View,
} from 'react-native';

type IoniconName = ComponentProps<typeof Ionicons>['name'];

const DEFAULT_TEAM_ICON: IoniconName = 'paw-outline';
const TYPE_STYLES: Record<string, { badge: string; border: string; row: string; text: string }> = {
  Agua: {
    badge: 'bg-[#dbeafe]',
    border: 'border-[#93c5fd]',
    row: 'bg-[#eff6ff]',
    text: 'text-[#1d4ed8]',
  },
  Electrico: {
    badge: 'bg-[#fef3c7]',
    border: 'border-[#facc15]',
    row: 'bg-[#fffbeb]',
    text: 'text-[#a16207]',
  },
  Fuego: {
    badge: 'bg-[#ffedd5]',
    border: 'border-[#fb923c]',
    row: 'bg-[#fff7ed]',
    text: 'text-[#c2410c]',
  },
  Normal: {
    badge: 'bg-[#e5e7eb]',
    border: 'border-[#cbd5e1]',
    row: 'bg-[#f8fafc]',
    text: 'text-[#475569]',
  },
  Planta: {
    badge: 'bg-[#dcfce7]',
    border: 'border-[#86efac]',
    row: 'bg-[#f0fdf4]',
    text: 'text-[#15803d]',
  },
};

const DEFAULT_TYPE_STYLE = {
  badge: 'bg-[#e8edf5]',
  border: 'border-beasts-line',
  row: 'bg-white',
  text: 'text-beasts-blue',
};

export default function TeamCompositionScreen() {
  const user = useAuthStore((state) => state.user);
  const params = useLocalSearchParams<{
    iconName?: string;
    teamId?: string;
    teamName?: string;
  }>();
  const teamId = Number(params.teamId);
  const iconName = (params.iconName || DEFAULT_TEAM_ICON) as IoniconName;
  const fallbackTeamName = params.teamName || 'Equipo';
  const [composition, setComposition] = useState<TeamCompositionResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [deletingSlot, setDeletingSlot] = useState<number | null>(null);
  const [selectedSlot, setSelectedSlot] = useState<number | null>(null);
  const [jabeas, setJabeas] = useState<JaBeaCatalogResponse[]>([]);
  const [jabeasLoading, setJabeasLoading] = useState(false);
  const [jabeasError, setJabeasError] = useState('');
  const [selectedJaBea, setSelectedJaBea] = useState<JaBeaCatalogResponse | null>(null);
  const [availableMoves, setAvailableMoves] = useState<JaBeaAvailableMovesResponse | null>(null);
  const [movesLoading, setMovesLoading] = useState(false);
  const [move1Id, setMove1Id] = useState<number | null>(null);
  const [move2Id, setMove2Id] = useState<number | null>(null);
  const [savingSlot, setSavingSlot] = useState(false);
  const [slotError, setSlotError] = useState('');
  const [error, setError] = useState('');

  const loadComposition = useCallback(async () => {
    if (!user || !Number.isFinite(teamId)) {
      setLoading(false);
      return;
    }

    setError('');

    try {
      const data = await getTeamCompositionAction(user.userId, teamId);
      setComposition(data);
    } catch (requestError: any) {
      const message =
        requestError?.response?.data?.message ??
        requestError?.response?.data?.detail ??
        requestError?.message ??
        'No se pudo cargar el equipo';
      setError(String(message));
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [teamId, user]);

  useEffect(() => {
    void loadComposition();
  }, [loadComposition]);

  function refreshComposition() {
    setRefreshing(true);
    void loadComposition();
  }

  async function deleteSlot(slot: number) {
    if (!user || deletingSlot !== null || !Number.isFinite(teamId)) {
      return;
    }

    setDeletingSlot(slot);
    setError('');

    try {
      const data = await deleteTeamSlotAction(user.userId, teamId, slot);
      setComposition(data);
    } catch (requestError: any) {
      const message =
        requestError?.response?.data?.message ??
        requestError?.response?.data?.detail ??
        requestError?.message ??
        'No se pudo vaciar el slot';
      setError(String(message));
    } finally {
      setDeletingSlot(null);
    }
  }

  function confirmDeleteSlot(slot: number) {
    Alert.alert('Vaciar slot', 'Quieres quitar este JaBea del equipo?', [
      { text: 'Cancelar', style: 'cancel' },
      { text: 'Vaciar', style: 'destructive', onPress: () => void deleteSlot(slot) },
    ]);
  }

  async function openJaBeaPicker(slot: number) {
    setSelectedSlot(slot);
    setJabeasError('');
    resetSlotSelection();

    if (jabeas.length > 0) {
      return;
    }

    setJabeasLoading(true);

    try {
      const data = await getJaBeasCatalogAction();
      setJabeas(data);
    } catch (requestError: any) {
      const message =
        requestError?.response?.data?.message ??
        requestError?.response?.data?.detail ??
        requestError?.message ??
        'No se pudieron cargar los JaBeas';
      setJabeasError(String(message));
    } finally {
      setJabeasLoading(false);
    }
  }

  function closeJaBeaPicker() {
    setSelectedSlot(null);
    setJabeasError('');
    resetSlotSelection();
  }

  function resetSlotSelection() {
    setSelectedJaBea(null);
    setAvailableMoves(null);
    setMovesLoading(false);
    setMove1Id(null);
    setMove2Id(null);
    setSavingSlot(false);
    setSlotError('');
  }

  async function selectJaBea(jaBea: JaBeaCatalogResponse) {
    setSelectedJaBea(jaBea);
    setSlotError('');
    setMove1Id(null);
    setMove2Id(null);
    setMovesLoading(true);

    try {
      const data = await getJaBeaAvailableMovesAction(jaBea.jaBeasId);
      setAvailableMoves(data);
    } catch (requestError: any) {
      const message =
        requestError?.response?.data?.message ??
        requestError?.response?.data?.detail ??
        requestError?.message ??
        'No se pudieron cargar los movimientos';
      setSlotError(String(message));
    } finally {
      setMovesLoading(false);
    }
  }

  async function saveSelectedSlot() {
    if (!user || selectedSlot === null || !selectedJaBea || savingSlot || !Number.isFinite(teamId)) {
      return;
    }

    if (!move1Id || !move2Id) {
      setSlotError('Elige los dos movimientos configurables.');
      return;
    }

    if (move1Id === move2Id) {
      setSlotError('Los dos movimientos deben ser distintos.');
      return;
    }

    setSavingSlot(true);
    setSlotError('');

    try {
      const data = await upsertTeamSlotAction(
        user.userId,
        teamId,
        selectedSlot,
        selectedJaBea.jaBeasId,
        move1Id,
        move2Id
      );
      setComposition(data);
      closeJaBeaPicker();
    } catch (requestError: any) {
      const message =
        requestError?.response?.data?.message ??
        requestError?.response?.data?.detail ??
        requestError?.message ??
        'No se pudo guardar el slot';
      setSlotError(String(message));
    } finally {
      setSavingSlot(false);
    }
  }

  const teamName = composition?.name ?? fallbackTeamName;
  const slots = composition?.slots ?? [];

  return (
    <SafeAreaView className="flex-1 bg-beasts-soft">
      <View className="flex-1 px-[18px] pt-6">
        <View className="mb-[18px] flex-row items-center gap-3">
          <Pressable
            className="h-[42px] w-[42px] items-center justify-center rounded-lg bg-white active:opacity-80"
            onPress={() => router.back()}>
            <Ionicons name="chevron-back" size={22} color="#1e4f8f" />
          </Pressable>

          <View className="h-[46px] w-[46px] items-center justify-center rounded-lg bg-beasts-blue">
            <Ionicons name={iconName} size={24} color="#ffffff" />
          </View>

          <View className="flex-1">
            <Text className="text-2xl font-extrabold text-beasts-ink" numberOfLines={1}>
              {teamName}
            </Text>
            <Text className="mt-0.5 text-sm text-beasts-muted">Composicion del equipo</Text>
          </View>
        </View>

        {loading ? (
          <View className="flex-1 items-center justify-center px-6">
            <ActivityIndicator color="#1e4f8f" />
            <Text className="mt-2 text-center text-sm leading-5 text-beasts-muted">
              Cargando composicion...
            </Text>
          </View>
        ) : error ? (
          <View className="flex-1 items-center justify-center px-6">
            <Ionicons name="warning-outline" size={28} color="#b54708" />
            <Text className="mt-2.5 text-center text-[17px] font-extrabold text-beasts-ink">
              No se ha podido cargar
            </Text>
            <Text className="mt-2 text-center text-sm leading-5 text-beasts-muted">{error}</Text>
            <Pressable
              className="mt-4 min-h-11 items-center justify-center rounded-lg bg-beasts-blue px-[18px] active:opacity-80"
              onPress={refreshComposition}>
              <Text className="text-sm font-extrabold text-white">Reintentar</Text>
            </Pressable>
          </View>
        ) : (
          <FlatList
            contentContainerClassName="gap-3 pb-4"
            data={slots}
            keyExtractor={(item) => String(item.slot)}
            refreshControl={
              <RefreshControl refreshing={refreshing} tintColor="#1e4f8f" onRefresh={refreshComposition} />
            }
            renderItem={({ item }) => (
              <TeamSlotCard
                deleting={deletingSlot === item.slot}
                onAdd={() => void openJaBeaPicker(item.slot)}
                onDelete={() => confirmDeleteSlot(item.slot)}
                slot={item}
              />
            )}
          />
        )}

        <Modal
          animationType="fade"
          transparent
          visible={selectedSlot !== null}
          onRequestClose={closeJaBeaPicker}>
          <View className="flex-1 justify-end bg-[rgba(23,32,51,0.45)]">
            <View className="h-[92%] rounded-t-lg bg-white px-[18px] pb-5 pt-4">
              <View className="mb-4 flex-row items-center justify-between gap-4">
                <View className="flex-1">
                  <Text className="text-xl font-extrabold text-beasts-ink">
                    {selectedJaBea ? 'Configurar JaBea' : 'Anadir JaBea'}
                  </Text>
                  <Text className="mt-1 text-sm text-beasts-muted">
                    {selectedJaBea ? selectedJaBea.name : `Slot ${selectedSlot}`}
                  </Text>
                </View>
                <Pressable
                  className="h-10 w-10 items-center justify-center rounded-lg bg-[#e8edf5] active:opacity-80"
                  onPress={closeJaBeaPicker}>
                  <Ionicons name="close" size={22} color="#1e4f8f" />
                </Pressable>
              </View>

              {jabeasLoading ? (
                <View className="min-h-[260px] items-center justify-center">
                  <ActivityIndicator color="#1e4f8f" />
                  <Text className="mt-2 text-sm text-beasts-muted">Cargando JaBeas...</Text>
                </View>
              ) : jabeasError ? (
                <View className="min-h-[260px] items-center justify-center px-6">
                  <Ionicons name="warning-outline" size={28} color="#b54708" />
                  <Text className="mt-2 text-center text-sm leading-5 text-beasts-muted">
                    {jabeasError}
                  </Text>
                  <Pressable
                    className="mt-4 min-h-11 items-center justify-center rounded-lg bg-beasts-blue px-[18px] active:opacity-80"
                    onPress={() => selectedSlot !== null && void openJaBeaPicker(selectedSlot)}>
                    <Text className="text-sm font-extrabold text-white">Reintentar</Text>
                  </Pressable>
                </View>
              ) : (
                <View className="flex-1">
                  {selectedJaBea ? (
                    <SlotMoveEditor
                      availableMoves={availableMoves?.configurableMoves ?? []}
                      loading={movesLoading}
                      move1Id={move1Id}
                      move2Id={move2Id}
                      onBack={resetSlotSelection}
                      onMove1Change={(moveId) => {
                        setMove1Id(moveId);
                        setSlotError('');
                      }}
                      onMove2Change={(moveId) => {
                        setMove2Id(moveId);
                        setSlotError('');
                      }}
                      onSave={() => void saveSelectedSlot()}
                      saving={savingSlot}
                      selectedJaBea={selectedJaBea}
                      slotError={slotError}
                    />
                  ) : (
                    <FlatList
                      contentContainerClassName="gap-3 pb-2"
                      data={jabeas}
                      keyExtractor={(item) => String(item.jaBeasId)}
                      renderItem={({ item }) => (
                        <JaBeaPickerRow jaBea={item} onPress={() => void selectJaBea(item)} />
                      )}
                    />
                  )}
                </View>
              )}
            </View>
          </View>
        </Modal>
      </View>
    </SafeAreaView>
  );
}

function TeamSlotCard({
  deleting,
  onAdd,
  onDelete,
  slot,
}: {
  deleting: boolean;
  onAdd: () => void;
  onDelete: () => void;
  slot: TeamSlotResponse;
}) {
  const member = slot.member;

  if (!member) {
    return (
      <Pressable
        className="min-h-[96px] flex-row items-center rounded-lg border border-dashed border-beasts-line bg-white px-4 py-3 active:opacity-80"
        onPress={onAdd}>
        <View className="mr-3 h-12 w-12 items-center justify-center rounded-lg bg-[#e8edf5]">
          <Text className="text-base font-extrabold text-beasts-blue">{slot.slot}</Text>
        </View>
        <View className="flex-1">
          <Text className="text-base font-extrabold text-beasts-ink">Slot vacio</Text>
          <Text className="mt-1 text-sm leading-5 text-beasts-muted">
            Aqui se podra anadir un JaBea al equipo.
          </Text>
        </View>
        <Ionicons name="add-circle-outline" size={24} color="#68758a" />
      </Pressable>
    );
  }

  return (
    <View className="rounded-lg bg-white px-4 py-3">
      <View className="flex-row items-center">
        <View className="mr-3 h-12 w-12 items-center justify-center rounded-lg bg-beasts-blue">
          <Text className="text-base font-extrabold text-white">{slot.slot}</Text>
        </View>
        <View className="flex-1">
          <Text className="text-base font-extrabold text-beasts-ink" numberOfLines={1}>
            {member.name}
          </Text>
          <Text className="mt-1 text-sm text-beasts-muted">{member.typeName}</Text>
        </View>
        <Pressable
          className="h-10 w-10 items-center justify-center active:opacity-75"
          disabled={deleting}
          onPress={onDelete}>
          {deleting ? (
            <ActivityIndicator color="#b54708" />
          ) : (
            <Ionicons name="trash-outline" size={20} color="#b54708" />
          )}
        </Pressable>
      </View>

      <View className="mt-3 gap-2">
        {member.uniqueMove ? <MoveLine label="Unico" name={member.uniqueMove.name} /> : null}
        <MoveLine label="Mov. 1" name={member.move1.name} />
        <MoveLine label="Mov. 2" name={member.move2.name} />
      </View>
    </View>
  );
}

function JaBeaPickerRow({
  jaBea,
  onPress,
}: {
  jaBea: JaBeaCatalogResponse;
  onPress: () => void;
}) {
  const typeStyle = TYPE_STYLES[jaBea.typeName] ?? DEFAULT_TYPE_STYLE;

  return (
    <View className={`overflow-hidden rounded-lg border ${typeStyle.border} ${typeStyle.row}`}>
      <Pressable className="min-h-[76px] flex-row items-center px-4 py-3 active:opacity-80" onPress={onPress}>
        <View className={`mr-3 rounded-lg px-3 py-2 ${typeStyle.badge}`}>
          <Text className={`text-xs font-extrabold uppercase ${typeStyle.text}`}>{jaBea.typeName}</Text>
        </View>

        <View className="flex-1">
          <Text className="text-base font-extrabold text-beasts-ink" numberOfLines={1}>
            {jaBea.name}
          </Text>
          <Text className="mt-1 text-xs font-semibold text-beasts-muted">
            PV {jaBea.health} · ATQ {jaBea.damage} · DEF {jaBea.defence} · VEL {jaBea.speed}
          </Text>
        </View>

        <Ionicons name="chevron-forward" size={20} color="#68758a" />
      </Pressable>
    </View>
  );
}

function SlotMoveEditor({
  availableMoves,
  loading,
  move1Id,
  move2Id,
  onBack,
  onMove1Change,
  onMove2Change,
  onSave,
  saving,
  selectedJaBea,
  slotError,
}: {
  availableMoves: JaBeaMoveResponse[];
  loading: boolean;
  move1Id: number | null;
  move2Id: number | null;
  onBack: () => void;
  onMove1Change: (moveId: number) => void;
  onMove2Change: (moveId: number) => void;
  onSave: () => void;
  saving: boolean;
  selectedJaBea: JaBeaCatalogResponse;
  slotError: string;
}) {
  const typeStyle = TYPE_STYLES[selectedJaBea.typeName] ?? DEFAULT_TYPE_STYLE;

  return (
    <View className="flex-1 gap-3">
      <Pressable className="self-start flex-row items-center gap-1.5 py-1 active:opacity-75" onPress={onBack}>
        <Ionicons name="chevron-back" size={18} color="#1e4f8f" />
        <Text className="text-sm font-extrabold text-beasts-blue">Cambiar JaBea</Text>
      </Pressable>

      <View className={`rounded-lg border ${typeStyle.border} ${typeStyle.row} p-4`}>
        <View className="flex-row items-center justify-between gap-3">
          <View className="flex-1">
            <Text className="text-xl font-extrabold text-beasts-ink" numberOfLines={1}>
              {selectedJaBea.name}
            </Text>
            <Text className="mt-1 text-sm leading-5 text-[#42506a]">{selectedJaBea.description}</Text>
          </View>
          <View className={`rounded-lg px-3 py-2 ${typeStyle.badge}`}>
            <Text className={`text-xs font-extrabold uppercase ${typeStyle.text}`}>
              {selectedJaBea.typeName}
            </Text>
          </View>
        </View>

        <View className="mt-3 flex-row gap-2">
          <StatBox label="Vida" value={selectedJaBea.health} />
          <StatBox label="Dano" value={selectedJaBea.damage} />
          <StatBox label="Def." value={selectedJaBea.defence} />
          <StatBox label="Vel." value={selectedJaBea.speed} />
        </View>

        {selectedJaBea.uniqueMove ? (
          <View className="mt-3 rounded-lg bg-white px-3 py-3">
            <Text className="text-xs font-extrabold uppercase text-beasts-muted">Movimiento unico</Text>
            <Text className="mt-1 text-sm font-extrabold text-beasts-ink">
              {selectedJaBea.uniqueMove.name}
            </Text>
            <Text className="mt-1 text-xs leading-4 text-beasts-muted">
              Dano {selectedJaBea.uniqueMove.damage} · Precision {selectedJaBea.uniqueMove.accuracy}
            </Text>
            {selectedJaBea.uniqueMove.description ? (
              <Text className="mt-2 text-xs leading-4 text-[#42506a]">
                {selectedJaBea.uniqueMove.description}
              </Text>
            ) : null}
          </View>
        ) : null}
      </View>

      <View className="flex-1 rounded-lg border border-beasts-line bg-[#f8fafc] p-3">
        <Text className="text-sm font-extrabold text-beasts-ink">Elige sus movimientos</Text>

        {loading ? (
          <View className="min-h-[120px] items-center justify-center">
            <ActivityIndicator color="#1e4f8f" />
            <Text className="mt-2 text-sm text-beasts-muted">Cargando movimientos...</Text>
          </View>
        ) : (
          <View className="mt-3 flex-1 gap-3">
            <View className="flex-1 flex-row gap-3">
              <MovePicker
                moves={availableMoves}
                selectedMoveId={move1Id}
                title="Movimiento 1"
                unavailableMoveId={move2Id}
                onSelect={onMove1Change}
              />
              <MovePicker
                moves={availableMoves}
                selectedMoveId={move2Id}
                title="Movimiento 2"
                unavailableMoveId={move1Id}
                onSelect={onMove2Change}
              />
            </View>

            {slotError ? (
              <View className="rounded-lg border border-[#f7d6bf] bg-[#fff4ed] px-3 py-2">
                <Text className="text-sm font-semibold leading-5 text-beasts-warning">{slotError}</Text>
              </View>
            ) : null}

            <Pressable
              className={`min-h-11 items-center justify-center rounded-lg bg-beasts-blue px-4 active:opacity-80 ${
                saving ? 'opacity-65' : ''
              }`}
              disabled={saving}
              onPress={onSave}>
              {saving ? (
                <ActivityIndicator color="#ffffff" />
              ) : (
                <Text className="text-sm font-extrabold text-white">Guardar slot</Text>
              )}
            </Pressable>
          </View>
        )}
      </View>
    </View>
  );
}

function MovePicker({
  moves,
  onSelect,
  selectedMoveId,
  title,
  unavailableMoveId,
}: {
  moves: JaBeaMoveResponse[];
  onSelect: (moveId: number) => void;
  selectedMoveId: number | null;
  title: string;
  unavailableMoveId: number | null;
}) {
  return (
    <View className="flex-1">
      <Text className="mb-2 text-xs font-extrabold uppercase text-beasts-muted">{title}</Text>
      <ScrollView
        className="max-h-[172px]"
        contentContainerClassName="gap-2"
        nestedScrollEnabled
        showsVerticalScrollIndicator>
        {moves.map((move) => {
          const selected = move.moveId === selectedMoveId;
          const unavailable = move.moveId === unavailableMoveId;

          return (
            <Pressable
              className={`rounded-lg border px-3 py-2 active:opacity-80 ${
                selected ? 'border-beasts-blue bg-[#e8edf5]' : 'border-beasts-line bg-white'
              } ${unavailable ? 'opacity-45' : ''}`}
              disabled={unavailable}
              key={move.moveId}
              onPress={() => onSelect(move.moveId)}>
              <View className="flex-row items-center justify-between gap-3">
                <Text className="flex-1 text-sm font-extrabold text-beasts-ink" numberOfLines={1}>
                  {move.name}
                </Text>
                <Text className="text-xs font-bold text-beasts-muted">{move.typeName}</Text>
              </View>
              <Text className="mt-1 text-xs text-beasts-muted">
                Dano {move.damage} · Precision {move.accuracy}
              </Text>
            </Pressable>
          );
        })}
      </ScrollView>
    </View>
  );
}

function StatBox({ label, value }: { label: string; value: number }) {
  return (
    <View className="flex-1 rounded-lg bg-white px-2 py-2">
      <Text className="text-center text-[11px] font-extrabold uppercase text-beasts-muted">{label}</Text>
      <Text className="mt-1 text-center text-sm font-extrabold text-beasts-ink">{value}</Text>
    </View>
  );
}

function MoveLine({ label, name }: { label: string; name: string }) {
  return (
    <View className="flex-row items-center justify-between rounded-lg bg-[#f8fafc] px-3 py-2">
      <Text className="text-xs font-extrabold uppercase text-beasts-muted">{label}</Text>
      <Text className="ml-3 flex-1 text-right text-sm font-semibold text-beasts-ink" numberOfLines={1}>
        {name}
      </Text>
    </View>
  );
}

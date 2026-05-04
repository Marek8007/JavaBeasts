import {
  deleteTeamSlotAction,
  getTeamCompositionAction,
} from '@/actions/team-composition.actions';
import { TeamCompositionResponse, TeamSlotResponse } from '@/interfaces/team-composition.interface';
import { useAuthStore } from '@/stores/authStore';
import { Ionicons } from '@expo/vector-icons';
import { router, useLocalSearchParams } from 'expo-router';
import { ComponentProps, useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  FlatList,
  Pressable,
  RefreshControl,
  SafeAreaView,
  Text,
  View,
} from 'react-native';

type IoniconName = ComponentProps<typeof Ionicons>['name'];

const DEFAULT_TEAM_ICON: IoniconName = 'paw-outline';

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
                onDelete={() => confirmDeleteSlot(item.slot)}
                slot={item}
              />
            )}
          />
        )}
      </View>
    </SafeAreaView>
  );
}

function TeamSlotCard({
  deleting,
  onDelete,
  slot,
}: {
  deleting: boolean;
  onDelete: () => void;
  slot: TeamSlotResponse;
}) {
  const member = slot.member;

  if (!member) {
    return (
      <View className="min-h-[96px] flex-row items-center rounded-lg border border-dashed border-beasts-line bg-white px-4 py-3">
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
      </View>
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

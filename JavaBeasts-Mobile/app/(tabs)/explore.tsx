import {
  activateTeamAction,
  createTeamAction,
  getTeamsByUserAction,
  renameTeamAction,
} from '@/actions/team.actions';
import { TeamListRow } from '@/components/ui/team-list-row';
import { TeamResponse } from '@/interfaces/team.interface';
import { useAuthStore } from '@/stores/authStore';
import { Ionicons } from '@expo/vector-icons';
import { ComponentProps, useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  Modal,
  Pressable,
  RefreshControl,
  Text,
  TextInput,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

type IoniconName = ComponentProps<typeof Ionicons>['name'];

const DEFAULT_TEAM_ICON: IoniconName = 'paw-outline';
const TEAM_ICON_NAMES = Object.keys(
  (Ionicons as unknown as { glyphMap: Record<string, number> }).glyphMap
).sort() as IoniconName[];

export default function TeamsScreen() {
  const user = useAuthStore((state) => state.user);
  const [teams, setTeams] = useState<TeamResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [activatingTeamId, setActivatingTeamId] = useState<number | null>(null);
  const [savingTeam, setSavingTeam] = useState(false);
  const [editingTeam, setEditingTeam] = useState<TeamResponse | null>(null);
  const [teamName, setTeamName] = useState('');
  const [selectedIconName, setSelectedIconName] = useState<IoniconName>(DEFAULT_TEAM_ICON);
  const [modalVisible, setModalVisible] = useState(false);
  const [modalError, setModalError] = useState('');
  const [error, setError] = useState('');

  const loadTeams = useCallback(async () => {
    if (!user) {
      setTeams([]);
      setLoading(false);
      return;
    }

    setError('');

    try {
      const userTeams = await getTeamsByUserAction(user.userId);
      setTeams(userTeams);
    } catch (requestError: any) {
      const message =
        requestError?.response?.data?.message ??
        requestError?.response?.data?.detail ??
        requestError?.message ??
        'No se pudieron cargar los equipos';
      setError(String(message));
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [user]);

  useEffect(() => {
    void loadTeams();
  }, [loadTeams]);

  function refreshTeams() {
    setRefreshing(true);
    void loadTeams();
  }

  async function activateTeam(team: TeamResponse) {
    if (!user || team.active || activatingTeamId !== null) {
      return;
    }

    setActivatingTeamId(team.teamId);
    setError('');

    try {
      await activateTeamAction(user.userId, team.teamId);
      await loadTeams();
    } catch (requestError: any) {
      const message =
        requestError?.response?.data?.message ??
        requestError?.response?.data?.detail ??
        requestError?.message ??
        'No se pudo activar el equipo';
      setError(String(message));
    } finally {
      setActivatingTeamId(null);
    }
  }

  function openCreateModal() {
    setEditingTeam(null);
    setTeamName('');
    setSelectedIconName(DEFAULT_TEAM_ICON);
    setModalError('');
    setModalVisible(true);
  }

  function openRenameModal(team: TeamResponse) {
    setEditingTeam(team);
    setTeamName(team.name);
    setSelectedIconName((team.iconName || DEFAULT_TEAM_ICON) as IoniconName);
    setModalError('');
    setModalVisible(true);
  }

  function closeModal() {
    if (savingTeam) {
      return;
    }

    setModalVisible(false);
    setEditingTeam(null);
    setTeamName('');
    setSelectedIconName(DEFAULT_TEAM_ICON);
    setModalError('');
  }

  async function saveTeam() {
    if (!user || savingTeam) {
      return;
    }

    const trimmedName = teamName.trim();

    if (trimmedName.length < 3 || trimmedName.length > 50) {
      setModalError('El nombre del equipo debe tener entre 3 y 50 caracteres.');
      return;
    }

    setSavingTeam(true);
    setModalError('');

    try {
      if (editingTeam) {
        await renameTeamAction(user.userId, editingTeam.teamId, trimmedName, selectedIconName);
      } else {
        await createTeamAction(user.userId, trimmedName, selectedIconName);
      }

      setModalVisible(false);
      setEditingTeam(null);
      setTeamName('');
      setSelectedIconName(DEFAULT_TEAM_ICON);
      setModalError('');
      await loadTeams();
    } catch (requestError: any) {
      const message =
        requestError?.response?.data?.message ??
        requestError?.response?.data?.detail ??
        requestError?.message ??
        'No se pudo guardar el equipo';
      setModalError(String(message));
    } finally {
      setSavingTeam(false);
    }
  }

  const activeTeam = teams.find((team) => team.active);

  return (
    <SafeAreaView className="flex-1 bg-beasts-soft" edges={['top']}>
      <View className="flex-1 px-[18px] pt-6">
        <View className="mb-[18px] flex-row items-center justify-between">
          <View>
            <Text className="text-3xl font-extrabold text-beasts-ink">Equipos</Text>
            <Text className="mt-1 max-w-[270px] text-sm text-beasts-muted">
              {activeTeam ? `Activo: ${activeTeam.name}` : 'Selecciona un equipo activo antes de jugar.'}
            </Text>
          </View>

          <View className="flex-row gap-2.5">
            <Pressable
              className="h-[42px] w-[42px] items-center justify-center rounded-lg bg-white active:opacity-80"
              onPress={openCreateModal}>
              <Ionicons name="add" size={22} color="#1e4f8f" />
            </Pressable>
            <Pressable
              className="h-[42px] w-[42px] items-center justify-center rounded-lg bg-white active:opacity-80"
              onPress={refreshTeams}>
              <Ionicons name="refresh" size={20} color="#1e4f8f" />
            </Pressable>
          </View>
        </View>

        {loading ? (
          <View className="flex-1 items-center justify-center px-6">
            <ActivityIndicator color="#1e4f8f" />
            <Text className="mt-2 text-center text-sm leading-5 text-beasts-muted">
              Cargando equipos...
            </Text>
          </View>
        ) : error ? (
          <View className="flex-1 items-center justify-center px-6">
            <Ionicons name="warning-outline" size={28} color="#b54708" />
            <Text className="mt-2.5 text-center text-[17px] font-extrabold text-beasts-ink">
              No se han podido cargar
            </Text>
            <Text className="mt-2 text-center text-sm leading-5 text-beasts-muted">{error}</Text>
            <Pressable
              className="mt-4 min-h-11 items-center justify-center rounded-lg bg-beasts-blue px-[18px] active:opacity-80"
              onPress={refreshTeams}>
              <Text className="text-sm font-extrabold text-white">Reintentar</Text>
            </Pressable>
          </View>
        ) : (
          <FlatList
            className="rounded-lg bg-white"
            contentContainerClassName={teams.length === 0 ? 'grow justify-center' : 'py-1'}
            data={teams}
            keyExtractor={(team) => String(team.teamId)}
            refreshControl={
              <RefreshControl refreshing={refreshing} tintColor="#1e4f8f" onRefresh={refreshTeams} />
            }
            renderItem={({ item }) => (
              <TeamListRow
                activating={activatingTeamId === item.teamId}
                disabled={activatingTeamId !== null}
                onActivate={() => void activateTeam(item)}
                onRename={() => openRenameModal(item)}
                team={item}
              />
            )}
            ListEmptyComponent={
              <View className="flex-1 items-center justify-center px-6">
                <Ionicons name="albums-outline" size={30} color="#68758a" />
                <Text className="mt-2.5 text-center text-[17px] font-extrabold text-beasts-ink">
                  Todavia no tienes equipos
                </Text>
                <Text className="mt-2 text-center text-sm leading-5 text-beasts-muted">
                  Crea tu primer equipo desde la gestion de equipos.
                </Text>
              </View>
            }
          />
        )}

        <Modal animationType="fade" transparent visible={modalVisible} onRequestClose={closeModal}>
          <View className="flex-1 items-center justify-center bg-[rgba(23,32,51,0.45)] px-5">
            <View className="max-h-[88%] w-full gap-4 rounded-lg bg-white p-[18px]">
              <View className="flex-row items-center justify-between gap-4">
                <View className="flex-1">
                  <Text className="text-xl font-extrabold text-beasts-ink">
                    {editingTeam ? 'Renombrar equipo' : 'Crear equipo'}
                  </Text>
                </View>
                <View className="h-12 w-12 items-center justify-center rounded-lg bg-beasts-blue">
                  <Ionicons name={selectedIconName} size={24} color="#ffffff" />
                </View>
              </View>

              <TextInput
                autoCapitalize="sentences"
                autoFocus
                className="min-h-12 rounded-lg border border-beasts-line bg-[#f8fafc] px-3.5 text-base text-beasts-ink"
                maxLength={50}
                onChangeText={(value) => {
                  setTeamName(value);
                  setModalError('');
                }}
                placeholder="Nombre del equipo"
                placeholderTextColor="#8a93a3"
                value={teamName}
              />

              {modalError ? (
                <View className="rounded-lg border border-[#f7d6bf] bg-[#fff4ed] px-3.5 py-3">
                  <Text className="text-sm font-semibold leading-5 text-beasts-warning">{modalError}</Text>
                </View>
              ) : null}

              <FlatList
                className="max-h-[260px] rounded-lg border border-beasts-line bg-[#f8fafc]"
                columnWrapperClassName="gap-2"
                contentContainerClassName="gap-2 p-2"
                data={TEAM_ICON_NAMES}
                extraData={selectedIconName}
                keyExtractor={(iconName) => iconName}
                keyboardShouldPersistTaps="handled"
                numColumns={5}
                renderItem={({ item }) => {
                  const selected = item === selectedIconName;

                  return (
                    <Pressable
                      className={`h-11 flex-1 items-center justify-center rounded-lg active:opacity-80 ${
                        selected ? 'bg-beasts-blue' : 'bg-white'
                      }`}
                      onPress={() => setSelectedIconName(item)}>
                      <Ionicons name={item} size={22} color={selected ? '#ffffff' : '#1e4f8f'} />
                    </Pressable>
                  );
                }}
              />

              <View className="flex-row justify-end gap-2.5">
                <Pressable
                  className="min-h-11 items-center justify-center rounded-lg border border-beasts-line px-4 active:opacity-80"
                  disabled={savingTeam}
                  onPress={closeModal}>
                  <Text className="text-sm font-extrabold text-[#42506a]">Cancelar</Text>
                </Pressable>
                <Pressable
                  className={`min-h-11 min-w-[102px] items-center justify-center rounded-lg bg-beasts-blue px-4 active:opacity-80 ${
                    savingTeam ? 'opacity-65' : ''
                  }`}
                  disabled={savingTeam}
                  onPress={() => void saveTeam()}>
                  {savingTeam ? (
                    <ActivityIndicator color="#ffffff" />
                  ) : (
                    <Text className="text-sm font-extrabold text-white">Guardar</Text>
                  )}
                </Pressable>
              </View>
            </View>
          </View>
        </Modal>
      </View>
    </SafeAreaView>
  );
}

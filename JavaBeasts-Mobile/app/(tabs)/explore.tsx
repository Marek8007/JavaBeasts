import { activateTeamAction, getTeamsByUserAction } from '@/actions/team.actions';
import { TeamListRow } from '@/components/ui/team-list-row';
import { TeamResponse } from '@/interfaces/team.interface';
import { useAuthStore } from '@/stores/authStore';
import { Ionicons } from '@expo/vector-icons';
import { useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  RefreshControl,
  SafeAreaView,
  StyleSheet,
  Text,
  View,
} from 'react-native';

export default function TeamsScreen() {
  const user = useAuthStore((state) => state.user);
  const [teams, setTeams] = useState<TeamResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [activatingTeamId, setActivatingTeamId] = useState<number | null>(null);
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

  const activeTeam = teams.find((team) => team.active);

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.screen}>
        <View style={styles.header}>
          <View>
            <Text style={styles.title}>Equipos</Text>
            <Text style={styles.subtitle}>
              {activeTeam ? `Activo: ${activeTeam.name}` : 'Selecciona un equipo activo antes de jugar.'}
            </Text>
          </View>

          <Pressable onPress={refreshTeams} style={styles.refreshButton}>
            <Ionicons name="refresh" size={20} color="#1e4f8f" />
          </Pressable>
        </View>

        {loading ? (
          <View style={styles.centerState}>
            <ActivityIndicator color="#1e4f8f" />
            <Text style={styles.stateText}>Cargando equipos...</Text>
          </View>
        ) : error ? (
          <View style={styles.centerState}>
            <Ionicons name="warning-outline" size={28} color="#b54708" />
            <Text style={styles.stateTitle}>No se han podido cargar</Text>
            <Text style={styles.stateText}>{error}</Text>
            <Pressable onPress={refreshTeams} style={styles.retryButton}>
              <Text style={styles.retryButtonText}>Reintentar</Text>
            </Pressable>
          </View>
        ) : (
          <FlatList
            contentContainerStyle={teams.length === 0 ? styles.emptyList : styles.listContent}
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
                team={item}
              />
            )}
            ListEmptyComponent={
              <View style={styles.centerState}>
                <Ionicons name="albums-outline" size={30} color="#68758a" />
                <Text style={styles.stateTitle}>Todavia no tienes equipos</Text>
                <Text style={styles.stateText}>Crea tu primer equipo desde la gestion de equipos.</Text>
              </View>
            }
            style={styles.list}
          />
        )}
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#f3f6fb',
  },
  screen: {
    flex: 1,
    paddingHorizontal: 18,
    paddingTop: 18,
  },
  header: {
    alignItems: 'center',
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 18,
  },
  title: {
    color: '#172033',
    fontSize: 30,
    fontWeight: '800',
  },
  subtitle: {
    color: '#5d6678',
    fontSize: 14,
    marginTop: 4,
    maxWidth: 270,
  },
  refreshButton: {
    alignItems: 'center',
    backgroundColor: '#ffffff',
    borderRadius: 8,
    height: 42,
    justifyContent: 'center',
    width: 42,
  },
  list: {
    backgroundColor: '#ffffff',
    borderRadius: 8,
  },
  listContent: {
    paddingVertical: 4,
  },
  emptyList: {
    flexGrow: 1,
    justifyContent: 'center',
  },
  centerState: {
    alignItems: 'center',
    flex: 1,
    justifyContent: 'center',
    paddingHorizontal: 24,
  },
  stateTitle: {
    color: '#172033',
    fontSize: 17,
    fontWeight: '800',
    marginTop: 10,
    textAlign: 'center',
  },
  stateText: {
    color: '#5d6678',
    fontSize: 14,
    lineHeight: 20,
    marginTop: 8,
    textAlign: 'center',
  },
  retryButton: {
    alignItems: 'center',
    backgroundColor: '#1e4f8f',
    borderRadius: 8,
    justifyContent: 'center',
    marginTop: 16,
    minHeight: 44,
    paddingHorizontal: 18,
  },
  retryButtonText: {
    color: '#ffffff',
    fontSize: 14,
    fontWeight: '800',
  },
});

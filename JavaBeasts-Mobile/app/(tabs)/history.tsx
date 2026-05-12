import { getMatchHistoryByUserAction } from '@/actions/history.actions';
import { getJaBeaImage } from '@/constants/jabea-images';
import { MatchHistoryResponse } from '@/interfaces/history.interface';
import { useAuthStore } from '@/stores/authStore';
import { useFocusEffect } from '@react-navigation/native';
import { useCallback, useState } from 'react';
import { ActivityIndicator, Image, RefreshControl, SafeAreaView, ScrollView, Text, View } from 'react-native';

export default function HistoryScreen() {
  const user = useAuthStore((state) => state.user);
  const [history, setHistory] = useState<MatchHistoryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');

  const loadHistory = useCallback(
    async (showRefresh = false) => {
      if (!user?.userId) {
        setHistory([]);
        setLoading(false);
        return;
      }

      if (showRefresh) {
        setRefreshing(true);
      } else {
        setLoading(true);
      }

      try {
        const response = await getMatchHistoryByUserAction(user.userId);
        setHistory(response);
        setError('');
      } catch (requestError: any) {
        setError(requestError?.message ? String(requestError.message) : 'No se pudo cargar el historial.');
      } finally {
        setLoading(false);
        setRefreshing(false);
      }
    },
    [user?.userId]
  );

  useFocusEffect(
    useCallback(() => {
      void loadHistory();
    }, [loadHistory])
  );

  return (
    <SafeAreaView className="flex-1 bg-beasts-soft">
      <ScrollView
        className="flex-1"
        contentContainerClassName="px-5 pb-8 pt-6"
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => void loadHistory(true)} />}>
        <View className="mb-5 gap-1">
          <Text className="text-3xl font-extrabold text-beasts-ink">Historial</Text>
          <Text className="text-sm font-semibold text-beasts-muted">
            Partidas jugadas por {user?.username ?? 'el jugador'}
          </Text>
        </View>

        {loading ? (
          <View className="min-h-[320px] items-center justify-center rounded-lg bg-white">
            <ActivityIndicator color="#1e4f8f" />
            <Text className="mt-3 text-sm font-semibold text-beasts-muted">Cargando historial...</Text>
          </View>
        ) : error ? (
          <View className="rounded-lg border border-[#f7d6bf] bg-[#fff4ed] px-4 py-4">
            <Text className="text-sm font-semibold leading-5 text-beasts-warning">{error}</Text>
          </View>
        ) : history.length ? (
          <View className="gap-4">
            {history.map((match) => (
              <View key={match.matchId} className="rounded-lg bg-white p-4 shadow-lg shadow-beasts-ink/10">
                <View className="flex-row items-center justify-between gap-3">
                  <View>
                    <Text className={`text-xs font-extrabold uppercase ${match.won ? 'text-[#15803d]' : 'text-[#bb3e03]'}`}>
                      {match.won ? 'Victoria' : 'Derrota'}
                    </Text>
                    <Text className="mt-1 text-lg font-extrabold text-beasts-ink">
                      Contra {match.opponentUsername}
                    </Text>
                  </View>
                  <View className="items-end">
                    <Text className="text-xs font-extrabold uppercase text-beasts-muted">Turnos</Text>
                    <Text className="mt-1 text-2xl font-extrabold text-beasts-ink">{match.turns}</Text>
                  </View>
                </View>

                <View className="mt-4 gap-3">
                  {match.team.map((jabea) => (
                    <View key={`${match.matchId}-${jabea.slot}`} className="flex-row items-center gap-3 rounded-lg bg-[#f8fafc] px-3 py-3">
                      <Image className="h-12 w-12" resizeMode="contain" source={getJaBeaImage(jabea.name)} />
                      <View className="flex-1">
                        <Text className="text-sm font-extrabold text-beasts-ink">
                          Slot {jabea.slot} · {jabea.name}
                        </Text>
                        <Text className="mt-1 text-xs font-semibold text-beasts-muted">
                          {jabea.move1Name ?? 'Sin move1'} · {jabea.move2Name ?? 'Sin move2'}
                        </Text>
                      </View>
                    </View>
                  ))}
                </View>
              </View>
            ))}
          </View>
        ) : (
          <View className="rounded-lg bg-white px-4 py-5">
            <Text className="text-sm font-semibold leading-5 text-beasts-muted">
              Todavia no hay partidas registradas para este usuario.
            </Text>
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

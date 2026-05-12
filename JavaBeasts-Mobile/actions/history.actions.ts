import { MatchHistoryResponse } from '@/interfaces/history.interface';

import { javabeastsApi } from './api';

export const getMatchHistoryByUserAction = async (userId: number): Promise<MatchHistoryResponse[]> => {
  const { data } = await javabeastsApi.get<MatchHistoryResponse[]>(`/matches/user/${userId}`);
  return data;
};

import { TeamResponse } from '@/interfaces/team.interface';

import { javabeastsApi } from './api';

export const getTeamsByUserAction = async (userId: number): Promise<TeamResponse[]> => {
  const { data } = await javabeastsApi.get<TeamResponse[]>(`/teams/user/${userId}`);
  return data;
};

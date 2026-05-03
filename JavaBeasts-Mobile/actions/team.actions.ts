import { TeamResponse } from '@/interfaces/team.interface';

import { javabeastsApi } from './api';

export const getTeamsByUserAction = async (userId: number): Promise<TeamResponse[]> => {
  const { data } = await javabeastsApi.get<TeamResponse[]>(`/teams/user/${userId}`);
  return data;
};

export const activateTeamAction = async (userId: number, teamId: number): Promise<TeamResponse> => {
  const { data } = await javabeastsApi.put<TeamResponse>(`/teams/${teamId}/active?userId=${userId}`);
  return data;
};

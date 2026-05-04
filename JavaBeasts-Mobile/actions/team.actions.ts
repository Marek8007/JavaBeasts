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

export const createTeamAction = async (
  userId: number,
  name: string,
  iconName?: string
): Promise<TeamResponse> => {
  const { data } = await javabeastsApi.post<TeamResponse>('/teams', {
    userId,
    name,
    ...(iconName ? { iconName } : {}),
  });
  return data;
};

export const renameTeamAction = async (
  userId: number,
  teamId: number,
  name: string,
  iconName?: string
): Promise<TeamResponse> => {
  const { data } = await javabeastsApi.put<TeamResponse>(`/teams/${teamId}?userId=${userId}`, {
    name,
    ...(iconName ? { iconName } : {}),
  });
  return data;
};

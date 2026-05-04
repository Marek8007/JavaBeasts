import { TeamCompositionResponse } from '@/interfaces/team-composition.interface';

import { javabeastsApi } from './api';

export const getTeamCompositionAction = async (
  userId: number,
  teamId: number
): Promise<TeamCompositionResponse> => {
  const { data } = await javabeastsApi.get<TeamCompositionResponse>(
    `/teams/${teamId}/composition?userId=${userId}`
  );
  return data;
};

export const deleteTeamSlotAction = async (
  userId: number,
  teamId: number,
  slot: number
): Promise<TeamCompositionResponse> => {
  const { data } = await javabeastsApi.delete<TeamCompositionResponse>(
    `/teams/${teamId}/slots/${slot}?userId=${userId}`
  );
  return data;
};

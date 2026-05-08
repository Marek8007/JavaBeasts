import {
  BattleActionSubmissionResponse,
  BattleActionType,
  BattleSnapshotResponse,
} from '@/interfaces/battle.interface';

import { javabeastsApi } from './api';

export const getBattleSnapshotAction = async (roomCode: string): Promise<BattleSnapshotResponse> => {
  const { data } = await javabeastsApi.get<BattleSnapshotResponse>(`/battle/snapshot?roomCode=${roomCode}`);
  return data;
};

export const submitBattleActionAction = async (
  roomCode: string,
  username: string,
  actionType: BattleActionType,
  moveSlot?: number,
  switchSlot?: number
): Promise<BattleActionSubmissionResponse> => {
  const { data } = await javabeastsApi.post<BattleActionSubmissionResponse>('/battle/action', {
    roomCode,
    username,
    actionType,
    moveSlot,
    switchSlot,
  });

  return data;
};

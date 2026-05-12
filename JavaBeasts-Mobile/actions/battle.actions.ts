import {
  BattleActionSubmissionResponse,
  BattleActionType,
  BattleSnapshotResponse,
} from '@/interfaces/battle.interface';
import { LOBBY_SOCKET_CODES, RoomStatusPayload } from '@/interfaces/lobby-socket.interface';

import { sendLobbyRequest } from './lobby-socket.actions';

export const getBattleSnapshotAction = async (roomCode: string): Promise<BattleSnapshotResponse> => {
  return sendLobbyRequest<BattleSnapshotResponse>(LOBBY_SOCKET_CODES.BATTLE_SNAPSHOT, {
    roomCode,
  });
};

export const submitBattleActionAction = async (
  roomCode: string,
  username: string,
  actionType: BattleActionType,
  moveSlot?: number,
  switchSlot?: number
): Promise<BattleActionSubmissionResponse> => {
  return sendLobbyRequest<BattleActionSubmissionResponse>(LOBBY_SOCKET_CODES.SUBMIT_ACTION, {
    roomCode,
    username,
    actionType,
    moveSlot,
    switchSlot,
  });
};

export const resetBattleAction = async (roomCode: string): Promise<RoomStatusPayload> => {
  return sendLobbyRequest<RoomStatusPayload>(LOBBY_SOCKET_CODES.RESET_BATTLE, {
    roomCode,
  });
};

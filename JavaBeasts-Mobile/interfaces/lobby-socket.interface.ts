export const LOBBY_SOCKET_CODES = {
  ROOM_STATUS: 'room_status',
  JOIN_ROOM: 'join_room',
  LEAVE_ROOM: 'leave_room',
  SET_READY: 'set_ready',
  BATTLE_SNAPSHOT: 'battle_snapshot',
  SUBMIT_ACTION: 'submit_action',
  RESET_BATTLE: 'reset_battle',
} as const;

export type LobbySocketCode = (typeof LOBBY_SOCKET_CODES)[keyof typeof LOBBY_SOCKET_CODES];

export interface LobbySocketRequest<TData extends Record<string, unknown> = Record<string, unknown>> {
  code: LobbySocketCode;
  data: TData;
}

export interface LobbySocketResponse<TData = unknown> {
  status: 'success' | 'error';
  data: TData;
}

export interface LobbySocketErrorPayload {
  message: string;
}

export interface LobbyPlayerSlotPayload {
  username: string;
  ready: boolean;
}

export interface RoomStatusPayload {
  roomCode: string;
  full: boolean;
  canStart: boolean;
  playerOne?: LobbyPlayerSlotPayload | null;
  playerTwo?: LobbyPlayerSlotPayload | null;
}

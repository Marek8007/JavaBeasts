export interface BattleCreatureSnapshotResponse {
  slot: number;
  jaBeasId: number;
  name: string;
  currentHealth: number;
  maxHealth: number;
  damage: number;
  defence: number;
  speed: number;
}

export interface BattlePlayerSnapshotResponse {
  userId: number;
  username: string;
  teamId: number;
  teamName: string;
  activeJaBea: BattleCreatureSnapshotResponse;
}

export interface BattleSnapshotResponse {
  roomCode: string;
  turnNumber: number;
  playerOne: BattlePlayerSnapshotResponse;
  playerTwo: BattlePlayerSnapshotResponse;
}

export type BattleActionType = 'ATTACK' | 'SWITCH';

export interface BattleActionSubmissionResponse {
  roomCode: string;
  turnNumber: number;
  playerOneActionSubmitted: boolean;
  playerTwoActionSubmitted: boolean;
  turnReadyToResolve: boolean;
  turnResolved: boolean;
  message: string;
  snapshot: BattleSnapshotResponse;
}

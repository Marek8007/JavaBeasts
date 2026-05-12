export interface BattleMoveSnapshotResponse {
  slot: number;
  moveId: number;
  name: string;
  typeId: number | null;
  typeName: string | null;
  damage: number;
  accuracy: number;
  specialEffect: string | null;
}

export interface BattleCreatureSnapshotResponse {
  slot: number;
  jaBeasId: number;
  name: string;
  currentHealth: number;
  maxHealth: number;
  damage: number;
  defence: number;
  speed: number;
  moves: BattleMoveSnapshotResponse[];
}

export interface BattlePlayerSnapshotResponse {
  userId: number;
  username: string;
  teamId: number;
  teamName: string;
  activeJaBea: BattleCreatureSnapshotResponse;
  teamCreatures?: BattleCreatureSnapshotResponse[];
}

export interface BattleSnapshotResponse {
  roomCode: string;
  turnNumber: number;
  playerOne: BattlePlayerSnapshotResponse;
  playerTwo: BattlePlayerSnapshotResponse;
  message?: string;
  finished?: boolean;
  winnerUsername?: string | null;
}

export type BattleActionType = 'ATTACK' | 'SWITCH' | 'SURRENDER';

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

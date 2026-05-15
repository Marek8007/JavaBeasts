export interface MatchHistoryJaBeaResponse {
  slot: number;
  jaBeasId: number | null;
  name: string;
  move1Name: string | null;
  move2Name: string | null;
}

export interface MatchHistoryResponse {
  matchId: number;
  won: boolean;
  opponentUsername: string;
  turns: number;
  team: MatchHistoryJaBeaResponse[];
}

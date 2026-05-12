export interface MoveSummaryResponse {
  moveId: number;
  name: string;
  typeId: number;
  typeName: string;
  damage: number;
  accuracy: number;
  specialEffect?: string | null;
}

export interface TeamJaBeaResponse {
  jaBeasId: number;
  name: string;
  typeId: number;
  typeName: string;
  uniqueMove?: MoveSummaryResponse | null;
  move1: MoveSummaryResponse;
  move2: MoveSummaryResponse;
}

export interface TeamSlotResponse {
  slot: number;
  member?: TeamJaBeaResponse | null;
}

export interface TeamCompositionResponse {
  teamId: number;
  userId: number;
  name: string;
  active: boolean;
  slots: TeamSlotResponse[];
}

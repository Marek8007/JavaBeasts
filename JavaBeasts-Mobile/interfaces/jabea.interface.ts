export interface JaBeaMoveResponse {
  moveId: number;
  name: string;
  description: string;
  typeId: number;
  typeName: string;
  damage: number;
  accuracy: number;
  specialEffect?: string | null;
}

export interface JaBeaCatalogResponse {
  jaBeasId: number;
  name: string;
  description: string;
  health: number;
  damage: number;
  defence: number;
  speed: number;
  typeId: number;
  typeName: string;
  uniqueMove?: JaBeaMoveResponse | null;
}

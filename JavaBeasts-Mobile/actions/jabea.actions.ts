import { JaBeaAvailableMovesResponse, JaBeaCatalogResponse } from '@/interfaces/jabea.interface';

import { javabeastsApi } from './api';

export const getJaBeasCatalogAction = async (): Promise<JaBeaCatalogResponse[]> => {
  const { data } = await javabeastsApi.get<JaBeaCatalogResponse[]>('/jabeas');
  return data;
};

export const getJaBeaAvailableMovesAction = async (
  jaBeasId: number
): Promise<JaBeaAvailableMovesResponse> => {
  const { data } = await javabeastsApi.get<JaBeaAvailableMovesResponse>(
    `/jabeas/${jaBeasId}/available-moves`
  );
  return data;
};

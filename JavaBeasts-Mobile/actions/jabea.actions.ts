import { JaBeaCatalogResponse } from '@/interfaces/jabea.interface';

import { javabeastsApi } from './api';

export const getJaBeasCatalogAction = async (): Promise<JaBeaCatalogResponse[]> => {
  const { data } = await javabeastsApi.get<JaBeaCatalogResponse[]>('/jabeas');
  return data;
};

import axios from 'axios';

export const API_BASE_URL = process.env.EXPO_PUBLIC_API_BASE_URL ?? 'http://192.168.1.144:9234';

export const javabeastsApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 5000,
});

javabeastsApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.message === 'Network Error' || error?.code === 'ECONNABORTED') {
      error.message = `${error.message} (${API_BASE_URL})`;
    }

    return Promise.reject(error);
  }
);

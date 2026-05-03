import axios from 'axios';

const BASE_URL = process.env.EXPO_PUBLIC_API_BASE_URL ?? 'http://192.168.1.140:9234';

export const javabeastsApi = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

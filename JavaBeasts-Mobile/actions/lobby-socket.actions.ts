import TcpSocket from 'react-native-tcp-socket';

import {
  LOBBY_SOCKET_CODES,
  LobbySocketCode,
  LobbySocketErrorPayload,
  LobbySocketResponse,
  RoomStatusPayload,
} from '@/interfaces/lobby-socket.interface';

import { API_BASE_URL } from './api';

const DEFAULT_SOCKET_PORT = 7878;
const REQUEST_TIMEOUT_MS = 5000;

const getLobbySocketHost = () => {
  const configuredHost = process.env.EXPO_PUBLIC_SOCKET_HOST?.trim();

  if (configuredHost) {
    return configuredHost;
  }

  try {
    return new URL(API_BASE_URL).hostname;
  } catch {
    return '127.0.0.1';
  }
};

const getLobbySocketPort = () => {
  const configuredPort = Number(process.env.EXPO_PUBLIC_SOCKET_PORT);
  return Number.isFinite(configuredPort) && configuredPort > 0 ? configuredPort : DEFAULT_SOCKET_PORT;
};

export class LobbySocketError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'LobbySocketError';
  }
}

export const sendLobbyRequest = <TResponse>(
  code: LobbySocketCode,
  data: Record<string, unknown> = {}
): Promise<TResponse> => {
  return new Promise((resolve, reject) => {
    const socket = TcpSocket.createConnection(
      {
        host: getLobbySocketHost(),
        port: getLobbySocketPort(),
      },
      () => {
        socket.write(JSON.stringify({ code, data }) + '\n');
      }
    );
    let responseBuffer = '';
    let settled = false;
    const timeout = setTimeout(() => {
      settleWithError(new LobbySocketError('Tiempo de espera agotado al conectar con la sala'));
    }, REQUEST_TIMEOUT_MS);

    const cleanup = () => {
      clearTimeout(timeout);
      socket.destroy();
    };

    const settleWithSuccess = (response: TResponse) => {
      if (settled) {
        return;
      }

      settled = true;
      cleanup();
      resolve(response);
    };

    const settleWithError = (error: Error) => {
      if (settled) {
        return;
      }

      settled = true;
      cleanup();
      reject(error);
    };

    const parseResponse = (rawResponse: string) => {
      try {
        const response = JSON.parse(rawResponse) as LobbySocketResponse<TResponse | LobbySocketErrorPayload>;

        if (response.status === 'error') {
          const errorPayload = response.data as LobbySocketErrorPayload;
          settleWithError(new LobbySocketError(errorPayload.message || 'Error del servidor TCP'));
          return;
        }

        settleWithSuccess(response.data as TResponse);
      } catch {
        settleWithError(new LobbySocketError('Respuesta TCP no valida'));
      }
    };

    socket.setTimeout(REQUEST_TIMEOUT_MS, () => {
      settleWithError(new LobbySocketError('Tiempo de espera agotado al conectar con la sala'));
    });

    socket.on('data', (chunk) => {
      responseBuffer += String(chunk);

      const lineBreakIndex = responseBuffer.indexOf('\n');
      if (lineBreakIndex === -1) {
        return;
      }

      parseResponse(responseBuffer.slice(0, lineBreakIndex).trim());
    });

    socket.on('error', (error) => {
      settleWithError(error);
    });

    socket.on('close', () => {
      if (!settled && responseBuffer.trim()) {
        parseResponse(responseBuffer.trim());
        return;
      }

      if (!settled) {
        settleWithError(new LobbySocketError('La conexion TCP se cerro sin respuesta'));
      }
    });
  });
};

export const getRoomStatusAction = () => {
  return sendLobbyRequest<RoomStatusPayload>(LOBBY_SOCKET_CODES.ROOM_STATUS);
};

export const joinRoomAction = (roomCode: string, username: string) => {
  return sendLobbyRequest<RoomStatusPayload>(LOBBY_SOCKET_CODES.JOIN_ROOM, {
    roomCode,
    username,
  });
};

export const leaveRoomAction = (roomCode: string, username: string) => {
  return sendLobbyRequest<RoomStatusPayload>(LOBBY_SOCKET_CODES.LEAVE_ROOM, {
    roomCode,
    username,
  });
};

export const setReadyAction = (roomCode: string, username: string, ready: boolean) => {
  return sendLobbyRequest<RoomStatusPayload>(LOBBY_SOCKET_CODES.SET_READY, {
    ready,
    roomCode,
    username,
  });
};

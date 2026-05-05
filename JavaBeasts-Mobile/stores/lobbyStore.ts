import { RoomStatusPayload } from '@/interfaces/lobby-socket.interface';
import { create } from 'zustand';

interface LobbyStore {
  roomStatus: RoomStatusPayload | null;
  clearRoom: () => void;
  setRoomStatus: (roomStatus: RoomStatusPayload | null) => void;
}

export const useLobbyStore = create<LobbyStore>((set) => ({
  roomStatus: null,

  clearRoom: () => {
    set({ roomStatus: null });
  },

  setRoomStatus: (roomStatus) => {
    set({ roomStatus });
  },
}));

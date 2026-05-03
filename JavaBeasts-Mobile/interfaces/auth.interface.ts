export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  userId: number;
  username: string;
  logged: boolean;
  matchesWon: number;
  matchesLost: number;
  message: string;
}

import { Tabs } from 'expo-router';
import React from 'react';
import { Ionicons } from '@expo/vector-icons';

import { HapticTab } from '@/components/haptic-tab';
import { IconSymbol } from '@/components/ui/icon-symbol';
import { Colors } from '@/constants/theme';
import { useColorScheme } from '@/hooks/use-color-scheme';
import { useAuthStore } from '@/stores/authStore';
import { useLobbyStore } from '@/stores/lobbyStore';

export default function TabLayout() {
  const colorScheme = useColorScheme();
  const user = useAuthStore((state) => state.user);
  const roomStatus = useLobbyStore((state) => state.roomStatus);
  const currentPlayer = [roomStatus?.playerOne, roomStatus?.playerTwo].find(
    (slot) => slot?.username === user?.username
  );
  const ready = Boolean(currentPlayer?.ready);

  const preventLeavingRoomWhenReady = {
    tabPress: (event: { preventDefault: () => void }) => {
      if (ready) {
        event.preventDefault();
      }
    },
  };

  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: Colors[colorScheme ?? 'light'].tint,
        headerShown: false,
        tabBarButton: HapticTab,
      }}>
      <Tabs.Screen
        name="index"
        listeners={preventLeavingRoomWhenReady}
        options={{
          title: 'Home',
          tabBarIcon: ({ color }) => <IconSymbol size={28} name="house.fill" color={color} />,
        }}
      />
      <Tabs.Screen
        name="explore"
        listeners={preventLeavingRoomWhenReady}
        options={{
          title: 'Equipos',
          tabBarIcon: ({ color, size }) => <Ionicons size={size} name="albums-outline" color={color} />,
        }}
      />
      <Tabs.Screen
        name="history"
        listeners={preventLeavingRoomWhenReady}
        options={{
          title: 'Historial',
          tabBarIcon: ({ color, size }) => <Ionicons size={size} name="time-outline" color={color} />,
        }}
      />
      <Tabs.Screen
        name="room"
        options={{
          title: 'Sala',
          tabBarIcon: ({ color, size }) => <Ionicons size={size} name="keypad-outline" color={color} />,
        }}
      />
    </Tabs>
  );
}

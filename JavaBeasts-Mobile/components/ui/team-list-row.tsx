import { TeamResponse } from '@/interfaces/team.interface';
import { Ionicons } from '@expo/vector-icons';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';

type TeamListRowProps = {
  activating?: boolean;
  disabled?: boolean;
  onPress?: () => void;
  team: TeamResponse;
};

export function TeamListRow({ activating = false, disabled = false, onPress, team }: TeamListRowProps) {
  return (
    <Pressable
      disabled={disabled}
      onPress={onPress}
      style={({ pressed }) => [styles.row, pressed && styles.pressed, disabled && styles.disabled]}>
      <View style={[styles.avatar, team.active && styles.avatarActive]}>
        <Ionicons
          name={team.active ? 'star' : 'paw-outline'}
          size={22}
          color={team.active ? '#ffffff' : '#1e4f8f'}
        />
      </View>

      <View style={styles.content}>
        <Text numberOfLines={1} style={styles.title}>
          {team.name}
        </Text>
      </View>

      {activating ? (
        <ActivityIndicator color="#1e4f8f" />
      ) : (
        <Ionicons name="chevron-forward" size={18} color="#9aa5b5" />
      )}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  row: {
    alignItems: 'center',
    backgroundColor: '#ffffff',
    borderBottomColor: '#edf1f6',
    borderBottomWidth: 1,
    flexDirection: 'row',
    minHeight: 68,
    paddingHorizontal: 16,
    paddingVertical: 9,
  },
  pressed: {
    opacity: 0.75,
  },
  disabled: {
    opacity: 0.65,
  },
  avatar: {
    alignItems: 'center',
    backgroundColor: '#e8edf5',
    borderRadius: 8,
    height: 50,
    justifyContent: 'center',
    marginRight: 12,
    width: 50,
  },
  avatarActive: {
    backgroundColor: '#1e4f8f',
  },
  content: {
    flex: 1,
    justifyContent: 'center',
  },
  title: {
    color: '#172033',
    fontSize: 16,
    fontWeight: '800',
  },
});

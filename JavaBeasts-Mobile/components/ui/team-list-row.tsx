import { TeamResponse } from '@/interfaces/team.interface';
import { Ionicons } from '@expo/vector-icons';
import { ActivityIndicator, Pressable, Text, View } from 'react-native';

type TeamListRowProps = {
  activating?: boolean;
  disabled?: boolean;
  onActivate?: () => void;
  onRename?: () => void;
  team: TeamResponse;
};

export function TeamListRow({
  activating = false,
  disabled = false,
  onActivate,
  onRename,
  team,
}: TeamListRowProps) {
  return (
    <View
      className={`min-h-[68px] flex-row items-center border-b border-[#edf1f6] bg-white px-4 py-[9px] ${
        disabled ? 'opacity-65' : ''
      }`}>
      <Pressable
        className={`mr-3 h-[50px] w-[50px] items-center justify-center rounded-lg active:opacity-75 ${
          team.active ? 'bg-beasts-blue' : 'bg-[#e8edf5]'
        }`}
        disabled={disabled || team.active}
        onPress={onActivate}>
        {activating ? (
          <ActivityIndicator color="#1e4f8f" />
        ) : (
          <Ionicons
            name={team.active ? 'star' : 'paw-outline'}
            size={22}
            color={team.active ? '#ffffff' : '#1e4f8f'}
          />
        )}
      </Pressable>

      <View className="flex-1 justify-center">
        <Text className="text-base font-extrabold text-beasts-ink" numberOfLines={1}>
          {team.name}
        </Text>
      </View>

      <Pressable
        className="ml-2 h-10 w-10 items-center justify-center active:opacity-75"
        disabled={disabled}
        onPress={onRename}>
        <Ionicons name="create-outline" size={20} color="#68758a" />
      </Pressable>
    </View>
  );
}

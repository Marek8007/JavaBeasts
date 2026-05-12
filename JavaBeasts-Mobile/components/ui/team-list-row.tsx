import { TeamResponse } from '@/interfaces/team.interface';
import { Ionicons } from '@expo/vector-icons';
import { ComponentProps } from 'react';
import { ActivityIndicator, Pressable, Text, View } from 'react-native';

type IoniconName = ComponentProps<typeof Ionicons>['name'];

const DEFAULT_TEAM_ICON: IoniconName = 'paw-outline';

type TeamListRowProps = {
  activating?: boolean;
  disabled?: boolean;
  onActivate?: () => void;
  onOpen?: () => void;
  onRename?: () => void;
  team: TeamResponse;
};

export function TeamListRow({
  activating = false,
  disabled = false,
  onActivate,
  onOpen,
  onRename,
  team,
}: TeamListRowProps) {
  const iconName = (team.iconName || DEFAULT_TEAM_ICON) as IoniconName;

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
          <Ionicons name={iconName} size={22} color={team.active ? '#ffffff' : '#1e4f8f'} />
        )}
      </Pressable>

      <Pressable className="flex-1 justify-center self-stretch" disabled={disabled} onPress={onOpen}>
        <Text className="text-base font-extrabold text-beasts-ink" numberOfLines={1}>
          {team.name}
        </Text>
      </Pressable>

      <Pressable
        className="ml-2 h-10 w-10 items-center justify-center active:opacity-75"
        disabled={disabled}
        onPress={onRename}>
        <Ionicons name="create-outline" size={20} color="#68758a" />
      </Pressable>
    </View>
  );
}

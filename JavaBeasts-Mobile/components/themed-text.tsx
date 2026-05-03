import { Text, type TextProps } from 'react-native';

import { useThemeColor } from '@/hooks/use-theme-color';

export type ThemedTextProps = TextProps & {
  lightColor?: string;
  darkColor?: string;
  type?: 'default' | 'title' | 'defaultSemiBold' | 'subtitle' | 'link';
};

export function ThemedText({
  style,
  lightColor,
  darkColor,
  type = 'default',
  ...rest
}: ThemedTextProps) {
  const color = useThemeColor({ light: lightColor, dark: darkColor }, 'text');
  const typeClassName = {
    default: 'text-base leading-6',
    defaultSemiBold: 'text-base font-semibold leading-6',
    title: 'text-[32px] font-bold leading-8',
    subtitle: 'text-xl font-bold',
    link: 'text-base leading-[30px]',
  }[type];

  return (
    <Text
      className={typeClassName}
      style={[
        { color: type === 'link' ? '#0a7ea4' : color },
        style,
      ]}
      {...rest}
    />
  );
}

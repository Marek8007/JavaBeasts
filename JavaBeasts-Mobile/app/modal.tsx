import { Link } from 'expo-router';
import { Text, View } from 'react-native';

export default function ModalScreen() {
  return (
    <View className="flex-1 items-center justify-center bg-beasts-soft p-5">
      <Text className="text-[32px] font-bold leading-8 text-beasts-ink">This is a modal</Text>
      <Link href="/" dismissTo className="mt-[15px] py-[15px]">
        <Text className="text-base leading-[30px] text-beasts-blue">Go to home screen</Text>
      </Link>
    </View>
  );
}

import { registerAction } from '@/actions/auth.actions';
import { Ionicons } from '@expo/vector-icons';
import { router } from 'expo-router';
import { useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  SafeAreaView,
  Text,
  TextInput,
  View,
} from 'react-native';

export default function RegisterScreen() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleRegister() {
    if (username.trim().length < 3 || password.length < 6) {
      Alert.alert('Error', 'Usuario minimo 3 caracteres y contrasena minimo 6.');
      return;
    }

    setLoading(true);

    try {
      await registerAction({
        username: username.trim(),
        password,
      });

      Alert.alert('Registro completado', 'Ya puedes iniciar sesion', [
        { text: 'Ir al login', onPress: () => router.replace('/(auth)/login') },
      ]);
    } catch (error: any) {
      const message =
        error?.response?.data?.message ??
        error?.response?.data?.detail ??
        error?.message ??
        'No se pudo registrar el usuario';
      Alert.alert('Error', String(message));
    } finally {
      setLoading(false);
    }
  }

  return (
    <SafeAreaView className="flex-1 bg-beasts-soft">
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        className="flex-1 justify-center px-6">
        <View className="mb-[34px] items-center gap-2">
          <Ionicons name="person-add-outline" size={44} color="#1d4ed8" />
          <Text className="text-3xl font-extrabold text-beasts-ink">Crear cuenta</Text>
          <Text className="text-[15px] text-beasts-muted">Registrate para jugar desde el movil.</Text>
        </View>

        <View className="gap-3.5">
          <View className="min-h-[52px] flex-row items-center rounded-lg border border-beasts-line bg-beasts-panel px-3.5">
            <Ionicons name="person-outline" size={20} color="#94a3b8" />
            <TextInput
              autoCapitalize="none"
              autoCorrect={false}
              className="ml-2.5 flex-1 text-base text-beasts-ink"
              onChangeText={setUsername}
              placeholder="Usuario"
              placeholderTextColor="#94a3b8"
              value={username}
            />
          </View>

          <View className="min-h-[52px] flex-row items-center rounded-lg border border-beasts-line bg-beasts-panel px-3.5">
            <Ionicons name="lock-closed-outline" size={20} color="#94a3b8" />
            <TextInput
              autoCapitalize="none"
              className="ml-2.5 flex-1 text-base text-beasts-ink"
              onChangeText={setPassword}
              placeholder="Contrasena"
              placeholderTextColor="#94a3b8"
              secureTextEntry
              value={password}
            />
          </View>

          <Pressable
            className={`min-h-[52px] items-center justify-center rounded-lg bg-beasts-blue active:opacity-80 ${
              loading ? 'opacity-65' : ''
            }`}
            disabled={loading}
            onPress={handleRegister}>
            {loading ? (
              <ActivityIndicator color="#ffffff" />
            ) : (
              <Text className="text-base font-extrabold text-white">Crear cuenta</Text>
            )}
          </Pressable>
        </View>

        <View className="mt-7 flex-row justify-center">
          <Text className="text-sm text-beasts-muted">Ya tienes cuenta?</Text>
          <Pressable onPress={() => router.replace('/(auth)/login')}>
            <Text className="text-sm font-extrabold text-beasts-blue"> Inicia sesion</Text>
          </Pressable>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

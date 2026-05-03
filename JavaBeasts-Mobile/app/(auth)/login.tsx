import { loginAction } from '@/actions/auth.actions';
import { useAuthStore } from '@/stores/authStore';
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
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';

export default function LoginScreen() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const login = useAuthStore((state) => state.login);

  async function handleLogin() {
    if (username.trim().length < 3 || password.length < 6) {
      Alert.alert('Error', 'Usuario minimo 3 caracteres y contrasena minimo 6.');
      return;
    }

    setLoading(true);

    try {
      const user = await loginAction({
        username: username.trim(),
        password,
      });
      await login(user);
      router.replace('/(tabs)');
    } catch (error: any) {
      const message =
        error?.response?.data?.message ??
        error?.response?.data?.detail ??
        error?.message ??
        'No se pudo iniciar sesion';
      Alert.alert('Error', String(message));
    } finally {
      setLoading(false);
    }
  }

  return (
    <SafeAreaView style={styles.safeArea}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.screen}>
        <View style={styles.header}>
          <Ionicons name="game-controller-outline" size={46} color="#1e4f8f" />
          <Text style={styles.brand}>JavaBeasts</Text>
          <Text style={styles.subtitle}>Inicia sesion para preparar tu equipo.</Text>
        </View>

        <View style={styles.form}>
          <View style={styles.inputRow}>
            <Ionicons name="person-outline" size={20} color="#68758a" />
            <TextInput
              autoCapitalize="none"
              autoCorrect={false}
              onChangeText={setUsername}
              placeholder="Usuario"
              placeholderTextColor="#8a93a3"
              style={styles.input}
              value={username}
            />
          </View>

          <View style={styles.inputRow}>
            <Ionicons name="lock-closed-outline" size={20} color="#68758a" />
            <TextInput
              autoCapitalize="none"
              onChangeText={setPassword}
              placeholder="Contrasena"
              placeholderTextColor="#8a93a3"
              secureTextEntry={!showPassword}
              style={styles.input}
              value={password}
            />
            <Pressable onPress={() => setShowPassword((value) => !value)} style={styles.iconButton}>
              <Ionicons
                name={showPassword ? 'eye-off-outline' : 'eye-outline'}
                size={20}
                color="#68758a"
              />
            </Pressable>
          </View>

          <Pressable
            disabled={loading}
            onPress={handleLogin}
            style={({ pressed }) => [
              styles.primaryButton,
              pressed && styles.pressed,
              loading && styles.disabled,
            ]}>
            {loading ? (
              <ActivityIndicator color="#ffffff" />
            ) : (
              <Text style={styles.primaryButtonText}>Entrar</Text>
            )}
          </Pressable>
        </View>

        <View style={styles.footer}>
          <Text style={styles.footerText}>No tienes cuenta?</Text>
          <Pressable onPress={() => router.push('/(auth)/register')}>
            <Text style={styles.footerLink}> Registrate</Text>
          </Pressable>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#f3f6fb',
  },
  screen: {
    flex: 1,
    justifyContent: 'center',
    paddingHorizontal: 24,
  },
  header: {
    alignItems: 'center',
    gap: 8,
    marginBottom: 34,
  },
  brand: {
    color: '#172033',
    fontSize: 34,
    fontWeight: '800',
  },
  subtitle: {
    color: '#5d6678',
    fontSize: 15,
  },
  form: {
    gap: 14,
  },
  inputRow: {
    alignItems: 'center',
    backgroundColor: '#ffffff',
    borderColor: '#d9e1ec',
    borderRadius: 8,
    borderWidth: 1,
    flexDirection: 'row',
    minHeight: 52,
    paddingHorizontal: 14,
  },
  input: {
    color: '#172033',
    flex: 1,
    fontSize: 16,
    marginLeft: 10,
  },
  iconButton: {
    padding: 6,
  },
  primaryButton: {
    alignItems: 'center',
    backgroundColor: '#1e4f8f',
    borderRadius: 8,
    justifyContent: 'center',
    minHeight: 52,
  },
  primaryButtonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: '800',
  },
  pressed: {
    opacity: 0.82,
  },
  disabled: {
    opacity: 0.65,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginTop: 28,
  },
  footerText: {
    color: '#5d6678',
    fontSize: 14,
  },
  footerLink: {
    color: '#1e4f8f',
    fontSize: 14,
    fontWeight: '800',
  },
});

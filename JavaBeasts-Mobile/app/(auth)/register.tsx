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
  StyleSheet,
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
    <SafeAreaView style={styles.safeArea}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.screen}>
        <View style={styles.header}>
          <Ionicons name="person-add-outline" size={44} color="#1e4f8f" />
          <Text style={styles.title}>Crear cuenta</Text>
          <Text style={styles.subtitle}>Registrate para jugar desde el movil.</Text>
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
              secureTextEntry
              style={styles.input}
              value={password}
            />
          </View>

          <Pressable
            disabled={loading}
            onPress={handleRegister}
            style={({ pressed }) => [
              styles.primaryButton,
              pressed && styles.pressed,
              loading && styles.disabled,
            ]}>
            {loading ? (
              <ActivityIndicator color="#ffffff" />
            ) : (
              <Text style={styles.primaryButtonText}>Crear cuenta</Text>
            )}
          </Pressable>
        </View>

        <View style={styles.footer}>
          <Text style={styles.footerText}>Ya tienes cuenta?</Text>
          <Pressable onPress={() => router.replace('/(auth)/login')}>
            <Text style={styles.footerLink}> Inicia sesion</Text>
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
  title: {
    color: '#172033',
    fontSize: 30,
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

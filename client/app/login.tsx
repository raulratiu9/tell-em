import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import * as WebBrowser from 'expo-web-browser';
import * as SecureStore from 'expo-secure-store';
import * as Linking from 'expo-linking';

const Login = () => {
  const handleLogin = async (provider) => {
    const authUrl = `https://445a-95-77-253-33.ngrok-free.app/oauth2/authorization/${provider}`;

    await WebBrowser.openBrowserAsync(authUrl);

    const handleDeepLink = (event) => {
      let url = event.url;
      let { queryParams } = Linking.parse(url);

      if (queryParams?.token) {
        queryParams && SecureStore.setItemAsync('jwt_token', queryParams?.token);
        console.log('JWT stored:', queryParams.token);

        WebBrowser.dismissBrowser();
      }
    };

    Linking.addEventListener('url', handleDeepLink);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome to Tell-em</Text>

      <TouchableOpacity style={styles.googleButton} onPress={() => handleLogin('google')}>
        <Text style={styles.buttonText}>Sign in with Google</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.facebookButton}
        onPress={() => handleLogin('facebook')}
      >
        <Text style={styles.buttonText}>Sign in with Facebook</Text>
      </TouchableOpacity>
    </View>
  );
};

export default Login;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 30,
  },
  googleButton: {
    backgroundColor: '#DB4437',
    padding: 12,
    borderRadius: 8,
    width: '80%',
    alignItems: 'center',
    marginBottom: 15,
  },
  facebookButton: {
    backgroundColor: '#1877F2',
    padding: 12,
    borderRadius: 8,
    width: '80%',
    alignItems: 'center',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

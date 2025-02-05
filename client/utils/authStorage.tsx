// AuthService.js (example of JWT token management)
import * as SecureStore from 'expo-secure-store';

export const storeToken = async (token) => {
  try {
    await SecureStore.setItemAsync('auth_token', token);
  } catch (error) {
    console.error('Error storing the token', error);
  }
};

export const getToken = async () => {
  try {
    return await SecureStore.getItemAsync('auth_token');
  } catch (error) {
    console.error('Error retrieving the token', error);
  }
};

export const clearToken = async () => {
  try {
    await SecureStore.deleteItemAsync('auth_token');
  } catch (error) {
    console.error('Error clearing the token', error);
  }
};

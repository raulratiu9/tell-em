import type { Persister } from '@tanstack/react-query-persist-client';
import AsyncStorage from '@react-native-async-storage/async-storage';

export function createAsyncStoragePersistor(): Persister {
  return {
    persistClient: async (client) => {
      try {
        await AsyncStorage.setItem('REACT_QUERY_OFFLINE_CACHE', JSON.stringify(client));
      } catch (err) {
        console.error('Error persisting query client:', err);
      }
    },
    restoreClient: async () => {
      try {
        const cache = await AsyncStorage.getItem('REACT_QUERY_OFFLINE_CACHE');
        return cache ? JSON.parse(cache) : undefined;
      } catch (err) {
        console.error('Error restoring query client:', err);
        return undefined;
      }
    },
    removeClient: async () => {
      try {
        await AsyncStorage.removeItem('REACT_QUERY_OFFLINE_CACHE');
      } catch (err) {
        console.error('Error removing query client:', err);
      }
    },
  };
}

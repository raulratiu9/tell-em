import { Story } from '@/types';
import AsyncStorage from '@react-native-async-storage/async-storage';

export const saveCachedStories = async (stories: Story[]) => {
  try {
    await AsyncStorage.setItem('cachedFeedStories', JSON.stringify(stories.slice(0, 10)));
  } catch (err) {
    console.error('Failed to save cached feed:', err);
  }
};

export const loadCachedStories = async (): Promise<Story[]> => {
  try {
    const raw = await AsyncStorage.getItem('cachedFeedStories');
    return raw ? JSON.parse(raw) : [];
  } catch (err) {
    console.error('Failed to load cached feed:', err);
    return [];
  }
};

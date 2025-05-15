import { Story } from '@/types';
import axios from 'axios';

export const getSearchedStories = async (
  page: number,
  size: number = 10,
  query: string,
): Promise<Story[]> => {
  try {
    const response = await axios.get(
      `${process.env.EXPO_PUBLIC_BASE_API_URL}api/stories/search?page=${page}&size=${size}&query=${encodeURIComponent(query)}`,
    );
    return response.data;
  } catch (error) {
    console.error('Error searching stories:', error);
    return [];
  }
};

import { Story } from '@/types';
import axios from 'axios';

export const getStories = async (page: number, size: number = 10): Promise<Story[]> => {
  try {
    const response = await axios.get(
      `${process.env.EXPO_PUBLIC_BASE_API_URL}api/stories?page=${page}&size=${size}`,
      {
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
      },
    );
    return response.data;
  } catch (error) {
    console.error('Error fetching stories:', error);
    return [];
  }
};

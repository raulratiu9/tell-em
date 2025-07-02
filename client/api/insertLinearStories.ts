import { Story } from '@/types';
import axios, { CancelToken } from 'axios';

export const insertLinearStories = async (
  stories: number,
  nodes: number,
  cancelToken?: CancelToken,
): Promise<Story[]> => {
  try {
    const response = await axios.get(
      `${process.env.EXPO_PUBLIC_BASE_API_URL}api/benchmark/linear-stories?numberOfStories=${stories}&numberOfNodes=${nodes}`,
      {
        cancelToken,
      },
    );
    return response.data;
  } catch (error) {
    if (axios.isCancel(error)) {
      console.warn('Request canceled:', error.message);
    } else {
      console.error('Error inserting stories:', error);
    }
    return [];
  }
};

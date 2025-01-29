import axios from 'axios';

export default function postStory(formData: FormData) {
  const response = axios.post(
    `${process.env.EXPO_PUBLIC_BASE_API_URL}api/stories`,
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${process.env.EXPO_PUBLIC_AUTH_TOKEN}`,
      },
    },
  );

  return response;
}

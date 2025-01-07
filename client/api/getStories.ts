import { Story } from "@/types";
import axios from "axios";

export const getStories = async (setter: (param: Story[]) => void) => {
  try {
    const response = await axios.get(
      `${process.env.EXPO_PUBLIC_BASE_API_URL}api/stories`,
      {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Bearer ${process.env.EXPO_PUBLIC_AUTH_TOKEN}`,
        },
      }
    );

    setter(response.data);
  } catch (error) {
    console.error("Error fetching stories:", error);
  }
};

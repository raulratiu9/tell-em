import { useEffect, useState } from "react";
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  Button,
} from "react-native";
import axios from "axios";
import { Story } from "@/types";
import StoryCard from "@/components/StoryCard";
import Animated, { FadeInUp } from "react-native-reanimated";
import { router, useNavigation } from "expo-router";
import ToastNotification from "@/components/ToastNotification";
import Toast from "react-native-toast-message";

export default function StoriesFeed() {
  const navigation = useNavigation();

  const [stories, setStories] = useState([]);

  useEffect(() => {
    const fetchStories = async () => {
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
        setStories(response.data);
      } catch (error) {
        console.error("Error fetching stories:", error);
      }
    };

    fetchStories();
  }, []);

  const showToast = () => {
    Toast.show({
      type: "success",
      text1: "Hello",
      text2: "This is some something 👋",
    });
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Tell'em you've got new stories</Text>
      <Button title="Show Toast" onPress={showToast} />
      <FlatList
        data={stories.slice(-10)}
        keyExtractor={(item: Story) => item.id.toString()}
        renderItem={({ item }) => (
          <TouchableOpacity
            key={item.title}
            onPress={() => router.push(`/story/${item.id}`)}
          >
            <StoryCard key={item.title} story={item} />
          </TouchableOpacity>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    justifyContent: "center",
    fontFamily: "MontserratBlack",
    overflowX: "hidden",
  },
  header: {
    fontSize: 24,
    fontWeight: "bold",
    marginVertical: 20,
    textAlign: "left",
  },
});

import DonationButton from "@/components/DonationButton";
import ShareButton from "@/components/ShareButton";
import { Story } from "@/types";
import axios from "axios";
import { LinearGradient } from "expo-linear-gradient";
import { useSearchParams } from "expo-router/build/hooks";
import React, { useEffect, useRef, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  Image,
  ScrollView,
  Animated,
} from "react-native";

const StoryDetails = () => {
  const searchParams = useSearchParams();
  const id = searchParams.get("id");

  const [story, setStory] = useState<Story>();

  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const scrollY = useRef(new Animated.Value(0)).current;
  const lastScrollY = useRef(0).current; // Keep track of the last scroll position

  useEffect(() => {
    const fetchStory = async () => {
      try {
        const response = await axios.get(
          `${process.env.EXPO_PUBLIC_BASE_API_URL}api/stories/${id}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${process.env.EXPO_PUBLIC_AUTH_TOKEN}`,
            },
          }
        );

        setStory(response.data);
      } catch (error) {
        console.error("Error fetching story:", error);
      }
    };

    if (id) {
      fetchStory();
    }
  }, [id]);

  if (!story) return "Tell'em there is no story here!";

  const images = [
    `${process.env.EXPO_PUBLIC_BASE_API_URL}${story.image}`,
    `${process.env.EXPO_PUBLIC_BASE_API_URL}1736715174378_majkl-velner-nKY59_d9FlA-unsplash.jpg`,
    // `${process.env.EXPO_PUBLIC_BASE_API_URL}1736716303973_BC2950B7-A27C-475A-B4B5-74519CA60962.jpg`,
    `${process.env.EXPO_PUBLIC_BASE_API_URL}1736932901750_88EC2918-52C4-4BBF-AACA-B7AC9FEEEBD2.jpg`,
  ];
  const handleScroll = Animated.event(
    [{ nativeEvent: { contentOffset: { y: scrollY } } }],
    {
      useNativeDriver: false,
      listener: (event: any) => {
        const scrollPosition = event.nativeEvent.contentOffset.y;
        const imageIndex = Math.min(
          Math.floor(scrollPosition / 300), // Adjust number to control image change frequency
          images.length - 1
        );
        console.log(imageIndex, "index");
        if (imageIndex < 0) setCurrentImageIndex(0);
        else setCurrentImageIndex(imageIndex);
      },
    }
  );

  // Interpolating the scrollY value to create a fade-in/fade-out effect for the image
  const fadeAnim = scrollY.interpolate({
    inputRange: [0, 300, 600],
    outputRange: [1, 0.3, 1],
    extrapolate: "clamp",
  });

  return (
    <View style={styles.container}>
      <Animated.ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContainer}
        onScroll={handleScroll}
        scrollEventThrottle={100} // Improve performance by limiting scroll events
      >
        <Text style={styles.title}>{story.title}</Text>
        <View style={styles.divider} />
        <View style={{ alignItems: "flex-start", flexDirection: "row" }}>
          <DonationButton storyId={story.id} />
          <ShareButton storyId={story.id} />
        </View>
        <Text style={styles.content}>{story.content}</Text>
      </Animated.ScrollView>

      {/* Sticky Image with Linear Gradient */}
      <View style={styles.imageContainer}>
        <Animated.Image
          source={{ uri: images[currentImageIndex] }}
          style={[styles.image, { opacity: fadeAnim }]}
        />
        <LinearGradient
          colors={["rgba(255,255,255,0)", "rgba(255,255,255,1)"]}
          style={styles.gradientOverlay}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    padding: 32,
  },
  scrollView: {
    flex: 1,
  },
  scrollContainer: {
    paddingBottom: 250, // Padding to prevent content hiding behind sticky image
  },
  title: {
    fontSize: 42,
    textAlign: "left",
    fontFamily: "MontserratBold",
  },
  content: {
    marginTop: 16,
    fontSize: 16,
    lineHeight: 28,
    fontFamily: "MontserratRegular",
  },
  imageContainer: {
    position: "absolute", // Stick the image to the bottom of the screen
    bottom: 0,
    left: 0,
    right: 0,
    height: 250, // Image height
    backgroundColor: "#fff",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 100 },
    shadowOpacity: 0.1,
  },
  gradientOverlay: {
    position: "absolute",
    bottom: 250,
    left: 0,
    right: 0,
    height: 80,
  },
  image: {
    width: "100%",
    height: "100%",
    resizeMode: "cover",
  },
  divider: {
    width: 96,
    height: 8,
    backgroundColor: "black",
  },
});

export default StoryDetails;

import Feather from "@expo/vector-icons/Feather";
import React from "react";
import { TouchableOpacity, Text, StyleSheet, Share, Alert } from "react-native";

interface Props {
  storyId: number;
}

export default function ShareButton({ storyId }: Props) {
  const onShare = async () => {
    try {
      const result = await Share.share({
        message: `Tell 'em after reading this amazing story! 🌟 ${process.env.EXPO_PUBLIC_BASE_API_URL}/story/${storyId}`,
      });

      if (result.action === Share.sharedAction) {
        if (result.activityType) {
          console.log("Shared with activity type: ", result.activityType);
        } else {
          console.log("Shared successfully!");
        }
      } else if (result.action === Share.dismissedAction) {
        console.log("Share dismissed");
      }
    } catch (error) {
      Alert.alert("Error", "Something went wrong while sharing!");
      console.error(error);
    }
  };

  return (
    <TouchableOpacity style={styles.button} onPress={onShare}>
      <Feather name="send" size={24} color="white" />
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  button: {
    backgroundColor: "#333",
    width: 40,
    marginTop: 10,
    marginLeft: 10,
    paddingVertical: 10,
    // paddingHorizontal: 24,
    borderRadius: 8,
    alignItems: "center",
    height: 42,
  },
});

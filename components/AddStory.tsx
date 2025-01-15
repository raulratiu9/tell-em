import React, { useState } from "react";
import {
  View,
  TextInput,
  Button,
  StyleSheet,
  Text,
  Image,
  Keyboard,
  TouchableWithoutFeedback,
} from "react-native";
import axios from "axios";
import * as ImagePicker from "expo-image-picker";
import { LinearGradient } from "expo-linear-gradient";
import StoryCard from "./StoryCard";

export default function AddStory() {
  const [title, setTitle] = useState("");
  const [image, setImage] = useState<null | ImagePicker.ImagePickerAsset>(null);
  const [content, setContent] = useState("");
  const [responseMessage, setResponseMessage] = useState("");

  const handleImagePick = async () => {
    let result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [4, 3],
      quality: 1,
    });

    if (!result.canceled) {
      setImage(result.assets[0]);
    }
  };

  const handleSubmit = async () => {
    if (!title || !content) {
      setResponseMessage("Title and content are required.");
      return;
    }

    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);

    if (image) {
      const uri = image.uri;
      const type = uri.substring(uri.lastIndexOf(".") + 1);
      const fileName = uri.replace(/^.*[\\\/]/, "");
      const imageBlob = await fetch(uri)
        .then((res) => res.blob())
        .catch((error) => {
          console.error("Error fetching image blob:", error);
        });

      if (imageBlob && uri) {
        formData.append(
          "image",
          {
            uri: uri,
            name: fileName,
            type: `image/${type}`,
          },
          fileName
        );
      } else {
        console.log("Image blob is not valid");
      }
    }

    formData.append("author_id", 2);
    for (let pair of formData.entries()) {
      console.log(pair[0], pair[1]);
    }
    console.log(formData);
    try {
      const response = await axios.post(
        `${process.env.EXPO_PUBLIC_BASE_API_URL}api/stories`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${process.env.EXPO_PUBLIC_AUTH_TOKEN}`,
          },
        }
      );

      setResponseMessage("Story added successfully!");
      setTitle("");
      setContent("");
      setImage(null);
      console.log(response);
    } catch (error) {
      setResponseMessage(`Error adding story. ${error}`);
      console.error(error);
    }
  };

  const storyPreview = {
    id: 0,
    title,
    content,
    image: image?.uri,
  };

  return (
    <TouchableWithoutFeedback onPress={() => Keyboard.dismiss()}>
      <View style={styles.container}>
        <Text style={styles.header}>Tell'em what you've done</Text>

        <TextInput
          style={styles.input}
          placeholder="Title"
          value={title}
          onChangeText={setTitle}
        />

        <TextInput
          style={[styles.input, styles.textArea]}
          placeholder="Content"
          value={content}
          onChangeText={setContent}
          multiline
        />

        {/* Image Picker Button */}
        <Button title="Pick an image" onPress={handleImagePick} />
        <StoryCard story={storyPreview} isPreview />
        <Button title="Submit Story" onPress={handleSubmit} />

        {responseMessage ? (
          <Text style={styles.responseMessage}>{responseMessage}</Text>
        ) : null}
      </View>
    </TouchableWithoutFeedback>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    justifyContent: "center",
    fontFamily: "MontserratBlack",
  },
  header: {
    fontSize: 24,
    fontWeight: "bold",
    marginVertical: 20,
    textAlign: "left",
  },
  input: {
    height: 50,
    borderWidth: 1,
    marginBottom: 20,
    borderRadius: 16,
    paddingHorizontal: 10,
  },
  textArea: {
    height: 100,
  },
  responseMessage: {
    textAlign: "center",
    marginTop: 10,
    fontSize: 16,
    color: "red",
  },
});

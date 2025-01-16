import { useState } from "react";
import {
  View,
  TextInput,
  Button,
  StyleSheet,
  Text,
  Keyboard,
  TouchableWithoutFeedback,
} from "react-native";
import * as ImagePicker from "expo-image-picker";
import StoryCard from "@/components/StoryCard";
import Toast from "react-native-toast-message";
import { Story } from "@/types";
import FontAwesome5 from "@expo/vector-icons/FontAwesome5";
import { router } from "expo-router";
import postStory from "@/api/postStory";

export default function AddStory() {
  const [story, setStory] = useState({
    title: "",
    content: "",
    image: null as null | ImagePicker.ImagePickerAsset,
  });
  const { title, content, image } = story;

  const handleImagePick = async () => {
    let result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [4, 3],
      quality: 1,
    });

    if (!result.canceled) {
      setStory({ ...story, image: result.assets[0] });
    }
  };

  const handleSubmit = async () => {
    if (!story) {
      Toast.show({
        type: "error",
        text1: "You need to fill in all the fields before creating a story",
      });
      return;
    }

    const formData = new FormData();

    const authorId = "1"; //TODO: Remove hardcoded value after implementing OAuth2 properly
    formData.append("title", title);
    formData.append("content", content);
    formData.append("author_id", authorId);

    if (image) {
      const uri = image.uri;
      const type = uri.substring(uri.lastIndexOf(".") + 1);
      const fileName = uri.replace(/^.*[\\\/]/, "");
      const imageBlob = await fetch(uri)
        .then((res) => res.blob())
        .catch((error) => {
          Toast.show({
            type: "error",
            text1: "Failed to upload the image",
            text2: error.message,
          });
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
        Toast.show({
          type: "success",
          text1: "Image has been uploaded",
        });
      } else {
        Toast.show({
          type: "error",
          text1: "Image is not valid",
        });
      }
    }

    try {
      const response = await postStory(formData);

      router.push(`/story/${response.data}`);

      Toast.show({
        type: "success",
        text1: "Tell'em you're story is ready to read",
      });

      setStory({
        title: "",
        content: "",
        image: null as null | ImagePicker.ImagePickerAsset,
      });
    } catch (error) {
      Toast.show({
        type: "error",
        text1: "Internal Server Eror",
      });
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
          onChangeText={(title) =>
            setStory((story) => ({ ...story, title: title }))
          }
        />
        <TextInput
          style={[styles.input, styles.textArea]}
          placeholder="Content"
          value={story.content}
          onChangeText={(content) =>
            setStory((story) => ({ ...story, content: content }))
          }
          multiline
        />
        <Button
          title="Show 'em through a representative picture"
          onPress={handleImagePick}
        />
        <View style={styles.previewInfo}>
          <FontAwesome5 name="info-circle" size={24} color="white" />
          <Text
            style={{
              color: "#f4f4f4",
              marginLeft: 10,
            }}
          >
            This preview is not for you, is for thousand of people, think twice
            how interesting will look for them, will they be convinced to read
            your story?
          </Text>
        </View>
        <StoryCard story={storyPreview as Story} isPreview />
        <Button title="Tell 'em" onPress={handleSubmit} />
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
  previewInfo: {
    flexDirection: "row",
    backgroundColor: "#333",
    color: "#f4f4f4",
    borderRadius: 8,
    padding: 16,
    marginVertical: 16,
  },
});

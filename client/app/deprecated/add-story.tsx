import { useState } from 'react';
import {
  View,
  StyleSheet,
  Text,
  Keyboard,
  TouchableWithoutFeedback,
  TouchableOpacity,
  ScrollView,
} from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import StoryCard from '@/components/StoryCard';
import Toast from 'react-native-toast-message';
import { Frame, Story } from '@/types';
import FontAwesome5 from '@expo/vector-icons/FontAwesome5';
import { router } from 'expo-router';
import postStory from '@/api/postStory';
import Input from '@/components/Input';
import Button from '@/components/common/Button';
import FontAwesome from '@expo/vector-icons/FontAwesome';
import StoryMap from '@/components/add-story/StoryMap';

export default function AddStory() {
  const [story, setStory] = useState({
    title: '',
    content: '',
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
        type: 'error',
        text1: 'You need to fill in all the fields before creating a story',
      });
      return;
    }

    const formData = new FormData();

    const authorId = '1'; //TODO: Remove hardcoded value after implementing OAuth2 properly
    formData.append('title', title);
    formData.append('content', content);
    formData.append('author_id', authorId);

    if (image) {
      const uri = image.uri;
      const type = uri.substring(uri.lastIndexOf('.') + 1);
      const fileName = uri.replace(/^.*[\\\/]/, '');
      const imageBlob = await fetch(uri)
        .then((res) => res.blob())
        .catch((error) => {
          Toast.show({
            type: 'error',
            text1: 'Failed to upload the image',
            text2: error.message,
          });
        });

      if (imageBlob && uri) {
        formData.append(
          'image',
          {
            uri: uri,
            name: fileName,
            type: `image/${type}`,
          },
          fileName,
        );
        Toast.show({
          type: 'success',
          text1: 'Image has been uploaded',
        });
      } else {
        Toast.show({
          type: 'error',
          text1: 'Image is not valid',
        });
      }
    }

    try {
      const response = await postStory(formData);

      router.push(`/story/${response.data}`);

      Toast.show({
        type: 'success',
        text1: "Tell'em you're story is ready to read",
      });

      setStory({
        title: '',
        content: '',
        image: null as null | ImagePicker.ImagePickerAsset,
      });
    } catch (error) {
      Toast.show({
        type: 'error',
        text1: 'Internal Server Eror',
        text2: (error as Error).message,
      });
    }
  };

  const storyPreview = {
    id: 0,
    title,
    content,
    image: image?.uri,
  };

  const mockFrames: Frame[] = [
    {
      id: 1,
      content: 'You wake up in a mysterious forest.',
      image:
        'https://images.pexels.com/photos/31295481/pexels-photo-31295481/free-photo-of-scenic-autumn-view-of-kiyomizu-dera-in-kyoto-japan.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',
      storyId: 1,
      choices: [
        { id: 1, name: 'Go left', frameId: 2 },
        { id: 2, name: 'Go right', frameId: 3 },
      ],
    },
    {
      id: 2,
      content: 'You encounter a river.',
      image:
        'https://images.pexels.com/photos/9330653/pexels-photo-9330653.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',
      storyId: 1,
      choices: [],
    },
    {
      id: 3,
      content: 'You see a bear in the distance.',
      image:
        'https://images.pexels.com/photos/26955592/pexels-photo-26955592/free-photo-of-a-river-runs-through-a-grassy-field.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',
      storyId: 1,
      choices: [],
    },
    {
      id: 4,
      content: 'You find a hidden cabin. Is this the end?',
      image:
        'https://images.pexels.com/photos/31152265/pexels-photo-31152265/free-photo-of-autumn-leaves-and-rocky-mountain-landscape.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',
      storyId: 1,
      choices: [],
    },
  ];
  if (true) {
    return (
      <View style={styles.container}>
        <StoryMap frames={mockFrames} />;
      </View>
    );
  }

  return (
    <TouchableWithoutFeedback onPress={() => Keyboard.dismiss()}>
      <ScrollView showsVerticalScrollIndicator={false}>
        <View style={styles.container}>
          <StoryMap frames={mockFrames} />
          <Text style={styles.header}>Tell'em what you've done</Text>
          <Input
            placeholder="Title"
            value={title}
            onChangeText={(title) => setStory((story) => ({ ...story, title: title }))}
          />
          <Input
            style={styles.textArea}
            placeholder="Content"
            value={story.content}
            onChangeText={(content) =>
              setStory((story) => ({ ...story, content: content }))
            }
            multiline
          />
          <Text style={styles.header}>{!image ? 'Upload a file' : 'Preview'}</Text>
          {!image ? (
            <TouchableOpacity
              onPress={handleImagePick}
              style={{
                borderColor: '#333',
                borderWidth: 2,
                marginBottom: 20,
                borderRadius: 8,
              }}
            >
              <FontAwesome
                name="file-picture-o"
                size={68}
                color="#333"
                style={{
                  marginVertical: 60,
                  margin: 'auto',
                }}
              />
            </TouchableOpacity>
          ) : (
            <>
              <View style={styles.previewInfo}>
                <FontAwesome5 name="info-circle" size={24} color="white" />
                <Text
                  style={{
                    color: '#f4f4f4',
                    marginLeft: 10,
                  }}
                >
                  This preview is not for you, is for thousand of people, think twice how
                  interesting will look for them, will they be convinced to read your
                  story?
                </Text>
              </View>
              <StoryCard story={storyPreview as Story} isPreview />
            </>
          )}
          <Button title="Tell 'em" onPress={handleSubmit} disabled={!image} />
        </View>
      </ScrollView>
    </TouchableWithoutFeedback>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    justifyContent: 'center',
    fontFamily: 'MontserratBlack',
    overflow: 'scroll',
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginVertical: 20,
    textAlign: 'left',
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
    textAlign: 'center',
    marginTop: 10,
    fontSize: 16,
    color: 'red',
  },
  previewInfo: {
    flexDirection: 'row',
    backgroundColor: '#333',
    color: '#f4f4f4',
    borderRadius: 8,
    padding: 16,
    marginVertical: 16,
  },
});

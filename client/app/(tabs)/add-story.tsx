import React, { useEffect, useState } from 'react';
import {
  View,
  StyleSheet,
  Text,
  Keyboard,
  TouchableWithoutFeedback,
  TouchableOpacity,
  ScrollView,
  ActivityIndicator,
} from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import StoryCard from '@/components/StoryCard';
import Toast from 'react-native-toast-message';
import { Story } from '@/types';
import FontAwesome5 from '@expo/vector-icons/FontAwesome5';
import { router } from 'expo-router';
import postStory from '@/api/postStory';
import Input from '@/components/Input';
import Button from '@/components/Button';
import FontAwesome from '@expo/vector-icons/FontAwesome';
import { Audio } from 'expo-av';
import { Base64 } from 'js-base64';
import { Picker } from '@react-native-picker/picker';
import MaterialIcons from '@expo/vector-icons/MaterialIcons';
import Ionicons from '@expo/vector-icons/Ionicons';
import { Sound } from 'expo-av/build/Audio';

export interface VoiceDto {
  name: string;
  gender: string;
  displayName: string;
}

export default function AddStory() {
  const [story, setStory] = useState({
    title: '',
    content: '',
    image: null as null | ImagePicker.ImagePickerAsset,
    audio: null as null | Audio.Sound,
  });
  const [voiceover, setVoiceover] = useState({
    isLoading: false,
    isPlaying: false,
    gender: 'MALE',
    voice: { name: 'en-US-Casual-K', gender: 'MALE' },
  });
  const [voices, setVoices] = useState([]);
  const [bgMusic, setBgMusic] = useState(null as null | Sound);
  const { title, content, image, audio } = story;

  useEffect(() => {
    const fetchVoices = async () => {
      try {
        const response = await fetch(
          `${process.env.EXPO_PUBLIC_BASE_API_URL}api/voiceover/voices`,
        );
        if (response.ok) {
          const voiceList = await response.json();
          setVoices(voiceList);
          setVoiceover((prevState) => ({ ...prevState, voice: voiceList[0] }));
        } else {
          console.error('Error fetching voices:', response.status, await response.text());
        }
      } catch (error) {
        console.error('Error:', error);
      } finally {
        setVoiceover((prevState) => ({ ...prevState, isLoading: false }));
      }
    };

    fetchVoices();
  }, []);

  const playBackgroundMusic = async () => {
    const { sound } = await Audio.Sound.createAsync(require('../../assets/music.mp3'));
    sound.setVolumeAsync(0.5);
    setBgMusic(sound);
    await sound.playAsync();
  };

  const generateTTS = async (isPreview: boolean = false) => {
    const voiceText = isPreview ? "Tell 'em your story with me" : content;
    setVoiceover((prevState) => ({ ...prevState, isLoading: true }));
    try {
      const response = await fetch(
        `${process.env.EXPO_PUBLIC_BASE_API_URL}api/voiceover/tts`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            content: voiceText,
            voice: voiceover.voice,
          }),
        },
      );

      if (response.ok) {
        const audioBytes = await response.arrayBuffer();

        const base64Audio = Base64.fromUint8Array(new Uint8Array(audioBytes));
        const audioUri = `data:audio/mpeg;base64,${base64Audio}`;

        const { sound } = await Audio.Sound.createAsync({ uri: audioUri });
        await sound.playAsync();

        !isPreview && setStory((prevState) => ({ ...prevState, audio: sound }));
      } else {
        console.error('Error generating TTS:', response.status, await response.text());
      }
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setVoiceover((prevState) => ({ ...prevState, isLoading: false }));
    }
  };

  const playSound = async () => {
    if (audio) {
      setVoiceover((prevState) => ({ ...prevState, isPlaying: true }));
      try {
        await audio.playAsync();
        await audio.replayAsync();
      } catch (error) {
        console.error('Error playing sound:', error);
      } finally {
        setVoiceover((prevState) => ({ ...prevState, isPlaying: false }));
      }
    }
  };

  const stopSound = async () => {
    if (audio && voiceover.isPlaying) {
      try {
        await audio.stopAsync();
        setVoiceover((prevState) => ({ ...prevState, isPlaying: false }));
      } catch (error) {
        console.error('Error stopping sound:', error);
      }
    }
  };

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

    const authorId = 'f0ec5ca6-0820-4a62-8302-6d8f95ce3a8a'; //TODO: Remove hardcoded value after implementing OAuth2 properly
    formData.append('title', title);
    formData.append('content', content);
    formData.append('author_id', authorId);

    if (image) {
      const uri = image.uri;
      const type = uri.substring(uri.lastIndexOf('.') + 1);
      const fileName = uri.replace(/^.*[\\/]/, '');
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

      if (audio) {
        const storeResponse = await fetch(
          `${process.env.EXPO_PUBLIC_BASE_API_URL}/api/voiceover/`,
          {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ storyId: response.data, audio }),
          },
        );

        if (storeResponse) {
          Toast.show({
            type: 'success',
            text1: 'Voiceover has been uploaded',
          });
        } else {
          Toast.show({
            type: 'error',
            text1: 'Audio is not valid',
          });
        }
      }

      router.push(`/story/${response.data}`);

      Toast.show({
        type: 'success',
        text1: "Tell'em you're story is ready to read",
      });

      setStory({
        title: '',
        content: '',
        image: null as null | ImagePicker.ImagePickerAsset,
        audio: null,
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

  return (
    <TouchableWithoutFeedback onPress={() => Keyboard.dismiss()}>
      <ScrollView showsVerticalScrollIndicator={false}>
        <View style={styles.container}>
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
          <Text>Select voice</Text>
          {voiceover.isLoading ? (
            <ActivityIndicator />
          ) : (
            <Picker
              selectedValue={voiceover.voice}
              style={{ height: 50, width: 200 }}
              onValueChange={(voice: VoiceDto) =>
                setVoiceover((voiceover) => ({ ...voiceover, voice: voice }))
              }
            >
              {voices.map((voice: VoiceDto) => (
                <Picker.Item
                  key={voice.name}
                  label={`${voice.displayName}`}
                  value={voice}
                />
              ))}
            </Picker>
          )}
          <Button
            title={'Play voice'}
            onPress={() => generateTTS(true)}
            disabled={voiceover.isLoading}
          />
          {content && (
            <Button
              title={'Generate voiceover'}
              onPress={() => generateTTS(false)}
              disabled={voiceover.isLoading}
            />
          )}
          {audio && (
            <View style={styles.audioControls}>
              <Button
                title={voiceover.isPlaying ? 'Stop' : 'Play'}
                onPress={voiceover.isPlaying ? stopSound : playSound}
                disabled={voiceover.isLoading}
              >
                {voiceover.isPlaying ? (
                  <MaterialIcons name="play-circle" size={24} color="white" />
                ) : (
                  <FontAwesome5 name="play-circle" size={24} color="white" />
                )}
              </Button>
              <Button onPress={bgMusic ? () => bgMusic.stopAsync() : playBackgroundMusic}>
                <Ionicons name="musical-note" size={24} color="white" />
              </Button>
            </View>
          )}
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
            <React.Fragment>
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
            </React.Fragment>
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
  textInput: {
    height: 150,
    borderColor: 'gray',
    borderWidth: 1,
    marginBottom: 20,
    padding: 10,
    textAlignVertical: 'top', // Align text to the top
  },
  audioControls: {
    flexDirection: 'row',
    justifyContent: 'space-around', // Distribute buttons evenly
    marginTop: 10,
  },
});

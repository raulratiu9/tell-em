import { useEffect, useMemo, useRef, useState } from 'react';
import * as ImagePicker from 'expo-image-picker';
import {
  Dimensions,
  Image,
  ImageProps,
  Keyboard,
  ScrollView,
  Text,
  TextInput,
  TouchableOpacity,
  TouchableWithoutFeedback,
  View,
  StyleSheet,
} from 'react-native';
import FontAwesome from '@expo/vector-icons/FontAwesome';
import { Frame } from '@/types';

interface Props {
  frame: Frame;
  onSave: (updatedFrame: Frame) => void;
}

const { width } = Dimensions.get('screen');
const _imageWidth = width * 0.6;
const _imageHeight = _imageWidth * 1.8;

export function FrameDetails({ frame, onSave }: Props) {
  const { id, content, image, storyId, choices } = frame;

  const [data, setData] = useState({ ...frame });

  useEffect(() => {
    setData({ ...frame });
  }, [frame]);

  const handleImagePick = async () => {
    let result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [9, 16],
      quality: 1,
    });

    if (!result.canceled) {
      setData({
        ...data,
        image: result.assets[0]?.uri,
      });
    }
  };

  const inputRef = useRef<TextInput>(null);

  const isFrameEdited = useMemo(() => {
    return (
      data.content.trim() !== (frame.content || '').trim() ||
      data.image.trim() !== (frame.image || '').trim()
    );
  }, [data, frame]);

  return (
    <TouchableWithoutFeedback onPress={() => Keyboard.dismiss()}>
      <ScrollView showsVerticalScrollIndicator={false} style={{ flex: 1 }}>
        <View style={styles.container}>
          <TextInput
            style={styles.textInput}
            placeholder={`Insert the content of the frame ${id} `}
            value={data?.content}
            onChangeText={(content) => setData((data) => ({ ...data, content: content }))}
            placeholderTextColor="#bbb"
            multiline
            numberOfLines={5}
            maxLength={350}
          />
          <TouchableOpacity
            onPress={() => {
              if (data?.image) {
                inputRef.current?.focus();
              } else {
                handleImagePick();
              }
            }}
            style={styles.imageWrapper}
          >
            {data?.image ? (
              <Image
                source={
                  {
                    uri: data.image ?? '',
                  } as ImageProps
                }
                style={styles.image}
              />
            ) : (
              <View style={styles.imagePreview}>
                <FontAwesome
                  name="file-picture-o"
                  size={64}
                  color="#333"
                  style={styles.previewIcon}
                />
                <Text style={styles.header}>
                  {!data.image ? 'Upload an image' : 'Preview'}
                </Text>
              </View>
            )}
          </TouchableOpacity>
        </View>
        <TouchableOpacity
          style={[styles.saveButton, !isFrameEdited && styles.saveButtonDisabled]}
          onPress={() => onSave(data)}
          disabled={!isFrameEdited}
        >
          <Text style={styles.saveButtonText}>Save changes</Text>
        </TouchableOpacity>
      </ScrollView>
    </TouchableWithoutFeedback>
  );
}

const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    fontFamily: 'MontserratBlack',
    borderRadius: 16,
    borderColor: 'transparent',
    borderWidth: 1,
  },
  imageWrapper: {
    alignSelf: 'center',
    borderRadius: 16,
    overflow: 'hidden',
    height: _imageHeight,
    borderColor: 'rgba(252, 251, 251, 0.6)',
    borderWidth: 1,
    marginTop: 0,
    backgroundColor: 'rgba(252, 251, 251, 0.9)',
  },
  image: {
    height: _imageHeight,
    width: _imageWidth,
    resizeMode: 'cover',
  },
  textInput: {
    backgroundColor: 'rgb(255, 255, 255)',
    borderColor: '#ddd',
    borderWidth: 1,
    borderRadius: 10,
    marginVertical: 10,
    paddingHorizontal: 12,
    paddingVertical: 0,
    fontSize: 16,
    color: '#333',
    fontFamily: 'MontserratRegular',
    minHeight: 100,
    maxHeight: 200,
    width: _imageWidth * 1.3,
    alignSelf: 'center',
  },
  header: {
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'left',
  },
  imagePreview: {
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  previewIcon: {
    padding: 95,
  },
  description: {
    backgroundColor: 'rgba(255,255,255,0.1)',
    borderColor: '#ddd',
    borderWidth: 1,
    borderRadius: 10,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 16,
    color: 'white',
    minHeight: 100,
    maxHeight: 250,
  },
  saveButton: {
    marginTop: 10,
    backgroundColor: '#333',
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 8,
    opacity: 1,
  },
  saveButtonDisabled: {
    backgroundColor: '#ccc',
    opacity: 0.6,
  },
  saveButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
    fontFamily: 'MontserratRegular',
    textAlign: 'center',
  },
});

import { Frame } from '@/types';
import {
  FlatList,
  Pressable,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

import { useEffect, useState } from 'react';
import DotBackground from './DotBackground';
import MapItem from './MapItem';
import { FrameDetails } from './FrameDetails';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { ScrollView } from 'react-native';
import Toast from 'react-native-toast-message';
import FontAwesome from '@expo/vector-icons/FontAwesome';
import AddChoices from './AddChoices';

interface Props {
  frames: Frame[];
}

export default function StoryMap({ frames }: Props) {
  const [currentFrameId, setCurrentFrameId] = useState(0);
  const [framesData, setFramesData] = useState<Frame[]>(frames);
  const [isAddingChoice, setIsAddingChoice] = useState<boolean>(false);

  const saveFramesToStorage = async (framesData: Frame[]) => {
    try {
      const json = JSON.stringify(framesData);
      await AsyncStorage.setItem('story_frames', json);
    } catch (e) {
      console.error('Error saving frames:', e);
    }
  };

  const loadFramesFromStorage = async () => {
    try {
      const json = await AsyncStorage.getItem('story_frames');
      return json != null ? JSON.parse(json) : [];
    } catch (e) {
      console.error('Error loading frames:', e);
      return [];
    }
  };

  useEffect(() => {
    loadFramesFromStorage().then((savedFrames) => {
      if (savedFrames.length) {
        setFramesData(savedFrames);
      }
    });
  }, []);

  useEffect(() => {
    saveFramesToStorage(framesData);
  }, [framesData]);

  const selectedFrame = frames.find((frame) => currentFrameId === frame.id);

  const addNewFrame = () => {
    const newFrame: Frame = {
      id: framesData.length + 1,
      content: 'New frame content',
      image: '', // You can set an image if necessary
      storyId: 0,
      choices: [],
    };

    // Update the framesData state with the new frame
    setFramesData([...framesData, newFrame]);

    // Optionally, set the new frame as the selected frame
    setCurrentFrameId(newFrame.id);
  };

  const addChoiceToFrame = (frameId: number) => {
    const newChoice = {
      id: Date.now(), // Unique ID based on current timestamp
      name: 'New Choice', // Default name for the new choice
      frameId: frameId,
    };

    setFramesData((prevFrames: Frame[]) =>
      prevFrames.map((frame) =>
        frame.id === frameId
          ? { ...frame, choices: [...frame.choices, newChoice] }
          : frame,
      ),
    );

    Toast.show({
      type: 'success',
      text1: 'Choice added to frame!',
    });
  };

  const handleSaveChoice = (
    decision1: string,
    decision2: string,
    file1: string,
    file2: string,
  ) => {
    const updatedFrames = framesData.map((frame) =>
      frame.id === currentFrameId
        ? {
            ...frame,
            choices: [
              ...frame.choices,
              { id: Date.now(), name: 'New Choice', frameId: currentFrameId, decision1, decision2, file1, file2 }, // Ensure all required properties are included
            ],
          }
        : frame,
    );
    setFramesData(updatedFrames);
    setIsAddingChoice(false); // Închidem formularul după salvare
  };

  return (
    <DotBackground>
      <ScrollView style={styles.container}>
        <FlatList
          data={framesData}
          keyExtractor={(item) => item.id.toString()}
          horizontal
          renderItem={({ item, index }) => {
            const isSelected = currentFrameId === item.id;
            return (
              <View key={item.id} style={styles.nodeContainer}>
                <MapItem
                  onPress={() => setCurrentFrameId(item.id)}
                  frame={item}
                  isItemSelected={isSelected}
                />
                <FlatList
                  data={item.choices}
                  keyExtractor={(item) => `frame-${item.id}`}
                  renderItem={({ item }) => (
                    <View style={styles.branchContainer}>
                      <MapItem
                        onPress={() => setCurrentFrameId(item.id)}
                        frame={framesData.find((f) => f.id === item.id) ?? ({} as Frame)}
                        isItemSelected={currentFrameId === item.frameId}
                      />
                    </View>
                  )}
                  horizontal
                />
              </View>
            );
          }}
        />

        <FrameDetails
          frame={selectedFrame ?? framesData[0]}
          onSave={(updatedFrame) => {
            setFramesData((prev) =>
              prev.map((f) => (f.id === updatedFrame.id ? { ...f, ...updatedFrame } : f)),
            );
            Toast.show({
              type: 'success',
              text1: 'Frame has been added to your story!',
            });
          }}
        />

        {isAddingChoice && currentFrameId !== 0 && (
          <AddChoices onSaveChoice={handleSaveChoice} />
        )}

        <TouchableOpacity onPress={addNewFrame} style={styles.addNewFrameButton}>
          <Text style={styles.addButtonText}>+ Add new frame</Text>
        </TouchableOpacity>
        {currentFrameId !== 0 && (
          <TouchableOpacity
            onPress={() => addChoiceToFrame(currentFrameId)}
            style={styles.addChoiceButton}
          >
            <Text style={styles.addButtonText}>Add Choices</Text>
          </TouchableOpacity>
        )}
      </ScrollView>
    </DotBackground>
  );
}

const styles = StyleSheet.create({
  container: {
    fontFamily: 'MontserratBlack',
    borderRadius: 16,
    borderColor: 'transparent',
    borderWidth: 1,
    gap: 0,
  },
  nodeContainer: {
    position: 'relative',
    flexDirection: 'row',
    paddingHorizontal: 26,
  },
  branchContainer: {
    position: 'absolute',
    top: 50,
    left: 0,
    flexDirection: 'row',
  },
  addButton: {
    position: 'absolute',
    bottom: 20,
    right: 20,
    backgroundColor: '#333',
    borderRadius: 50,
    padding: 15,
    elevation: 5,
  },
  plusButton: {
    marginTop: 20,
    backgroundColor: '#333',
    borderRadius: 50,
    width: 60,
    height: 60,
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'center',
    zIndex: 1,
  },
  plusText: {
    fontSize: 30,
    color: '#333',
  },
  addNewFrameButton: {
    marginTop: 20,
    backgroundColor: '#E3F2FD',
    borderRadius: 50,
    height: 60,
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'center',
  },
  addChoiceButton: {
    marginTop: 20,
    backgroundColor: '#E3F2FD',
    borderRadius: 50,
    height: 60,
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'center',
  },
  addButtonText: {
    fontSize: 20,
    color: '#333',
  },
});

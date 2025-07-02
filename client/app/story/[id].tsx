import BackdropPhoto from '@/components/BackdropPhoto';
import FramePhoto from '@/components/FramePhoto';
import { Frame, Story } from '@/types';
import MaterialIcons from '@expo/vector-icons/MaterialIcons';
import axios from 'axios';
import { useNavigation } from 'expo-router';
import { useSearchParams } from 'expo-router/build/hooks';
import { View, Text, StyleSheet, TouchableOpacity, Dimensions } from 'react-native';
import { useEffect, useState, useRef, useLayoutEffect } from 'react';

import Animated, {
  useAnimatedScrollHandler,
  useSharedValue,
  runOnJS,
} from 'react-native-reanimated';

const WINDOW_SIZE = 2;
const { width } = Dimensions.get('window');
const _imageWidth = width * 0.8;
const _spacing = 12;

export const navigationOptions = {
  headerTransparent: true,
  headerTitle: '',
  headerBackTitleVisible: false,
  headerTintColor: '#888',
};

export default function StoryDetails() {
  const searchParams = useSearchParams();
  const navigation = useNavigation();
  const id = searchParams.get('id');

  const [story, setStory] = useState<Story>({} as Story);
  const [activeNodes, setActiveNodes] = useState<Frame[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  console.log(story, 'story');
  const flatListRef = useRef<Animated.FlatList<any>>(null);
  const scrollX = useSharedValue(0);
  const [pendingScrollIndex, setPendingScrollIndex] = useState<number | null>(null);

  useEffect(() => {
    if (pendingScrollIndex !== null && activeNodes.length > pendingScrollIndex) {
      flatListRef.current?.scrollToIndex({
        index: pendingScrollIndex,
        animated: true,
      });
      setPendingScrollIndex(null);
    }
  }, [activeNodes, pendingScrollIndex]);

  const onHandleScroll = useAnimatedScrollHandler((event) => {
    scrollX.value = event.contentOffset.x / (_imageWidth + _spacing);
    runOnJS(setCurrentIndex)(Math.round(scrollX.value));
  });

  useLayoutEffect(() => {
    if (id) {
      navigation.setOptions({
        headerLeft: () => (
          <TouchableOpacity
            onPress={() => navigation.goBack()}
            style={{ flexDirection: 'row', alignItems: 'center', paddingHorizontal: 12 }}
          >
            <MaterialIcons name="arrow-back-ios" size={24} color="#888" />
          </TouchableOpacity>
        ),
        headerTransparent: true,
        headerTitle: '',
        headerBackTitleVisible: false,
        headerTintColor: '#f4f4f4',
      });
    }
  }, [navigation, id]);

  useEffect(() => {
    const fetchStory = async () => {
      try {
        const response = await axios.get<Story>(
          `${process.env.EXPO_PUBLIC_BASE_API_URL}api/stories/${id}`,
        );
        setStory(response.data);
        if (story?.firstFrame) {
          setActiveNodes([story.firstFrame]);
        }
      } catch (error) {
        console.error('Failed to fetch story', error);
        setStory({} as Story);
      }
    };

    if (id) fetchStory();
  }, [id]);

  useEffect(() => {
    if (story?.firstFrame) {
      setActiveNodes([story.firstFrame]);
      setCurrentIndex(0);
    }
  }, [story]);

  const findFrameById = (frame: any, frameId: string): any | null => {
    if (frame.frameId === frameId) return frame;

    for (const child of frame.nextFrames || []) {
      const result = findFrameById(child, frameId);
      if (result) return result;
    }

    return null;
  };

  const followLinearPath = (frame: Frame) => {
    let current = frame;
    const newPath: Frame[] = [];

    while (current.nextFrames?.length === 1) {
      const next = current.nextFrames[0];
      newPath.push(next);
      current = next;
    }

    return newPath;
  };
  const onHandleDecision = (nextFrameId: number) => {
    const currentFrame = activeNodes[activeNodes.length - 1];
    if (!currentFrame) return;

    const chosen = currentFrame.nextFrames.find(
      (frame: Frame) => frame.frameId === nextFrameId,
    );
    if (!chosen) return;

    const linearChain = followLinearPath(chosen);
    console.log(linearChain, 'linearChain');
    const newPath = [chosen, ...linearChain];

    setActiveNodes([...newPath]);

    const newIndex = newPath.length - 1;
    setCurrentIndex(1);
    setPendingScrollIndex(0);
  };

  const onRestartStory = () => {
    setTimeout(() => {
      flatListRef.current?.scrollToIndex({
        index: 0,
        animated: true,
      });
    }, 50);
    setActiveNodes([story.firstFrame]);
    setCurrentIndex(0);
  };

  const getItemLayout = (_: unknown, index: number) => ({
    length: _imageWidth + _spacing,
    offset: (_imageWidth + _spacing) * index,
    index,
  });

  if (!story) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Text>Loading story...</Text>
      </View>
    );
  }
  const isTrulyLinear = (frame: Frame): boolean => {
    return (
      frame?.nextFrames?.length === 1 && frame?.nextFrames[0]?.nextFrames?.length <= 1
    );
  };
  const traverseFrames = (frame: Frame | undefined): Frame[] => {
    if (!frame) return [];

    const result: Frame[] = [frame];

    if (Array.isArray(frame.nextFrames)) {
      for (const next of frame.nextFrames) {
        result.push(...traverseFrames(next));
      }
    }

    return result;
  };

  const allFrames = traverseFrames(story.firstFrame);
  const isLinearStory = isTrulyLinear(story.firstFrame) ? allFrames : activeNodes;
  const backdropImages = isLinearStory.map((node) => node.image);

  useEffect(() => {
    const updated = activeNodes.filter(
      (_, i) => Math.abs(i - currentIndex) <= WINDOW_SIZE,
    );
  }, [activeNodes, currentIndex]);

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <View style={StyleSheet.absoluteFillObject}>
        {backdropImages.map((photo, index) => (
          <BackdropPhoto key={index} photo={photo} index={index} scrollX={scrollX} />
        ))}
      </View>
      <Animated.FlatList
        ref={flatListRef}
        data={isLinearStory}
        keyExtractor={(item) => item?.frameId?.toString()}
        horizontal
        snapToInterval={_imageWidth + _spacing}
        decelerationRate="fast"
        contentContainerStyle={{
          gap: _spacing,
          paddingHorizontal: (width - _imageWidth) / 2,
        }}
        renderItem={({ item, index }) => {
          const isCurrent = index === currentIndex;
          const isBranching = item.nextFrames.length > 1;

          if (!isCurrent && isBranching) {
            return <View style={{ width: _imageWidth }} />;
          }
          return (
            <FramePhoto
              key={item?.frameId}
              item={item}
              index={index}
              scrollX={scrollX}
              onHandleDecision={onHandleDecision}
              allFrames={isBranching ? item.nextFrames : allFrames}
              onRestartStory={onRestartStory}
            />
          );
        }}
        onScroll={onHandleScroll}
        showsHorizontalScrollIndicator={false}
        scrollEventThrottle={16}
        getItemLayout={getItemLayout}
      />
    </View>
  );
}

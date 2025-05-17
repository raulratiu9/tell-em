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
  const [historyNodes, setHistoryNodes] = useState<string[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);

  const flatListRef = useRef<Animated.FlatList<any>>(null);
  const scrollX = useSharedValue(0);

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
      } catch (error) {
        console.error('Failed to fetch story', error);
        setStory({} as Story);
      }
    };

    if (id) fetchStory();
  }, [id]);

  useEffect(() => {
    if (!story || !story.frames?.length) return;

    const visited = new Set<number>();
    const active: Frame[] = [];
    const findFrame = (id: number) => story.frames.find((f) => f.frameId === id);

    const buildPath = (frameId: number) => {
      if (visited.has(frameId)) return;
      const frame = findFrame(frameId);
      if (!frame) return;
      visited.add(frameId);
      active.push(frame);
      const next = frame.choices?.[0]?.nextFrameId;
      if (next != null) buildPath(next);
    };

    buildPath(story.firstFrameId ?? story.frames[0].frameId);
    setActiveNodes(active);
  }, [story]);

  const onHandleDecision = (nextFrameId: number) => {
    const nextFrame = story.frames.find((f) => f.frameId === nextFrameId);
    if (!nextFrame) return;
    setHistoryNodes((prev) => [...prev, String(nextFrameId)]);
    setActiveNodes((prev) => [...prev, nextFrame]);
    setCurrentIndex(activeNodes.length);
  };

  const goBack = () => {
    if (historyNodes.length === 0) return;
    const updatedHistory = historyNodes.slice(0, -1);
    const updatedNodes = activeNodes.slice(0, -1);
    setHistoryNodes(updatedHistory);
    setActiveNodes(updatedNodes);
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

  const backdropImages = activeNodes.map((node) => node.image);

  const windowSize = 2;
  const [visibleFrames, setVisibleFrames] = useState<Frame[]>([]);

  useEffect(() => {
    console.log('intra deacu');

    const updated = activeNodes.filter(
      (_, i) => Math.abs(i - currentIndex) <= windowSize,
    );
    console.log('updated', updated);
    setVisibleFrames(updated);
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
        data={activeNodes}
        keyExtractor={(item) => item.frameId.toString()}
        horizontal
        snapToInterval={_imageWidth + _spacing}
        decelerationRate="fast"
        contentContainerStyle={{
          gap: _spacing,
          paddingHorizontal: (width - _imageWidth) / 2,
        }}
        renderItem={({ item, index }) => (
          <FramePhoto
            key={item.frameId}
            item={item}
            index={index}
            scrollX={scrollX}
            onHandleDecision={onHandleDecision}
            allFrames={story.frames}
          />
        )}
        onScroll={onHandleScroll}
        showsHorizontalScrollIndicator={false}
        scrollEventThrottle={16}
        getItemLayout={getItemLayout}
      />
    </View>
  );
}

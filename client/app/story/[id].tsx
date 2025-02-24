import DonationButton from '@/components/DonationButton';
import ShareButton from '@/components/ShareButton';
import { Story } from '@/types';
import MaterialIcons from '@expo/vector-icons/MaterialIcons';
import axios from 'axios';
import { LinearGradient } from 'expo-linear-gradient';
import { useNavigation } from 'expo-router';
import { useSearchParams } from 'expo-router/build/hooks';
import { useEffect, useRef, useState } from 'react';
import { View, Animated, TouchableOpacity } from 'react-native';
import { Box, Text } from '@gluestack-ui/themed';

import NotFoundScreen from '../+not-found';

const StoryDetails = () => {
  const searchParams = useSearchParams();
  const navigation = useNavigation();
  const id = searchParams.get('id');

  navigation.setOptions({
    title: "Tell 'em",
    headerLeft: () => (
      <View>
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          style={{ flexDirection: 'row', alignItems: 'center' }}
        >
          <MaterialIcons name="arrow-back-ios" size={24} color="black" />
          <Text style={{ fontFamily: '', color: 'black' }}>Back</Text>
        </TouchableOpacity>
      </View>
    ),
  });

  const [story, setStory] = useState<Story>();

  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const scrollY = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    const fetchStory = async () => {
      try {
        const response = await axios.get(
          `${process.env.EXPO_PUBLIC_BASE_API_URL}api/stories/${id}`,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${process.env.EXPO_PUBLIC_AUTH_TOKEN}`,
            },
          },
        );

        setStory(response.data);
      } catch (error) {
        console.error(error);
      }
    };

    if (id) {
      fetchStory();
    }
  }, [id]);

  if (!story)
    return (
      <NotFoundScreen>
        <Text>
          There was a problem rendering the story or the story does not exist anymore. Try
          again later.
        </Text>
      </NotFoundScreen>
    );

  const images = [
    `${process.env.EXPO_PUBLIC_BASE_API_URL}${story.image}`,
    `${process.env.EXPO_PUBLIC_BASE_API_URL}1736715174378_majkl-velner-nKY59_d9FlA-unsplash.jpg`,
    `${process.env.EXPO_PUBLIC_BASE_API_URL}1736932901750_88EC2918-52C4-4BBF-AACA-B7AC9FEEEBD2.jpg`,
  ];
  const handleScroll = Animated.event(
    [{ nativeEvent: { contentOffset: { y: scrollY } } }],
    {
      useNativeDriver: false,
      listener: (event: any) => {
        const scrollPosition = event.nativeEvent.contentOffset.y;
        const imageIndex = Math.min(
          Math.floor(scrollPosition / story.content.length),
          images.length - 1,
        );

        if (imageIndex < 0) setCurrentImageIndex(0);
        else setCurrentImageIndex(imageIndex);
      },
    },
  );

  const fadeAnim = scrollY.interpolate({
    inputRange: [0, 300, 600],
    outputRange: [1, 0.3, 1],
    extrapolate: 'clamp',
  });

  return (
    <Box {...containerStyles}>
      <Animated.ScrollView
        style={scrollView}
        contentContainerStyle={scrollContainerStyles as Record<string, string>}
        onScroll={handleScroll}
        scrollEventThrottle={100}
        showsVerticalScrollIndicator={false}
      >
        <Text {...titleStyles}>{story.title}</Text>
        <Box {...dividerStyles} />
        <Box {...buttonsContainerStyles}>
          <DonationButton storyId={story.id} />
          <ShareButton storyId={story.id} />
        </Box>
        <Text {...contentStyles}>{story.content}</Text>
      </Animated.ScrollView>

      <Box {...imageContainerStyles}>
        <Animated.Image
          source={{ uri: images[currentImageIndex] }}
          style={[imageStyles as Record<string, string>, { opacity: fadeAnim }]}
        />
        <LinearGradient
          colors={['rgba(255,255,255,0)', 'rgba(255,255,255,1)']}
          style={gradientOverlayStyles as Record<string, string | number>}
        />
      </Box>
    </Box>
  );
};

const containerStyles = {
  flex: 1,
  bg: '$white',
  p: '$8',
};

const scrollView = {
  flex: 1,
};

const scrollContainerStyles = {
  pb: '$64',
};

const titleStyles = {
  fontSize: '$xl',
  textAlign: 'left',
  fontFamily: 'MontserratBold',
};

const dividerStyles = {
  h: '1px',
  w: '24',
  bg: '$black',
};

const buttonsContainerStyles = {
  alignItems: 'flex-start',
  flexDirection: 'row',
};

const contentStyles = {
  mt: '$4',
  fontSize: '$md',
  lineHeight: '$7',
  fontFamily: 'MontserratRegular',
};

const imageContainerStyles = {
  position: 'absolute',
  bottom: 0,
  left: 0,
  right: 0,
  height: 250,
};

const imageStyles = {
  width: '100%',
  height: '100%',
  resizeMode: 'cover',
};

const gradientOverlayStyles = {
  position: 'absolute',
  bottom: 250,
  left: 0,
  right: 0,
  height: '$20',
};

export default StoryDetails;

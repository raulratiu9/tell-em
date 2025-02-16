import DonationButton from '@/components/DonationButton';
import ShareButton from '@/components/ShareButton';
import { Story } from '@/types';
import MaterialIcons from '@expo/vector-icons/MaterialIcons';
import axios from 'axios';
import { LinearGradient } from 'expo-linear-gradient';
import { useNavigation } from 'expo-router';
import { useSearchParams } from 'expo-router/build/hooks';
import { useEffect, useRef, useState } from 'react';
import { View, Text, StyleSheet, Animated, TouchableOpacity } from 'react-native';
import NotFoundScreen from '../+not-found';

const StoryDetails = () => {
  const searchParams = useSearchParams();
  const navigation = useNavigation();
  const id = searchParams.get('id');

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
        return (
          <NotFoundScreen>
            <Text>{(error as Error).message}</Text>
          </NotFoundScreen>
        );
      }
    };

    if (id) {
      fetchStory();
      navigation.setOptions({
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
    }
  }, [id, navigation]);

  if (!story) return <NotFoundScreen />;

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
    <View style={styles.container}>
      <Animated.ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContainer}
        onScroll={handleScroll}
        scrollEventThrottle={100}
        showsVerticalScrollIndicator={false}
      >
        <Text style={styles.title}>{story.title}</Text>
        <View style={styles.divider} />
        <View style={{ alignItems: 'flex-start', flexDirection: 'row' }}>
          <DonationButton storyId={story.id} />
          <ShareButton storyId={story.id} />
        </View>
        <Text style={styles.content}>{story.content}</Text>
      </Animated.ScrollView>

      <View style={styles.imageContainer}>
        <Animated.Image
          source={{ uri: images[currentImageIndex] }}
          style={[styles.image, { opacity: fadeAnim }]}
        />
        <LinearGradient
          colors={['rgba(255,255,255,0)', 'rgba(255,255,255,1)']}
          style={styles.gradientOverlay}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    padding: 32,
  },
  scrollView: {
    flex: 1,
  },
  scrollContainer: {
    paddingBottom: 250,
  },
  title: {
    fontSize: 42,
    textAlign: 'left',
    fontFamily: 'MontserratBold',
  },
  content: {
    marginTop: 16,
    fontSize: 16,
    lineHeight: 28,
    fontFamily: 'MontserratRegular',
  },
  imageContainer: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    height: 250,
  },
  gradientOverlay: {
    position: 'absolute',
    bottom: 250,
    left: 0,
    right: 0,
    height: 80,
  },
  image: {
    width: '100%',
    height: '100%',
    resizeMode: 'cover',
  },
  divider: {
    width: 96,
    height: 8,
    backgroundColor: 'black',
  },
});

export default StoryDetails;

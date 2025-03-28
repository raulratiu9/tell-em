import { Story } from '@/types';
import { View, Text, StyleSheet, Image } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';

interface Props {
  story: Story;
  isPreview?: boolean;
}

const MAX_CONTENT_CHARS_ALLOWED = 75;

export default function StoryCard({ story, isPreview }: Props) {
  const { title, content, image } = story;

  return (
    <View>
      <View style={styles.imageContainer}>
        <Image
          source={{
            uri: isPreview ? image : `${process.env.EXPO_PUBLIC_BASE_API_URL}${image}`,
          }}
          style={styles.image}
        />
        <LinearGradient
          colors={['rgba(255,255,255,0)', 'rgba(255,255,255,0.8)']}
          style={styles.gradientOverlay}
        />
      </View>
      <View style={styles.container}>
        <Text style={styles.title}>{title}</Text>
        <Text style={styles.content}>
          {content.slice(0, MAX_CONTENT_CHARS_ALLOWED)}...
        </Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#fff',
    borderBottomLeftRadius: 8,
    borderBottomRightRadius: 8,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 4,
    },
    shadowOpacity: 0.1,
    shadowRadius: 6,
    elevation: 4,
    marginBottom: 16,
  },
  header: {
    fontSize: 24,
    textAlign: 'center',
    marginBottom: 20,
  },
  story: {
    marginBottom: 15,
    padding: 10,
    borderWidth: 1,
    borderColor: '#ccc',
  },
  title: {
    fontFamily: 'MontserratBold',
    fontSize: 18,
  },
  content: {
    fontFamily: 'MontserratRegular',
    lineHeight: 25,
  },
  image: {
    width: '100%',
    height: 200,
    resizeMode: 'cover',
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
  },
  imageContainer: {
    backgroundColor: '#fff',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 6,
    elevation: 8,
    overflow: 'hidden',
    borderTopRightRadius: 16,
    borderTopLeftRadius: 16,
  },
  gradientOverlay: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    height: 80,
  },
});

import { FlatList, Image, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { Story } from '@/types';
import { router } from 'expo-router';

interface Props {
  stories: Story[];
}

export default function LatestStoriesCarousel({ stories }: Props) {
  if (!stories || stories.length === 0) return null;

  return (
    <View style={styles.wrapper}>
      <FlatList
        data={stories}
        horizontal
        showsHorizontalScrollIndicator={false}
        keyExtractor={(item) => item.storyId.toString()}
        contentContainerStyle={styles.list}
        renderItem={({ item }) => (
          <TouchableOpacity
            onPress={() => router.push(`/story/${item.storyId}`)}
            style={styles.card}
          >
            <Image source={{ uri: item.featureImage }} style={styles.image} />
            <View style={styles.gradient} />
            <Text style={styles.cardTitle}>{item.title}</Text>
          </TouchableOpacity>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    marginTop: 24,
  },
  list: {
    paddingLeft: 16,
  },
  card: {
    width: 150,
    height: 180,
    borderRadius: 16,
    overflow: 'hidden',
    marginRight: 16,
    position: 'relative',
  },
  image: {
    width: '100%',
    height: '100%',
    resizeMode: 'cover',
  },
  gradient: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.35)',
  },
  cardTitle: {
    position: 'absolute',
    bottom: 12,
    left: 12,
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

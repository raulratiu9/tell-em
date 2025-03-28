import { useEffect, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
} from 'react-native';

import { Story } from '@/types';
import StoryCard from '@/components/StoryCard';
import { router } from 'expo-router';
import { getStories } from '@/api/getStories';

export default function StoriesFeed() {
  const [stories, setStories] = useState<Story[]>([]);

  useEffect(() => {
    getStories(setStories);
  }, []);

  return (
    <ScrollView showsVerticalScrollIndicator={false}>
      <View style={styles.container}>
        <Text style={styles.header}>Tell'em you've got new stories</Text>
        <FlatList
          data={stories}
          keyExtractor={(item: Story) => item.id.toString()}
          renderItem={({ item }) => (
            <TouchableOpacity
              key={item.title}
              onPress={() => router.push(`/story/${item.id}`)}
            >
              <StoryCard key={item.title} story={item} />
            </TouchableOpacity>
          )}
        />
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 24,
    justifyContent: 'center',
    fontFamily: 'MontserratBlack',
    overflowX: 'hidden',
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginTop: 60,
    marginBottom: 40,
    textAlign: 'left',
  },
});

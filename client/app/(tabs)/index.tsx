import { useEffect, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Button,
} from 'react-native';

import { Story } from '@/types';
import StoryCard from '@/components/StoryCard';
import { router } from 'expo-router';
import { getStories } from '@/api/getStories';
import { getToken } from '@/utils/authStorage';

export default function StoriesFeed() {
  const [stories, setStories] = useState<Story[]>([]);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Check if the user is authenticated by checking the JWT token
    const checkAuth = async () => {
      const token = await getToken();
      if (token) {
        setIsAuthenticated(true);
        getStories(setStories);
      } else {
        setIsAuthenticated(false);
      }
    };
    checkAuth();
  }, []);

  if (isAuthenticated === false) {
    // If not authenticated, show a message to log in
    return (
      <View style={styles.container}>
        <Text>You must be logged in to see stories.</Text>
        <Button title="Login" onPress={() => router.push('/login')} />
      </View>
    );
  }

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

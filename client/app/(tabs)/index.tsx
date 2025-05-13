import {
  View,
  Text,
  Image,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useEffect, useState } from 'react';
import { router } from 'expo-router';

import { Story } from '@/types';
import StoryCard from '@/components/StoryCard';
import LatestStoriesCarousel from '@/components/LatestStoriesCarousel';
import { getStories } from '@/api/getStories';
import { loadCachedStories, saveCachedStories } from '@/utils/loadCachedStories';
import FontAwesome from '@expo/vector-icons/FontAwesome';
import StoryFeedSkeleton from '@/components/StoryFeedSkeleton';

const PAGE_SIZE = 20;

export const navigationOptions = {
  headerShown: false,
};

export default function HomePage() {
  const [latestStories, setLatestStories] = useState<Story[]>([]);
  const [hydrated, setHydrated] = useState(false);

  useEffect(() => {
    loadCachedStories()
      .then((cached) => {
        setLatestStories(cached.slice(0, 10));
      })
      .finally(() => {
        setHydrated(true);
      });
  }, []);

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isRefetching, refetch } =
    useInfiniteQuery({
      queryKey: ['stories'],
      queryFn: ({ pageParam = 0 }) => getStories(pageParam, PAGE_SIZE),
      getNextPageParam: (lastPage, allPages) =>
        lastPage.length === PAGE_SIZE ? allPages.length : undefined,
      initialPageParam: 0,
      staleTime: 1000 * 60 * 5,
      enabled: hydrated,
    });

  const allStories = data?.pages.flat() ?? [];

  useEffect(() => {
    if (allStories.length > 0) {
      const firstPage = data?.pages[0]?.slice(0, 10) ?? [];
      saveCachedStories(firstPage);
      setLatestStories(firstPage);
    }
  }, [data]);

  return (
    <View style={{ flex: 1 }}>
      <LatestStoriesCarousel stories={latestStories} />
      <View style={styles.ctaContainer}>
        <Text style={styles.ctaTitle}>Tell 'em your story</Text>
        <TouchableOpacity onPress={() => router.push('/add-story')}>
          <Image
            source={require('../../assets/images/add_story_img.png')}
            style={styles.ctaImage}
          />
        </TouchableOpacity>
      </View>
      <FlatList
        contentContainerStyle={styles.container}
        data={allStories.slice(10)}
        keyExtractor={(item: Story) => item.id.toString()}
        renderItem={({ item }) => (
          <TouchableOpacity onPress={() => router.push(`/story/${item.id}`)}>
            <StoryCard story={item} />
          </TouchableOpacity>
        )}
        ListHeaderComponent={<Text style={styles.header}>Explore more stories</Text>}
        ListFooterComponent={isFetchingNextPage ? <StoryFeedSkeleton /> : null}
        onEndReached={() => {
          if (hasNextPage && !isFetchingNextPage) fetchNextPage();
        }}
        onEndReachedThreshold={0.5}
        refreshing={isRefetching}
        onRefresh={refetch}
        ListEmptyComponent={
          !isFetchingNextPage && !isRefetching ? <Text>No stories</Text> : null
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 24,
    paddingBottom: 80,
  },
  header: {
    fontSize: 22,
    fontFamily: 'MontserratBold',
    marginBottom: 12,
  },
  empty: {
    textAlign: 'center',
    marginTop: 60,
    fontSize: 16,
    color: '#999',
  },
  ctaContainer: {
    height: 220,
    marginVertical: 32,
    borderBottomEndRadius: 196,
    borderBottomStartRadius: 196,
    overflow: 'hidden',
    position: 'relative',
    padding: 0,
  },
  ctaTitle: {
    fontSize: 22,
    marginBottom: 12,
    marginHorizontal: 24,
    fontFamily: 'MontserratBold',
  },
  ctaImage: {
    width: '100%',
    height: '100%',
    resizeMode: 'cover',
  },
});

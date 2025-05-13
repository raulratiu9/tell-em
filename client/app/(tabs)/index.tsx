import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import { useInfiniteQuery } from '@tanstack/react-query';
import { router } from 'expo-router';
import { Story } from '@/types';
import StoryCard from '@/components/StoryCard';
import { getStories } from '@/api/getStories';
import { useEffect, useState } from 'react';
import { loadCachedStories, saveCachedStories } from '@/utils/loadCachedStories';

const PAGE_SIZE = 10;

export const navigationOptions = {
  headerShown: false,
};

export default function App() {
  const [hydratedFromCache, setHydratedFromCache] = useState(false);

  useEffect(() => {
    loadCachedStories().then((cached) => {
      if (cached.length > 0) {
        return;
      }
      setHydratedFromCache(true);
    });
  }, []);

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, refetch, isRefetching } =
    useInfiniteQuery({
      queryKey: ['stories'],
      queryFn: ({ pageParam = 0 }) => getStories(pageParam, PAGE_SIZE),
      getNextPageParam: (lastPage, allPages) =>
        lastPage.length === PAGE_SIZE ? allPages.length : undefined,
      initialPageParam: 0,
      staleTime: 1000 * 60 * 5,
      enabled: hydratedFromCache,
    });

  const allStories = data?.pages.flat() ?? [];

  useEffect(() => {
    if (allStories.length > 0) {
      const firstStories = data?.pages[0] ?? [];
      saveCachedStories(firstStories);
    }
  }, [data]);

  return (
    <FlatList
      contentContainerStyle={styles.container}
      data={allStories}
      keyExtractor={(item: Story) => item.id.toString()}
      renderItem={({ item }) => (
        <TouchableOpacity onPress={() => router.push(`/story/${item.id}`)}>
          <StoryCard story={item} />
        </TouchableOpacity>
      )}
      ListHeaderComponent={
        <Text style={styles.header}>Tell'em you've got new stories</Text>
      }
      ListFooterComponent={
        isFetchingNextPage ? <ActivityIndicator size="large" color="#888" /> : null
      }
      onEndReached={() => {
        if (hasNextPage && !isFetchingNextPage) {
          fetchNextPage();
        }
      }}
      onEndReachedThreshold={0.5}
      refreshing={isRefetching}
      onRefresh={refetch}
    />
  );
}

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 24,
    paddingBottom: 80,
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginTop: 60,
    marginBottom: 40,
    textAlign: 'left',
  },
});

import { View, Text, Image, FlatList, StyleSheet, TouchableOpacity } from 'react-native';
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
import useDebounce from '@/hooks/useDebounce';
import { getSearchedStories } from '@/api/getSearchedStories';
import Search from '@/components/Search';

const PAGE_SIZE = 20;

export const navigationOptions = {
  headerShown: false,
};

export default function HomePage() {
  const [latestStories, setLatestStories] = useState<Story[]>([]);
  const [hydrated, setHydrated] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);

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

  const {
    data: searchData,
    fetchNextPage: fetchNextSearchPage,
    hasNextPage: hasMoreSearch,
    isFetchingNextPage: loadingMoreSearch,
    isRefetching: isRefetchingSearch,
    refetch: refetchSearch,
  } = useInfiniteQuery({
    queryKey: ['search', debouncedSearch],
    queryFn: ({ pageParam = 0 }) =>
      getSearchedStories(pageParam, PAGE_SIZE, debouncedSearch),
    getNextPageParam: (lastPage, allPages) =>
      lastPage.length === PAGE_SIZE ? allPages.length : undefined,
    initialPageParam: 0,
    enabled: hydrated && debouncedSearch.length > 0,
  });

  const allStories = data?.pages.flat() ?? [];
  const showingSearchResults = debouncedSearch.length > 0;
  const storiesToRender = showingSearchResults
    ? (searchData?.pages.flat() ?? [])
    : allStories.slice(10);

  useEffect(() => {
    if (allStories.length > 0) {
      const firstPage = data?.pages[0]?.slice(0, 20) ?? [];
      saveCachedStories(firstPage);
      setLatestStories(firstPage);
    }
  }, [data]);

  const getListFooterComponent = () => {
    if (showingSearchResults && loadingMoreSearch) return <StoryFeedSkeleton />;
    if (!showingSearchResults && isFetchingNextPage) return <StoryFeedSkeleton />;
    return null;
  };

  const handleEndReached = () => {
    if (showingSearchResults && hasMoreSearch && !loadingMoreSearch) {
      fetchNextSearchPage();
    } else if (!showingSearchResults && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  };

  const getListEmptyComponent = () => {
    if (showingSearchResults && debouncedSearch.length > 0) {
      return (
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyTitle}>No stories found</Text>
          <Text style={styles.emptySubtitle}>No results for "{debouncedSearch}"</Text>
        </View>
      );
    }

    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyTitle}>No stories available</Text>
        <Text style={styles.emptySubtitle}>Be the first to share your adventure!</Text>
      </View>
    );
  };

  return (
    <View style={{ flex: 1 }}>
      <LatestStoriesCarousel stories={latestStories} />
      <Search value={searchTerm} onChange={setSearchTerm} />
      <Text style={styles.ctaTitle}>Tell 'em your story</Text>
      <View style={styles.ctaContainer}>
        <TouchableOpacity onPress={() => router.push('/add-story')}>
          <Image
            source={require('../../assets/images/add_story_img.png')}
            style={styles.ctaImage}
          />
        </TouchableOpacity>
      </View>
      <FlatList
        contentContainerStyle={styles.container}
        data={storiesToRender}
        keyExtractor={(item: Story) => item?.id?.toString()}
        renderItem={({ item }) => (
          <TouchableOpacity onPress={() => router.push(`/story/${item.id}`)}>
            <StoryCard story={item} />
          </TouchableOpacity>
        )}
        ListHeaderComponent={<Text style={styles.header}>Explore more stories</Text>}
        ListFooterComponent={getListFooterComponent}
        onEndReached={handleEndReached}
        onEndReachedThreshold={0.5}
        refreshing={showingSearchResults ? isRefetchingSearch : isRefetching}
        onRefresh={showingSearchResults ? refetchSearch : refetch}
        ListEmptyComponent={
          !isFetchingNextPage && !isRefetching ? getListEmptyComponent() : null
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
    height: 230,
    overflow: 'hidden',
    position: 'relative',
    padding: 0,
  },
  ctaTitle: {
    fontSize: 22,
    marginTop: 28,
    marginHorizontal: 24,
    fontFamily: 'MontserratBold',
  },
  ctaImage: {
    width: '100%',
    height: '100%',
    resizeMode: 'cover',
    transform: [{ translateY: -50 }],
  },
  emptyContainer: {
    alignItems: 'center',
    marginTop: 80,
    paddingHorizontal: 24,
  },
  emptyTitle: {
    fontSize: 20,
    fontFamily: 'MontserratBold',
    marginBottom: 8,
  },
  emptySubtitle: {
    fontSize: 16,
    fontFamily: 'MontserratRegular',
    color: '#666',
    textAlign: 'center',
  },
});

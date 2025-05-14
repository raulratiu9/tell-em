import { View } from 'react-native';
import SkeletonPlaceholder from 'react-native-skeleton-placeholder';

export default function StoryFeedSkeleton({ count = 3 }: { count?: number }) {
  return (
    <SkeletonPlaceholder>
      {[...Array(count)].map((_, index) => (
        <View key={index} style={{ marginBottom: 24 }}>
          <View style={{ width: '100%', height: 200, borderRadius: 16 }} />
          <View style={{ marginTop: 12, height: 20, width: '70%' }} />
          <View style={{ marginTop: 6, height: 16, width: '90%' }} />
        </View>
      ))}
    </SkeletonPlaceholder>
  );
}

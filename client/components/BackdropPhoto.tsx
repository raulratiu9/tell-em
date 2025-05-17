import Animated, { interpolate, useAnimatedStyle } from 'react-native-reanimated';
import { StyleSheet } from 'react-native';

export default function BackdropPhoto({ photo, index, scrollX }: any) {
  const stylez = useAnimatedStyle(() => {
    return {
      opacity: interpolate(scrollX.value, [index - 1, index, index + 1], [0, 1, 0]),
    };
  });

  return (
    <Animated.Image
      style={[StyleSheet.absoluteFillObject, stylez]}
      source={{ uri: photo }}
      blurRadius={50}
    />
  );
}

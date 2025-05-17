import { MaterialIcons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';
import { Text, TouchableOpacity, View, Dimensions, StyleSheet } from 'react-native';
import Animated, {
  interpolate,
  useAnimatedStyle,
  useSharedValue,
  withRepeat,
  withSequence,
  withTiming,
  Easing,
  Extrapolate,
} from 'react-native-reanimated';

const { width } = Dimensions.get('screen');
const _imageWidth = width * 0.8;
const _imageHeight = _imageWidth * 2.3;
const _spacing = 12;

export default function FramePhoto({
  item,
  index,
  scrollX,
  onHandleDecision,
  allFrames,
}: any) {
  const altScrollX = useSharedValue(0);
  console.log('index', index.toString());
  const isNearCurrent =
    Math.abs(scrollX.value - index * (_imageWidth + _spacing)) < _imageWidth * 1.5;
  const inputRange = [
    (index - 1) * (_imageWidth + _spacing),
    index * (_imageWidth + _spacing),
    (index + 1) * (_imageWidth + _spacing),
  ];

  const stylez = useAnimatedStyle(() => {
    return {
      transform: [
        {
          scale: interpolate(
            scrollX.value,
            [index - 1, index, index + 1],
            [1.4, 1, 1.4],
            Extrapolate.CLAMP,
          ),
        },
        {
          rotate: `${interpolate(scrollX.value, [index - 1, index, index + 1], [15, 0, -15], Extrapolate.CLAMP)}deg`,
        },
      ],
    };
  });

  return (
    <View
      style={{
        overflow: 'visible',
        width: _imageWidth,
        height: _imageHeight,
        borderRadius: 16,
      }}
    >
      {item.choices.length > 1 && (
        <MaterialIcons
          name="signpost"
          size={128}
          color="white"
          style={{ position: 'absolute', zIndex: 1, left: 100, top: 320 }}
        />
      )}
      {item.choices.length === 0 && <Text>The End. Thank you for reading.</Text>}
      {item.choices.length > 1 ? (
        item.choices.map((edge: any) => {
          const rotationAnim = useSharedValue(0);

          const edgeNode = allFrames?.find(
            (frame: any) => frame.frameId === edge.nextFrameId,
          );
          if (!edgeNode) return null;
          rotationAnim.value = withRepeat(
            withSequence(
              withTiming(-10, {
                duration: 2000,
                easing: Easing.inOut(Easing.ease),
              }),
              withTiming(10, {
                duration: 2000,
                easing: Easing.inOut(Easing.ease),
              }),
            ),
            -1,
            true,
          );
          const animatedStyle = useAnimatedStyle(() => ({
            transform: [{ rotate: `${rotationAnim.value}deg` }],
          }));
          return (
            <Animated.View key={edge.nextFrameId} style={[stylez]}>
              <TouchableOpacity onPress={() => onHandleDecision(edge.nextFrameId)}>
                <Animated.Image
                  source={{ uri: edgeNode.image }}
                  style={[
                    {
                      overflow: 'visible',
                      width: _imageWidth,
                      height: _imageHeight / 2,
                      borderRadius: 16,
                      overlayColor: 'rgba(0,0,0,0.9)',
                      opacity: 0.3,
                      transform: [{ rotate: '40deg' }],
                    },
                    animatedStyle,
                  ]}
                />
                <Text
                  style={{
                    position: 'absolute',
                    bottom: 150,
                    left: 80,
                    color: 'white',
                    fontFamily: 'MontserratBold',
                    fontSize: 24,
                    fontWeight: 'bold',
                    textTransform: 'uppercase',
                  }}
                >
                  {edge.label}
                </Text>
              </TouchableOpacity>
            </Animated.View>
          );
        })
      ) : (
        <Animated.View
          style={[
            {
              overflow: 'hidden',
              width: _imageWidth,
              height: _imageHeight,
              borderRadius: 16,
            },
            stylez,
          ]}
        >
          <Animated.Image
            source={{ uri: item.image }}
            style={[{ flex: 1, zIndex: -1 }]}
          />
          <LinearGradient
            colors={['rgba(241, 241, 241, 0.329)', 'rgba(37,37,37,0.2)']}
            style={styles.gradientOverlay}
          />
          <View style={styles.contentBox}>
            <Text style={styles.title}>{item?.title}</Text>
            <Text style={styles.description}>{item?.content}</Text>
          </View>
        </Animated.View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  title: {
    color: 'white',
    fontSize: 18,
    fontWeight: '600',
    letterSpacing: 1.5,
  },
  description: {
    color: 'white',
    fontSize: 14,
    lineHeight: 26,
    fontWeight: '400',
    letterSpacing: 1.3,
    marginTop: 0,
    paddingTop: 0,
    width: _imageWidth - 45,
  },
  contentBox: {
    gap: 0,
    position: 'absolute',
    bottom: 0,
    left: 0,
    padding: 30,
    backgroundColor: 'rgba(37,37,37,0.5)',
  },
  gradientOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: 0,
  },
});

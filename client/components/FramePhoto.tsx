import { MaterialIcons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';
import {
  Text,
  TouchableOpacity,
  View,
  Dimensions,
  StyleSheet,
  Image,
} from 'react-native';
import Animated, {
  interpolate,
  useAnimatedStyle,
  Extrapolate,
  withRepeat,
  withSequence,
  withTiming,
  useSharedValue,
  Easing,
} from 'react-native-reanimated';
import { Button } from 'react-native-paper';

const { width } = Dimensions.get('screen');
const _imageWidth = width * 0.8;
const _imageHeight = _imageWidth * 2.3;

export default function FramePhoto({
  item,
  index,
  scrollX,
  onHandleDecision,
  allFrames,
  onRestartStory,
}: any) {
  const stylez = useAnimatedStyle(() => {
    const scale = interpolate(
      scrollX.value,
      [index - 1, index, index + 1],
      [0.95, 1, 0.95],
      Extrapolate.CLAMP,
    );
    const rotate = interpolate(
      scrollX.value,
      [index - 1, index, index + 1],
      [-5, 0, 5],
      Extrapolate.CLAMP,
    );

    return {
      transform: [{ scale }, { rotate: `${rotate}deg` }],
    };
  });

  const renderChoices = () => {
    if (!item?.nextFrames) return;
    return item.nextFrames.map((edge: any, i: number) => {
      const edgeNode = allFrames?.find(
        (frame: any) =>
          frame.frameId === edge.frameId ||
          frame.frameId === edge.nextFrames?.[0]?.frameId,
      );
      const rotationAnim = useSharedValue(0);

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
        <TouchableOpacity
          key={edge.frameId + i}
          onPress={() => onHandleDecision(edgeNode.frameId)}
          style={{ marginBottom: 20 }}
        >
          <Animated.Image
            source={{ uri: edgeNode.image }}
            style={[styles.choiceImage, animatedStyle]}
          />
          <Text style={styles.choiceLabel}>{edge.content || edgeNode.content}</Text>
        </TouchableOpacity>
      );
    });
  };

  const renderSingle = () => (
    <Animated.View style={[styles.cardContainer, stylez]}>
      <Animated.Image source={{ uri: item.image }} style={[styles.fullImage]} />
      <LinearGradient
        colors={['rgba(241, 241, 241, 0.329)', 'rgba(37,37,37,0.4)']}
        style={styles.gradientOverlay}
      />
      <View style={styles.contentBox}>
        <Text style={styles.title}>{item?.title}</Text>
        <Text style={styles.description}>{item?.content}</Text>
      </View>
    </Animated.View>
  );

  const renderEnd = () => (
    <View
      style={[
        styles.cardContainer,
        { justifyContent: 'center', alignItems: 'center', backgroundColor: '#0007' },
      ]}
    >
      <Text style={styles.endText}>The End</Text>
      <Button mode="contained" onPress={onRestartStory} icon="restart">
        <Text style={{ color: 'white' }}>Start Over</Text>
      </Button>
    </View>
  );

  const isBranching = item.nextFrames.length > 1;
  const isEnd = item.nextFrames.length === 0;

  return (
    <View style={styles.outerCard}>
      {isBranching && (
        <MaterialIcons
          name="signpost"
          size={128}
          color="white"
          style={styles.branchIcon}
        />
      )}
      {isEnd ? renderEnd() : isBranching ? renderChoices() : renderSingle()}
    </View>
  );
}

const styles = StyleSheet.create({
  outerCard: {
    overflow: 'visible',
    width: _imageWidth,
    height: _imageHeight,
    borderRadius: 16,
    top: '7%',
  },
  cardContainer: {
    overflow: 'hidden',
    width: _imageWidth,
    height: _imageHeight,
    borderRadius: 16,
  },
  fullImage: {
    flex: 1,
    zIndex: -1,
  },
  gradientOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: '100%',
  },
  contentBox: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    padding: 30,
    backgroundColor: 'rgba(37,37,37,0.5)',
    width: '100%',
  },
  title: {
    color: 'white',
    fontSize: 18,
    fontWeight: '600',
    letterSpacing: 1.5,
  },
  description: {
    color: 'white',
    fontSize: 14,
    lineHeight: 24,
    fontWeight: '400',
    letterSpacing: 1.3,
    marginTop: 4,
  },
  choiceImage: {
    width: _imageWidth,
    height: _imageHeight / 2,
    opacity: 0.9,
    top: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.5)', // simulare de blur
    borderRadius: 16,
    alignItems: 'center',
    justifyContent: 'center',
  },
  choiceLabel: {
    position: 'absolute',
    bottom: '35%',
    textAlignVertical: 'center',
    left: 15,
    color: 'white',
    fontSize: 20,
    fontWeight: 'bold',
    textTransform: 'uppercase',
    textAlign: 'center',
    textShadowColor: 'rgba(0,0,0,0.6)',
    textShadowOffset: { width: 1, height: 1 },
    textShadowRadius: 2,
  },
  branchIcon: {
    position: 'absolute',
    zIndex: 1,
    left: (_imageWidth - 128) / 2,
    top: _imageHeight / 2 - 64,
  },
  endText: {
    color: 'white',
    fontSize: 68,
    fontWeight: 'bold',
  },
});

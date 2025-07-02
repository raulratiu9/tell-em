import {
  View,
  StyleSheet,
  Dimensions,
  Text,
  Button,
  TouchableOpacity,
} from "react-native";

import Animated, {
  Easing,
  interpolate,
  useAnimatedScrollHandler,
  useAnimatedStyle,
  useSharedValue,
  withRepeat,
  withSequence,
  withTiming,
} from "react-native-reanimated";
import { useEffect, useState } from "react";
import { mockData } from "./mockData";
import { LinearGradient } from "expo-linear-gradient";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import React from "react";

const { width } = Dimensions.get("screen");
const _imageWidth = width * 0.8;
const _imageHeight = _imageWidth * 2.3;
const _spacing = 12;

export default function ViewStory() {
  const [img, setImg] = useState(mockData.nodes.map((node) => node.image_id));
  const flatListRef = React.useRef<Animated.FlatList<any>>(null);
  const firstActiveNodes =
    mockData.nodes[0].edges.length < 2
      ? [mockData.nodes[0], mockData.nodes[1]]
      : [mockData.nodes[0]];
  const [activeNodes, setActiveNodes] = useState(firstActiveNodes);
  const [historyNodes, setHistoryNodes] = useState<string[]>([]);

  const onHandleDecision = (node: any) => {
    const selectedNode = mockData.nodes.find((item) => item.id === node);
    if (selectedNode) {
      setImg((prev) => [...prev, selectedNode.image_id]);
      setActiveNodes((prev) => [
        ...(Array.isArray(prev) ? prev : [prev]),
        selectedNode,
        ...selectedNode.edges
          .map((edge) => mockData.nodes.find((n) => n.id === edge.to))
          .filter(
            (node): node is NonNullable<typeof node> => node !== undefined
          ),
      ]);
    }
  };
  const windowHeight = Dimensions.get("window").height;
  const getItemLayout = (data, index) => ({
    length: windowHeight, // Înălțimea fiecărui element
    offset: windowHeight * index, // Calculate the offset based on the index
    index,
  });

  const goBack = () => {
    if (historyNodes.length > 0) {
      const previousNodeId = historyNodes[historyNodes.length - 1]; // Ultimul nod vizitat
      const previousNode = mockData.nodes.find(
        (node) => node.id === Number(previousNodeId)
      );
      if (previousNode) {
        setHistoryNodes((prev) => prev.slice(0, -1)); // Elimină ultimul nod din istoric
        setActiveNodes([
          previousNode,
          ...previousNode.edges
            .map((edge) => mockData.nodes.find((n) => n.id === edge.to))
            .filter(
              (node): node is NonNullable<typeof node> => node !== undefined
            ),
        ]);
      }
    }
  };

  const scrollX = useSharedValue(0);
  const onScroll = useAnimatedScrollHandler((e) => {
    scrollX.value = e.contentOffset.x / (_imageWidth + _spacing);
  });

  return (
    <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
      <View style={StyleSheet.absoluteFillObject}>
        {img.map((photo, index) => (
          <BackdropPhoto photo={photo} index={index} scrollX={scrollX} />
        ))}
      </View>
      <Animated.FlatList
        ref={flatListRef}
        data={Array.isArray(activeNodes) ? activeNodes : [activeNodes]}
        keyExtractor={(item: any) => item?.id?.toString()}
        horizontal
        style={{ flexGrow: 0, marginBottom: 50 }}
        snapToInterval={_imageWidth + _spacing}
        decelerationRate={"fast"}
        contentContainerStyle={{
          gap: _spacing,
          paddingHorizontal: (width - _imageWidth) / 2,
        }}
        renderItem={({ item, index }) => (
          <Photo
            item={item}
            index={index}
            scrollX={scrollX}
            onHandleDecision={onHandleDecision}
          />
        )}
        onScroll={onScroll}
        showsHorizontalScrollIndicator={false}
        scrollEventThrottle={1000 / 60}
        getItemLayout={getItemLayout}
      />
      {historyNodes.length > 0 && (
        <Button onPress={goBack} title="⬅ Go Back" color="white" />
      )}
    </View>
  );
}

function Photo({ item, index, scrollX, onHandleDecision }: any) {
  console.log(item, "item");
  const stylez = useAnimatedStyle(() => {
    return {
      transform: [
        {
          scale: interpolate(
            scrollX.value,
            [index - 1, index, index + 1],
            [1.6, 1, 1.6]
          ),
        },
        {
          rotate: `${interpolate(
            scrollX.value,
            [index - 1, index, index + 1],
            [15, 0, -15]
          )}deg`,
        },
      ],
    };
  });

  return (
    <View
      style={{
        overflow: "visible",
        width: _imageWidth,
        height: _imageHeight,
        borderRadius: 16,
      }}
    >
      {item.edges.length > 1 && (
        <>
          <MaterialIcons
            name="signpost"
            size={128}
            color="white"
            style={{
              position: "absolute",
              overflow: "visible",
              zIndex: 1,
              left: 100,
              top: 320,
            }}
          />
        </>
      )}
      {item.edges.length > 1 ? (
        item.edges.map((edge: any) => {
          const rotationAnim = useSharedValue(0);
          const edgeNode = mockData.nodes.find(
            (item: any) => item.id === edge.to
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
              })
            ),
            -1,
            true
          );

          const animatedStyle = useAnimatedStyle(() => ({
            transform: [{ rotate: `${rotationAnim.value}deg` }],
          }));

          return (
            <View key={edge.to}>
              <TouchableOpacity onPress={() => onHandleDecision(edge.to)}>
                <Animated.Image
                  source={{ uri: edgeNode.image_id }}
                  style={[
                    {
                      overflow: "visible",
                      width: _imageWidth,
                      height: _imageHeight / 2,
                      borderRadius: 16,
                      overlayColor: "rgba(0,0,0,0.9)",
                      opacity: 0.3,
                      transform: [{ rotate: "40deg" }],
                    },
                    animatedStyle,
                  ]}
                />
                <Text
                  style={{
                    position: "absolute",
                    bottom: 150,
                    left: 80,
                    color: "white",
                    textTransform: "uppercase",
                    fontFamily: "MontserratBold",
                    fontSize: 24,
                    fontWeight: "bold",
                    paddingHorizontal: 10,
                    paddingVertical: 5,
                    borderRadius: 8,
                  }}
                >
                  {edge.label}
                </Text>
              </TouchableOpacity>
            </View>
          );
        })
      ) : (
        <View
          style={{
            overflow: "hidden",
            width: _imageWidth,
            height: _imageHeight,
            borderRadius: 16,
          }}
        >
          <Animated.Image
            style={[{ flex: 1, zIndex: -1 }, stylez]}
            source={{ uri: item.image_id }}
          />
          <LinearGradient
            colors={["rgba(241, 241, 241, 0.329)", "rgba(37,37,37,0.2)"]}
            style={styles.gradientOverlay}
          />
          <View
            style={{
              gap: 0,
              position: "absolute",
              bottom: 0,
              left: 0,
              padding: 30,
              paddingTop: -10,
              height: 600,
              backgroundColor: "rgba(37,37,37,0.5)",
            }}
          >
            <Text style={styles.title}>{item?.title}</Text>
            <Text style={styles.description}>{item?.content}</Text>
          </View>
        </View>
      )}
    </View>
  );
}

function BackdropPhoto({ photo, index, scrollX }: any) {
  const stylez = useAnimatedStyle(() => {
    return {
      opacity: interpolate(
        scrollX.value,
        [index - 1, index, index + 1],
        [0, 1, 0]
      ),
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

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  header: {
    fontSize: 24,
    fontWeight: "bold",
    marginTop: 60,
    marginBottom: 40,
    textAlign: "left",
  },
  title: {
    color: "white",
    fontSize: 18,
    fontWeight: "600",
    letterSpacing: 1.5,
  },
  description: {
    color: "white",
    fontSize: 14,
    lineHeight: 26,
    fontWeight: "400",
    letterSpacing: 1.3,
    marginTop: 0,
    paddingTop: 0,
    width: _imageWidth - 45,
  },
  blurOverlay: {
    position: "absolute",
    bottom: 0,
    left: 0,
    width: "100%",
    padding: 20,
    borderBottomLeftRadius: 16,
    borderBottomRightRadius: 16,
  },
  gradientOverlay: {
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    height: 0,
  },
});

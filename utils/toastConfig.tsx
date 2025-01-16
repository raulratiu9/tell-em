import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { BaseToastProps } from "react-native-toast-message";

interface CustomToastProps extends BaseToastProps {
  text1?: string;
  text2?: string;
}

const toastConfig = {
  error: ({ text1, text2 }: CustomToastProps) => (
    <View
      style={{
        ...styles.container,
        borderColor: "#D92D20",
      }}
    >
      {text1 && (
        <Text
          style={{
            ...styles.text,
            color: "#D92D20",
          }}
        >
          {text1}
        </Text>
      )}
      {text2 && <Text style={styles.text2}>{text2}</Text>}
    </View>
  ),
  success: ({ text1, text2 }: CustomToastProps) => (
    <View
      style={{
        ...styles.container,
        borderColor: "#ABEFC6",
      }}
    >
      {text1 && (
        <Text
          style={{
            ...styles.text,
            color: "#067647",
          }}
        >
          {text1}
        </Text>
      )}
      {text2 && <Text style={styles.text2}>{text2}</Text>}
    </View>
  ),
};

export default toastConfig;

const styles = StyleSheet.create({
  container: {
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "flex-start",
    width: "90%",
    borderWidth: 1,
    borderColor: "#D92D20",
    backgroundColor: "#FEF3F2",
    padding: 12,
    borderRadius: 8,
  },
  text: {
    color: "#D92D20",
    fontSize: 16,
    fontWeight: "600",
    fontFamily: "MontserratBold",
  },
  text2: {
    color: "black",
  },
});

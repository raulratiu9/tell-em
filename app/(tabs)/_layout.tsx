import FontAwesome from "@expo/vector-icons/FontAwesome";
import { router, Tabs, useNavigation } from "expo-router";

import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { useClientOnlyValue } from "@/components/useClientOnlyValue";
import { ComponentProps } from "react";
import FontAwesome5 from "@expo/vector-icons/FontAwesome5";
import { Text, TouchableOpacity, View } from "react-native";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";

function TabBarIcon(props: {
  name: ComponentProps<typeof FontAwesome>["name"];
  color: string;
}) {
  return <FontAwesome size={28} style={{ marginBottom: -3 }} {...props} />;
}

export default function TabLayout() {
  const colorScheme = useColorScheme();
  const navigation = useNavigation();

  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: Colors[colorScheme ?? "light"].tint,
        headerShown: useClientOnlyValue(false, true),
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: "Stories",
          headerShown: false,
          tabBarIcon: ({ color }) => (
            <FontAwesome5
              name="pen-nib"
              size={28}
              style={{ marginBottom: -3 }}
              color={color}
            />
          ),
        }}
      />
      <Tabs.Screen
        name="add-story"
        options={{
          title: "Add story",
          tabBarIcon: ({ color }) => (
            <TabBarIcon name="plus-circle" color={color} />
          ),
          headerLeft: () => (
            <View>
              <TouchableOpacity
                onPress={() => router.push("/")}
                style={{ flexDirection: "row", alignItems: "center" }}
              >
                <MaterialIcons name="arrow-back-ios" size={24} color="black" />
                <Text style={{ fontFamily: "", color: "black" }}>Back</Text>
              </TouchableOpacity>
            </View>
          ),
        }}
      />
      <Tabs.Screen
        name="profile"
        options={{
          title: "Profile",
          tabBarIcon: ({ color }) => <TabBarIcon name="car" color={color} />,
        }}
      />
      <Tabs.Screen
        name="history"
        options={{
          title: "History",
          tabBarIcon: ({ color }) => <TabBarIcon name="car" color={color} />,
        }}
      />
    </Tabs>
  );
}

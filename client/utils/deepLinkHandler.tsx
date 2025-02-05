// DeepLinkHandler.js
import React, { useEffect } from 'react';
import { Linking, View, Text } from 'react-native';
import { useNavigation } from '@react-navigation/native';

const DeepLinkHandler = () => {
  const navigation = useNavigation();

  useEffect(() => {
    const handleDeepLink = (event) => {
      const { url } = event;
      if (url && url.startsWith('myapp://stories')) {
        // Navigate to the Stories screen when a deep link is received
        navigation.reset('Stories');
      }
    };

    // Add event listener for deep links
    const subscription = Linking.addEventListener('url', handleDeepLink);

    // Check if app was launched by a deep link
    (async () => {
      const initialUrl = await Linking.getInitialURL();
      if (initialUrl && initialUrl.startsWith('myapp://stories')) {
        navigation.replace('Stories');
      }
    })();

    // Clean up the listener on unmount
    return () => {
      subscription.remove();
    };
  }, [navigation]);

  // Optionally, render a loading state while waiting for deep link events.
  return (
    <View>
      <Text>Loading...</Text>
    </View>
  );
};

export default DeepLinkHandler;

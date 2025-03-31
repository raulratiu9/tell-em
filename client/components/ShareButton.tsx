import Feather from '@expo/vector-icons/Feather';
import { TouchableOpacity, StyleSheet, Share } from 'react-native';
import Toast from 'react-native-toast-message';

interface Props {
  storyId: number;
}

export default function ShareButton({ storyId }: Props) {
  const onShare = async () => {
    try {
      const result = await Share.share({
        message: `Tell 'em after reading this amazing story! 🌟 ${process.env.EXPO_PUBLIC_BASE_API_URL}/story/${storyId}`,
      });

      if (result.action === Share.sharedAction) {
        if (result.activityType) {
          Toast.show({
            type: 'success',
            text1: 'Shared with activity type: ',
            text2: result.activityType,
          });
        } else {
          Toast.show({
            type: 'success',
            text1: 'Shared successfully!',
          });
        }
      } else if (result.action === Share.dismissedAction) {
        Toast.show({
          type: 'error',
          text1: 'Share dismissed!',
        });
      }
    } catch (error) {
      Toast.show({
        type: 'error',
        text1: 'Something went wrong while sharing!',
        text2: (error as Error).message,
      });
    }
  };

  return (
    <TouchableOpacity style={styles.button} onPress={onShare}>
      <Feather name="send" size={24} color="white" />
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  button: {
    backgroundColor: '#333',
    width: 40,
    marginTop: 10,
    marginLeft: 10,
    paddingVertical: 10,
    borderRadius: 8,
    alignItems: 'center',
    height: 42,
  },
});

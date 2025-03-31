import FontAwesome5 from '@expo/vector-icons/FontAwesome5';
import { useStripe } from '@stripe/stripe-react-native';
import axios from 'axios';
import { LinearGradient } from 'expo-linear-gradient';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import Toast from 'react-native-toast-message';

interface Props {
  storyId: number;
}

export default function DonationButton({ storyId }: Props) {
  const { initPaymentSheet, presentPaymentSheet } = useStripe();

  const amount = 5000;
  const baseUrl = process.env.EXPO_PUBLIC_BASE_API_URL;

  const createPaymentIntent = async (storyId: number) => {
    try {
      const response = await axios.post(
        `${baseUrl}api/donations`,
        {
          amount,
          currency: 'ron',
          storyId,
          donorName: 'John Doe',
          donorEmail: 'john.doe@example.com',
        },
        {
          headers: {
            Authorization: `Bearer ${process.env.EXPO_PUBLIC_STRIPE_SECRET_KEY}`,
          },
        },
      );

      const { clientSecret } = response.data;

      const { error: sheetError } = await initPaymentSheet({
        paymentIntentClientSecret: clientSecret,
        merchantDisplayName: 'Visa',
      });

      if (sheetError) {
        Toast.show({
          type: 'error',
          text1: 'Failed to initialize payment sheet',
          text2: sheetError.message,
        });
      }

      const { error: paymentError } = await presentPaymentSheet();
      if (paymentError) {
        Toast.show({
          type: 'error',
          text1: 'Payment failed',
          text2: paymentError.message,
        });
      } else {
        Toast.show({
          type: 'success',
          text1: 'Payment successful',
        });
      }
    } catch (error) {
      Toast.show({
        type: 'error',
        text1: 'Internal Server Error',
        text2: error?.toString(),
      });
    }
  };

  return (
    <View>
      <TouchableOpacity onPress={() => createPaymentIntent(storyId)}>
        <LinearGradient colors={['#FF9A8B', '#FF6A88']} style={styles.gradient}>
          <FontAwesome5 name="hand-holding-heart" size={24} color="white" />
          <Text style={styles.text}>Donate</Text>
        </LinearGradient>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  gradient: {
    paddingHorizontal: 24,
    width: 148,
    height: 42,
    alignItems: 'center',
    justifyContent: 'space-evenly',
    flexDirection: 'row',
    borderRadius: 8,
    marginTop: 10,
  },
  text: {
    color: 'white',
    fontSize: 16,
    fontFamily: 'MontserratBold',
  },
});

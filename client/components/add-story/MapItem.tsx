import { Frame } from '@/types';
import { Image, Pressable, StyleSheet, Text } from 'react-native';

interface Props {
  frame: Frame;
  onPress: () => void;
  isItemSelected: boolean;
}

export default function MapItem({ frame, onPress, isItemSelected }: Props) {
  return (
    <Pressable
      onPress={onPress}
      style={[styles.circle, isItemSelected && styles.selectedCircle]}
    >
      <Image
        source={{
          uri: frame.image,
        }}
        style={styles.image}
      />
      <Text style={{ position: 'absolute', color: 'white', fontSize: 48 }}>
        {frame.id}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 24,
    justifyContent: 'center',
    fontFamily: 'MontserratBlack',
  },
  circle: {
    width: 50,
    height: 80,
    borderRadius: 80,
    backgroundColor: '#f4f4f4',
    justifyContent: 'center',
    alignItems: 'center',
    marginHorizontal: 8,
    borderWidth: 2,
    borderColor: 'transparent',
  },
  selectedCircle: {
    borderColor: '#333',
    borderWidth: 5,
    textColor: '#333',
    backgroundColor: '#E3F2FD',
  },
  image: { width: 80, height: 80, borderRadius: 80 },
});

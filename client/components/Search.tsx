import { View, TextInput, StyleSheet } from 'react-native';
import { FontAwesome } from '@expo/vector-icons';

interface Props {
  value: string;
  onChange: (text: string) => void;
  placeholder?: string;
}

export default function Search({
  value,
  onChange,
  placeholder = 'Search stories...',
}: Props) {
  return (
    <View style={styles.container}>
      <FontAwesome name="search" size={18} color="#888" style={styles.icon} />
      <TextInput
        value={value}
        onChangeText={onChange}
        placeholder={placeholder}
        placeholderTextColor="#aaa"
        style={styles.input}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 24,
    marginHorizontal: 12,
    backgroundColor: '#f6f6f6',
    borderRadius: 12,
    paddingHorizontal: 12,
    paddingVertical: 14,
  },
  icon: {
    marginRight: 8,
  },
  input: {
    flex: 1,
    fontSize: 16,
    fontFamily: 'Montserrat-Regular',
    color: '#333',
  },
});

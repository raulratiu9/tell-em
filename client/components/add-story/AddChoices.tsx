// AddChoiceComponent.tsx
import React, { useState } from 'react';
import { View, TextInput, Button, StyleSheet } from 'react-native';

interface Props {
  onSaveChoice: (
    decision1: string,
    decision2: string,
    file1: string,
    file2: string,
  ) => void;
}

export default function AddChoiceComponent({ onSaveChoice }: Props) {
  const [decision1, setDecision1] = useState('');
  const [decision2, setDecision2] = useState('');
  const [file1, setFile1] = useState('');
  const [file2, setFile2] = useState('');

  const handleSave = () => {
    // Call the onSaveChoice function passed as a prop
    onSaveChoice(decision1, decision2, file1, file2);
  };

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.input}
        placeholder="Decision 1"
        value={decision1}
        onChangeText={setDecision1}
      />
      <TextInput
        style={styles.input}
        placeholder="Decision 2"
        value={decision2}
        onChangeText={setDecision2}
      />
      <Button title="Upload File 1" onPress={() => {}} />
      <Button title="Upload File 2" onPress={() => {}} />
      <Button title="Save Choice" onPress={handleSave} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 20,
  },
  input: {
    height: 40,
    borderColor: 'gray',
    borderWidth: 1,
    marginBottom: 10,
    paddingLeft: 8,
  },
});

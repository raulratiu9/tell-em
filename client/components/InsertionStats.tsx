import { InsertionStatistics } from '@/types';
import { ScrollView, View, Text } from 'react-native';
import { Card, Divider } from 'react-native-paper';

const formatMemory = (bytes: number) =>
  bytes > 0 ? `${(bytes / (1024 * 1024)).toFixed(2)} MB` : 'N/A';

const formatTime = (ms: number) => `${(ms / 1000).toFixed(2)} sec`;
const formatAvg = (ms: number) => `${ms.toFixed(2)} ms/item`;

export default function InsertionStats({ data }: { data: InsertionStatistics }) {
  return (
    <ScrollView scrollToOverflowEnabled contentContainerStyle={{ padding: 16 }}>
      {Object.entries(data).map(([key, item]) => (
        <Card key={key} style={{ marginBottom: 16 }}>
          <Card.Title title={key.replace('Response', '').toUpperCase()} />
          <Divider />
          <Card.Content>
            <View style={{ marginVertical: 8 }}>
              <Text>📥 Inserted Items: {item.insertedItems}</Text>
              <Text>⏱ Total Time: {formatTime(item.totalTimeMillis)}</Text>
              <Text>⚙️ Avg Time/Item: {formatAvg(item.averageTimePerItem)}</Text>
              <Text>🧠 Memory Used: {formatMemory(item.memoryUsedBytes)}</Text>
            </View>
          </Card.Content>
        </Card>
      ))}
    </ScrollView>
  );
}

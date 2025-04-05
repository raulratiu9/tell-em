import { View, StyleSheet } from 'react-native';
import { ReactNode } from 'react';

export default function DotBackground({ children }: { children: ReactNode }) {
  const rows = 60;
  const columns = 20;

  return (
    <View style={styles.container}>
      <View style={styles.backgroundContainer}>
        {Array.from({ length: rows }).map((_, rowIndex) => (
          <View key={rowIndex} style={styles.row}>
            {Array.from({ length: columns }).map((_, colIndex) => (
              <View key={colIndex} style={styles.dot} />
            ))}
          </View>
        ))}
      </View>
      <View style={styles.contentContainer}>{children}</View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    position: 'relative',
  },
  backgroundContainer: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    zIndex: -1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  row: {
    flexDirection: 'row',
  },
  dot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: 'rgba(0, 0, 0, 0.1)',
    margin: 10,
  },
  contentContainer: {
    flex: 1,
  },
});

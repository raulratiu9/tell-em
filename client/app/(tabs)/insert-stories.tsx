import { useRef, useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { Button, Text, ActivityIndicator, Snackbar, Menu } from 'react-native-paper';
import { Formik } from 'formik';
import { insertLinearStories } from '@/api/insertLinearStories';
import { insertBranchingStories } from '@/api/insertBranchingStories';
import { Story } from '@/types';
import InsertionStats from '@/components/InsertionStats';
import axios from 'axios';

const Dropdown = ({ label, value, setValue, options }: any) => {
  const [visible, setVisible] = useState(false);

  return (
    <View style={{ marginBottom: 16 }}>
      <Text style={styles.label}>{label}</Text>
      <Menu
        visible={visible}
        onDismiss={() => setVisible(false)}
        anchor={
          <Button mode="outlined" onPress={() => setVisible(true)}>
            {value || 'Select...'}
          </Button>
        }
      >
        {options.map((option: string) => (
          <Menu.Item
            key={option}
            onPress={() => {
              setValue(option);
              setVisible(false);
            }}
            title={option}
          />
        ))}
      </Menu>
    </View>
  );
};

export default function InsertStories() {
  const [loading, setLoading] = useState(false);
  const [snackbar, setSnackbar] = useState({ visible: false, message: '', color: '' });
  const [data, setData] = useState<Story[] | null>(null);
  const [loadingMessage, setLoadingMessage] = useState('');
  const [elapsedTime, setElapsedTime] = useState(0);
  const timerRef = useRef(null as NodeJS.Timeout | null);
  const cancelSourceRef = useRef<ReturnType<typeof axios.CancelToken.source> | null>(
    null,
  );

  const generateLoadingMessage = (
    type: string,
    storyCount: number,
    nodeCount: number,
    depth?: number,
  ) => {
    if (type === 'linear') {
      return `Inserting ${storyCount} linear stories with ${nodeCount} nodes/story...`;
    } else {
      return `Inserting ${storyCount} branching stories with ${nodeCount} nodes/story and depth ${depth}...`;
    }
  };

  const handleAbort = () => {
    cancelSourceRef.current?.cancel('User cancelled the request.');
  };

  return (
    <View style={styles.container}>
      <Text
        style={{
          marginBottom: 20,
          fontSize: 22,
          fontFamily: 'MontserratBold',
        }}
      >
        🚀 Performance Benchmarking
      </Text>

      <Formik
        initialValues={{
          type: 'linear',
          storyCount: '1000',
          nodeCount: '25',
          depth: '4',
        }}
        onSubmit={async (values: any) => {
          setLoading(true);
          const msg = generateLoadingMessage(
            values.type,
            Number(values.storyCount),
            Number(values.nodeCount),
            Number(values.depth),
          );
          setLoadingMessage(msg);
          setSnackbar({ visible: false, message: '', color: '' });
          let response;

          timerRef.current = setInterval(() => {
            setElapsedTime((prev) => +(prev + 0.1).toFixed(1));
          }, 100);

          cancelSourceRef.current = axios.CancelToken.source();

          try {
            if (values.type === 'linear') {
              response = await insertLinearStories(
                Number(values.storyCount),
                Number(values.nodeCount),
                cancelSourceRef.current.token,
              );
            } else {
              response = await insertBranchingStories(
                Number(values.storyCount),
                Number(values.nodeCount),
                Number(values.depth),
                cancelSourceRef.current.token,
              );
            }

            if (!response || response.length === 0) {
              setSnackbar({
                visible: true,
                message: '❌ The insertion has been aborted.',
                color: '#4e080c',
              });
            } else {
              setSnackbar({
                visible: true,
                message: '✅ Stories inserted successfully!',
                color: 'green',
              });
            }
          } catch (err) {
            console.error(err);
            setSnackbar({
              visible: true,
              message: '❌ Something went wrong.',
              color: 'red',
            });
          } finally {
            if (timerRef.current) {
              clearInterval(timerRef.current);
            }
            setElapsedTime(0);
            setData(response as Story[]);
            setLoading(false);
          }
        }}
      >
        {({ values, setFieldValue, handleSubmit }: any) => (
          <View style={{ flex: 1 }}>
            <Dropdown
              label="Story Type"
              value={values.type}
              setValue={(val: string) => setFieldValue('type', val)}
              options={['linear', 'branching']}
            />

            <Dropdown
              label="Number of Stories"
              value={values.storyCount}
              setValue={(val: string) => setFieldValue('storyCount', val)}
              options={['10', '100', '1000', '5000', '10000']}
            />

            <Dropdown
              label="Number of Nodes/story"
              value={values.nodeCount}
              setValue={(val: string) => setFieldValue('nodeCount', val)}
              options={['10', '25', '100', '500', '1000']}
            />

            {values.type === 'branching' && (
              <Dropdown
                label="Story Depth"
                value={values.depth}
                setValue={(val: string) => setFieldValue('depth', val)}
                options={['4', '8']}
              />
            )}

            <View style={{ marginTop: 20, flex: 1 }}>
              <Button
                mode="contained"
                onPress={handleSubmit}
                disabled={loading}
                icon="database-plus"
              >
                <Text style={{ color: 'white' }}>Insert Stories</Text>
              </Button>

              {loading && (
                <>
                  <ActivityIndicator size="large" style={{ marginTop: 20 }} animating>
                    {elapsedTime.toFixed(1)}s
                  </ActivityIndicator>
                  <Text
                    style={{
                      marginTop: 10,
                      fontStyle: 'italic',
                      textAlign: 'center',
                      color: '#666',
                    }}
                  >
                    {loadingMessage}
                  </Text>
                  <Text style={{ marginTop: 4, color: '#993be6', textAlign: 'center' }}>
                    Elapsed time: {elapsedTime.toFixed(1)}s
                  </Text>
                  <Button
                    mode="contained"
                    onPress={handleAbort}
                    icon="cancel"
                    buttonColor="#4e080c"
                  >
                    <Text style={{ color: 'white' }}>Abort</Text>
                  </Button>
                </>
              )}
              {!loading && data && <InsertionStats data={data} />}
            </View>
          </View>
        )}
      </Formik>

      <Snackbar
        visible={snackbar.visible}
        onDismiss={() => setSnackbar({ ...snackbar, visible: false })}
        duration={3000}
        style={{ backgroundColor: snackbar.color }}
      >
        {snackbar.message}
      </Snackbar>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 24,
    flex: 1,
    backgroundColor: 'white',
  },
  label: {
    fontWeight: 'bold',
    marginBottom: 4,
  },
});

import { View } from 'react-native';
import DonationButton from './DonationButton';
import ShareButton from './ShareButton';
import { Story } from '@/types';

export default function FinalFrame({ story }: { story: Story }) {
  return (
    <View style={{ alignItems: 'flex-start', flexDirection: 'row' }}>
      <DonationButton storyId={story.id} />
      <ShareButton storyId={story.id} />
    </View>
  );
}

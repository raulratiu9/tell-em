export interface RootStackParamList {
  Home: undefined;
  StoryDetails: { id: number; story: Story };
}

export interface Story {
  storyId: string;
  title: string;
  description: string;
  featureImage: string;
  firstFrame: Frame;
}

export interface Frame {
  frameId: number;
  content: string;
  image: string;
  nextFrames: Frame[];
}

export interface InsertionStatistics {
  storiesResponse: Statistics;
  framesResponse: Statistics;
  choicesResponse: Statistics;
}

export interface Statistics {
  insertedItems: number;
  totalTimeMillis: number;
  averageTimePerItem: number;
  memoryUsedBytes: number;
}

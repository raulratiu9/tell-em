export interface RootStackParamList {
  Home: undefined;
  StoryDetails: { id: number; story: Story };
}

export interface Story {
  id: number;
  title: string;
  description: string;
  featureImage: string;
  firstFrameId: number;
  frames: Frame[];
}

export interface Frame {
  frameId: number;
  content: string;
  image: string;
  choices: Choice[];
}

export interface Choice {
  id: number;
  name: string;
  nextFrameId: number;
}

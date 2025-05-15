export interface RootStackParamList {
  Home: undefined;
  StoryDetails: { id: number; story: Story };
}

export interface Story {
  storyId: string;
  title: string;
  description: string;
  featureImage: string;
}

export interface Frame {
  id: number;
  content: string;
  image: string;
  storyId: number;
  choices: Choice[];
}

export interface Choice {
  id: number;
  name: string;
  frameId: number;
}

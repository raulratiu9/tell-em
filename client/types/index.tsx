export interface RootStackParamList {
  Home: undefined;
  StoryDetails: { id: number; story: Story };
}

export interface Story {
  id: number;
  title: string;
  content: string;
  image: string;
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

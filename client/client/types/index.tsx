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

export const mockData = {
  title: 'Aventura în pădure',
  description:
    'O poveste despre cum un personaj ajunge într-o pădure misterioasă și trebuie să ia decizii importante.',
  nodes: [
    {
      id: 1,
      content: 'John merge de unul singur într-o pădure.',
      image_id:
        'https://images.pexels.com/photos/775201/pexels-photo-775201.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',
      edges: [{ to: 2, label: 'Găsește un râu' }],
    },
    {
      id: 2,
      content: 'Găsește un râu',
      image_id:
        'https://images.pexels.com/photos/788200/pexels-photo-788200.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',
      edges: [
        { to: 3, label: 'Înoată în râu' },
        { to: 4, label: 'Caută un pod' },
      ],
    },
    {
      id: 3,
      content: 'Înoată și ajunge într-o zonă necunoscută',
      image_id:
        'https://images.pexels.com/photos/1142941/pexels-photo-1142941.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',
      edges: [
        { to: 5, label: 'Explorează îndeaproape' },
        { to: 6, label: 'Se întoarce la mal' },
      ],
    },
    {
      id: 4,
      content: 'Găsește un pod și trece râul',
      image_id:
        'https://images.pexels.com/photos/358457/pexels-photo-358457.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',
      edges: [{ to: 7, label: 'Urmează cărarea din pădure' }],
    },
    {
      id: 5,
      content: 'Explorând, descoperă o peșteră misterioasă.',
      image_id:
        'https://images.pexels.com/photos/226273/pexels-photo-226273.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',
      edges: [{ to: 6, label: 'Urmează cărarea din pădure' }],
    },
    {
      id: 6,
      content: 'Se întoarce la mal și continuă drumul',
      image_id:
        'https://images.pexels.com/photos/5321995/pexels-photo-5321995.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',
      edges: [{ to: 7, label: 'Urmează cărarea din pădure' }],
    },
    {
      id: 7,
      content: 'Urmează cărarea din pădure',
      image_id:
        'https://images.pexels.com/photos/2254030/pexels-photo-2254030.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',
      edges: [],
    },
  ],
};

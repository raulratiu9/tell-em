import { ScrollViewStyleReset } from 'expo-router/html';
import { ReactNode } from 'react';

export default function Root({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <head>
        <meta charSet="utf-8" />
        <meta httpEquiv="X-UA-Compatible" content="IE=edge" />
        <meta
          name="viewport"
          content="width=device-width, initial-scale=1, shrink-to-fit=no"
        />
        <ScrollViewStyleReset />
        <style dangerouslySetInnerHTML={{ __html: responsiveBackground }} />
      </head>
      <body>{children}</body>
    </html>
  );
}

const responsiveBackground = `
body {
  background-color: #fff;
  color: "#333";
  overflow: hidden;
}
* {
  scrollbar-width: none; /* Firefox */
}
*::-webkit-scrollbar {
  display: none; /* Chrome, Safari */
}
@media (prefers-color-scheme: dark) {
  body {
    background-color: #333;
    color: "#f4f4f4";
  }
}`;

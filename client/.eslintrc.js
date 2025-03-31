module.exports = {
  extends: ['expo', 'prettier', 'eslint:recommended'],
  plugins: ['prettier'],
  rules: {
    'prettier/prettier': [
      'error',
      {
        printWidth: 90,
        usePrettierrc: true,
        singleQuote: true,
        endOfLine: 'auto',
        indent: ['error', 2],
      },
    ],
  },
};

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './app/**/*.{js,jsx,ts,tsx}',
    './components/**/*.{js,jsx,ts,tsx}',
  ],
  presets: [require('nativewind/preset')],
  theme: {
    extend: {
      colors: {
        beasts: {
          blue: '#1e4f8f',
          ink: '#172033',
          muted: '#5d6678',
          line: '#d9e1ec',
          soft: '#f3f6fb',
          panel: '#ffffff',
          warning: '#b54708',
        },
      },
    },
  },
  plugins: [],
};

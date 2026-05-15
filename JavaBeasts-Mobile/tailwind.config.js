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
          blue: '#2563eb',
          ink: '#f8fafc',
          muted: '#bfdbfe',
          line: '#334155',
          soft: '#0f172a',
          panel: '#172033',
          gold: '#f59e0b',
          warning: '#fb923c',
        },
      },
    },
  },
  plugins: [],
};

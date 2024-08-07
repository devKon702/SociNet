/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      fontFamily: {
        body: ["DM Sans", "sans-serif"],
      },
      colors: {
        primary: "#f69175",
        secondary: "#65EEB7",
        third: "#60a5fa",
        backgroundSecondary: "rgba(101, 238, 183, 0.2)",
        backgroundLight: "#f8f9fd",
      },
    },
  },
  plugins: [],
};

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
        // secondary: "#65EEB7",
        secondary: "#669ae2",
        third: "#60a5fa",
        backgroundSecondary: "rgba(101, 238, 183, 0.2)",
        backgroundLight: "#f8f9fd",
        lightGray: "#e2e8f0",
        mediumGray: "",
        darkGray: "",
      },
    },
  },
  plugins: [],
};

import ReactDOM from "react-dom/client";
import store from "./redux/store.js";
import "./axiosConfig.js";
import "./firebaseConfig.js";
import App from "./App.jsx";
import "./index.css";
import { BrowserRouter } from "react-router-dom";
import { Provider } from "react-redux";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { ErrorBoundary } from "react-error-boundary";
import ErrorPage from "./pages/ErrorPage.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
  // <React.StrictMode>
  <Provider store={store}>
    <BrowserRouter>
      <GoogleOAuthProvider clientId="1054565960382-t27q2n763gg7pgueepvf4t3mclfcq14f.apps.googleusercontent.com">
        <ErrorBoundary FallbackComponent={ErrorPage}>
          <App />
        </ErrorBoundary>
      </GoogleOAuthProvider>
    </BrowserRouter>
  </Provider>
  // </React.StrictMode>
);

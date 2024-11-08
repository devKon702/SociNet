import axios from "axios";
import { refreshToken } from "./api/AuthService";
import { signin, signout } from "./redux/authSlice";

axios.defaults.baseURL = "http://localhost:8080";
axios.defaults.withCredentials = true;

export const setupInterceptors = (store) => {
  axios.interceptors.request.use(
    (config) => {
      const auth = store.getState().auth;
      if (auth.token) {
        config.headers["Authorization"] = `Bearer ${auth.token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  axios.interceptors.response.use(
    (response) => {
      return response;
    },
    async (error) => {
      const originalRequest = error.config;
      const message = error.response?.data?.message;

      if (message === "TOKEN EXPIRED" && !originalRequest._retry) {
        originalRequest._retry = true;
        try {
          const auth = store.getState().auth;
          await refreshToken(localStorage.getItem("socinet")).then((res) => {
            if (res.isSuccess) {
              const { accessToken, refreshToken, account } = res.data;
              store.dispatch(signin({ token: accessToken, user: account }));
              localStorage.setItem("socinet", refreshToken);
            }
          });
          axios.defaults.headers.common[
            "Authorization"
          ] = `Bearer ${auth.token}`;

          originalRequest.headers["Authorization"] = `Bearer ${auth.token}`;
          return axios(originalRequest);
        } catch (refreshError) {
          console.log(JSON.stringify(refreshError));
          store.dispatch(signout());
          // window.location.href = "/auth/signin";
          return Promise.reject(refreshError);
        }
      }

      if (error.response?.status === 401 || message === "Unauthorized") {
        store.dispatch(signout());
        // window.location.href = "/auth/signin";
      }

      return Promise.reject(error);
    }
  );
};
export default axios;

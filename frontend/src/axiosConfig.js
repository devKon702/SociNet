import axios from "axios";
import { refreshTokenThunk, signout } from "./redux/authSlice";
import { useDispatch } from "react-redux";
import { refreshToken } from "./api/AuthService";
import { useNavigate } from "react-router-dom";

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
      console.log(error);

      if (message === "TOKEN EXPIRED" && !originalRequest._retry) {
        originalRequest._retry = true;
        try {
          const dispatch = useDispatch();
          const navigate = useNavigate();
          const auth = store.getState().auth;
          await dispatch(refreshTokenThunk(dispatch, navigate));
          axios.defaults.headers.common[
            "Authorization"
          ] = `Bearer ${auth.token}`;
          originalRequest.headers["Authorization"] = `Bearer ${auth.token}`;
          return axios(originalRequest);
        } catch (refreshError) {
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

import axios from "axios";
import { refreshToken } from "./api/AuthService";
import { signin, signout } from "./redux/authSlice";

axios.defaults.baseURL = import.meta.env.VITE_API_BASE;
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
          await refreshToken().then((res) => {
            if (res.isSuccess) {
              const { accessToken, account, loginSessionId } = res.data;
              store.dispatch(
                signin({
                  token: accessToken,
                  user: account,
                  loginSessionId,
                })
              );
              // localStorage.setItem("socinet", refreshToken);
            }
          });
          axios.defaults.headers.common[
            "Authorization"
          ] = `Bearer ${auth.token}`;

          originalRequest.headers["Authorization"] = `Bearer ${auth.token}`;
          return axios(originalRequest);
        } catch (refreshError) {
          // console.log(JSON.stringify(refreshError));
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

import axios from "axios";

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

      //   if (message === "TOKEN EXPIRED" && !originalRequest._retry) {
      //     originalRequest._retry = true;
      //     try {
      //       const dispatch = useDispatch();
      //       const response = await dispatch(refreshToken()).unwrap();
      //       const newToken = response.token;
      //       axios.defaults.headers.common["Authorization"] = `Bearer ${newToken}`;
      //       originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
      //       return axios(originalRequest);
      //     } catch (refreshError) {
      //       store.dispatch(logout());
      //       window.location.href = "/login";
      //       return Promise.reject(refreshError);
      //     }
      //   }

      //   if (error.response?.status === 401 || message === "Unauthorized") {
      //     store.dispatch(logout());
      //     window.location.href = "/login";
      //   }

      return Promise.reject(error);
    }
  );
};
export default axios;

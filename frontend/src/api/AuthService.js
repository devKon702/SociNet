import axios from "../axiosConfig";
import {
  setError,
  setPending,
  setSuccess,
  signin,
  signout,
} from "../redux/authSlice";
const AuthService = {
  signIn: async ({ username, password }, dispatch, navigate) => {
    try {
      dispatch(setPending());
      const res = await axios
        .post("api/v1/auth/sign-in", {
          username,
          password,
        })
        .then((res) => res.data);
      dispatch(setSuccess());
      dispatch(signin({ token: res.data.accessToken, user: res.data.account }));
      localStorage.setItem("socinet", res.data.refreshToken);
      navigate("/");
      return res.data.data;
    } catch (error) {
      console.log(error);
      dispatch(setError(error.response.data.message));
    }
  },
  signOut: async (dispatch, navigate) => {
    dispatch(signout());
    localStorage.removeItem("socinet");
    navigate("/auth/signin");
  },
  signUp: async (username, password, email, name, otp) =>
    axios
      .post("api/v1/auth/sign-up", { username, password, email, name, otp })
      .then((res) => res.data),
  refreshToken: async (dispatch, navigate) => {
    try {
      const token = localStorage.getItem("socinet");
      const res = await axios
        .get(`api/v1/auth/refresh-token/${token}`)
        .then((res) => res.data);
      dispatch(signin({ token: res.data.accessToken, user: res.data.account }));
      localStorage.setItem("socinet", res.data.refreshToken);
    } catch (e) {
      navigate("/auth/signin");
    }
  },
  getOtp: async (email) =>
    axios
      .get("api/v1/auth/otp/" + email)
      .then((res) => res.data)
      .catch((e) => e.response.data.message),
};

export const { signIn, signOut, signUp, refreshToken, getOtp } = AuthService;
export default AuthService;

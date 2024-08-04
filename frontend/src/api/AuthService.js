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
      // if (res.data.account.roles.includes("USER")) navigate("/");
      // else if (res.data.account.roles.includes("ADMIN")) navigate("/admin");
      return res;
    } catch (error) {
      console.log(error);
      dispatch(setError(error.response.data.message));
    }
  },
  signInWithGoogle: async (email, googleId, name, avatarUrl) =>
    axios
      .post(
        "api/v1/auth/google",
        { email, googleId, name, avatarUrl },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),
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
      dispatch(signout());
    }
  },
  getOtp: async (email) =>
    axios
      .get("api/v1/auth/otp/" + email)
      .then((res) => res.data)
      .catch((e) => e.response.data.message),

  searchAccountByEmail: async (email) =>
    axios
      .get("api/v1/auth/forgot-password?email=" + email)
      .then((res) => res.data),
  forgotPassword: async (email, newPassword, otp) =>
    axios
      .put(
        "api/v1/auth/forgot-password",
        { email, newPassword, otp },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data.message),
};

export const {
  signIn,
  signInWithGoogle,
  signOut,
  signUp,
  refreshToken,
  getOtp,
  searchAccountByEmail,
  forgotPassword,
} = AuthService;
export default AuthService;

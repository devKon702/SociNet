import axios from "../axiosConfig";
import { getIpInformation } from "../helper";
import {
  setError,
  setPending,
  setSuccess,
  signin,
  signout,
} from "../redux/authSlice";
import { socket } from "../socket";

const BASE = "api/v1/auth";
const AuthService = {
  // signIn: async ({ username, password }, dispatch) => {
  //   dispatch(setPending());
  //   const res = await axios
  //     .post("api/v1/auth/sign-in", {
  //       username,
  //       password,
  //     })
  //     .then((res) => res.data)
  //     .catch((e) => e.response.data);
  //   if (res.isSuccess) {
  //     dispatch(setSuccess());
  //     dispatch(signin({ token: res.data.accessToken, user: res.data.account }));
  //     localStorage.setItem("socinet", res.data.refreshToken);
  //     // if (res.data.account.roles.includes("USER")) navigate("/");
  //     // else if (res.data.account.roles.includes("ADMIN")) navigate("/admin");
  //     return res;
  //   } else {
  //     console.log(res);
  //     dispatch(setError(res.message));
  //   }
  // },
  signIn: async (username, password) => {
    const ipInformation = await getIpInformation();
    return axios
      .post(BASE + "/sign-in?ip=" + ipInformation.query, { username, password })
      .then((res) => res.data)
      .catch((e) => e.response.data);
  },
  signInWithGoogle: async (email, googleId, name, avatarUrl) => {
    const ipInformation = await getIpInformation();
    return axios
      .post(
        "api/v1/auth/google",
        { email, googleId, name, avatarUrl, ip: ipInformation.query },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data);
  },
  signOut: async (dispatch, navigate) => {
    dispatch(signout());
    navigate("/auth/signin");
    socket.disconnect();
  },
  logout: async () =>
    axios
      .delete("api/v1/auth/sign-out")
      .then((res) => res.data)
      .catch((e) => e.response.data),
  signUp: async (username, password, email, name, otp) =>
    axios
      .post("api/v1/auth/sign-up", { username, password, email, name, otp })
      .then((res) => res.data)
      .catch((e) => e.response.data),
  // refreshToken: async (dispatch) => {
  //   const token = localStorage.getItem("socinet");
  //   if (!token) dispatch(signout());
  //   else {
  //     // const ipInfo = await getIpInformation();
  //     // const userAgent = navigator.userAgent;
  //     return axios
  //       .get(`api/v1/auth/refresh-token?token=${token}`)
  //       .then((res) => {
  //         const { accessToken, refreshToken, account } = res.data.data;
  //         dispatch(signin({ token: accessToken, user: account }));
  //         localStorage.setItem("socinet", refreshToken);
  //       })
  //       .catch((e) => {
  //         dispatch(signout());
  //         console.log(e);
  //       });
  //   }
  // },
  refreshToken: async () => {
    return axios
      .get(`api/v1/auth/refresh-token`)
      .then((res) => res.data)
      .catch((e) => e.response.data);
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
      .catch((e) => e.response.data),
};

export const {
  signIn,
  signInWithGoogle,
  signOut,
  logout,
  signUp,
  refreshToken,
  getOtp,
  searchAccountByEmail,
  forgotPassword,
} = AuthService;
export default AuthService;

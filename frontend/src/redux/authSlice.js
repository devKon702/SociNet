import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import axios from "axios";
import { refreshToken, signIn, signInWithGoogle } from "../api/AuthService";
import { socket } from "../socket";
import store from "./store";
import { showSnackbar } from "./snackbarSlice";

const authSlice = createSlice({
  name: "auth",
  initialState: {
    token: null,
    user: null,
    isLoading: true,
    error: null,
    isAuthenticated: false,
  },
  reducers: {
    signin: (state, action) => {
      state.token = action.payload.token;
      state.user = action.payload.user;
      state.isAuthenticated = true;
      state.isLoading = false;
    },
    signout: (state) => {
      state.token = null;
      state.user = null;
      state.isAuthenticated = false;
      state.isLoading = false;
      localStorage.removeItem("socinet");
      socket.disconnect();
    },
    setPending: (state) => {
      state.isLoading = true;
      state.error = null;
    },
    setError: (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    },
    setSuccess: (state) => {
      state.isLoading = false;
      state.error = null;
    },
    setUser: (state, action) => {
      state.user = action.payload;
    },
    setUserInfo: (state, action) => {
      state.user.user = action.payload;
    },
    setAccountEmail: (state, action) => {
      state.user.email = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(signInThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          const { accessToken, refreshToken, account } = action.payload.data;
          state.token = accessToken;
          state.user = account;
          localStorage.setItem("socinet", refreshToken);
          state.isAuthenticated = true;
        } else {
          setError(action.payload.message);
        }
      })
      .addCase(signinWithGoogleThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          const { accessToken, refreshToken, account } = action.payload.data;
          state.token = accessToken;
          localStorage.setItem("socinet", refreshToken);
          state.user = account;
          state.isAuthenticated = true;
        } else {
          console.log(action.payload);
        }
      });
  },
});

export const signInThunk = createAsyncThunk(
  "auth/signInThunk",
  async ({ username, password }, { dispatch }) => {
    const res = await signIn(username, password);
    if (res.isSuccess) {
      dispatch(
        showSnackbar({ message: "Đăng nhập thành công", type: "success" })
      );
    } else {
      let message = "";
      switch (res.message) {
        case "ACCOUNT NOT FOUND":
          message = "Không tìm thấy tài khoản";
          break;
        case "INACTIVE ACCOUNT":
          message = "Tài khoản đã bị khóa";
          break;
        case "INCORRECT PASSWORD":
          message = "Mật khẩu không đúng";
          break;
        default:
          message = "Đăng nhập thất bại";
      }
      dispatch(showSnackbar({ message, type: "error" }));
    }
    return res;
  }
);

export const refreshTokenThunk = createAsyncThunk(
  "auth/refreshTokenThunk",
  async ({ dispatch, navigate }) => {
    await refreshToken(dispatch, navigate);
  }
);

export const signinWithGoogleThunk = createAsyncThunk(
  "auth/signinWithGoogleThunk",
  async ({ email, googleId, name, avatarUrl }, { dispatch }) => {
    const res = await signInWithGoogle(email, googleId, name, avatarUrl);
    if (res.isSuccess) {
      dispatch(
        showSnackbar({ message: "Đăng nhập thành công", type: "success" })
      );
    }
    return res;
  }
);

export const {
  signin,
  signout,
  setPending,
  setError,
  setSuccess,
  setUser,
  setUserInfo,
  setAccountEmail,
} = authSlice.actions;
export default authSlice.reducer;

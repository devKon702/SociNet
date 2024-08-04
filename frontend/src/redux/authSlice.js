import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import axios from "axios";
import { refreshToken, signInWithGoogle } from "../api/AuthService";
import { socket } from "../socket";

const authSlice = createSlice({
  name: "auth",
  initialState: {
    token: null,
    user: null,
    isLoading: false,
    error: null,
    isAuthenticated: false,
  },
  reducers: {
    signin: (state, action) => {
      state.token = action.payload.token;
      state.user = action.payload.user;
      state.isAuthenticated = true;
    },
    signout: (state) => {
      state.token = null;
      state.user = null;
      state.isAuthenticated = false;
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
  },
  extraReducers: (builder) => {
    builder.addCase(signinWithGoogleThunk.fulfilled, (state, action) => {
      if (action.payload.isSuccess) {
        const { accessToken, refreshToken, account } = action.payload.data;
        state.token = accessToken;
        localStorage.setItem("socinet", refreshToken);
        state.user = account;
        state.isAuthenticated = true;
      } else {
        console.log(action.payload.message);
      }
    });
  },
});

export const refreshTokenThunk = createAsyncThunk(
  "auth/refreshTokenThunk",
  async (dispatch, navigate) => {
    await refreshToken(dispatch, navigate);
  }
);

export const signinWithGoogleThunk = createAsyncThunk(
  "auth/signinWithGoogleThunk",
  async ({ email, googleId, name, avatarUrl }) => {
    const res = await signInWithGoogle(email, googleId, name, avatarUrl);
    return { ...res };
  }
);

export const { signin, signout, setPending, setError, setSuccess, setUser } =
  authSlice.actions;
export default authSlice.reducer;

import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import axios from "axios";
import { refreshToken } from "../api/AuthService";

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
});

export const refreshTokenThunk = createAsyncThunk(
  "auth/refreshTokenThunk",
  async (dispatch, navigate) => {
    await refreshToken(dispatch, navigate);
  }
);

export const { signin, signout, setPending, setError, setSuccess, setUser } =
  authSlice.actions;
export default authSlice.reducer;

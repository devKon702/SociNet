import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { searchAccountByEmail } from "../api/AuthService";

const forgotPasswordSlice = createSlice({
  name: "forgotPassword",
  initialState: {
    email: "",
    searchedAccount: null,
  },
  reducers: {
    setEmail: (state, action) => {
      state.email = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(searchAccountByEmailThunk.fulfilled, (state, action) => {
      state.searchedAccount = action.payload;
    });
  },
});

export const searchAccountByEmailThunk = createAsyncThunk(
  "forgotPassword/searchAccountByEmailThunk",
  async (email) => {
    const account = await searchAccountByEmail(email);
    return account.data;
  }
);

export const { setEmail } = forgotPasswordSlice.actions;
export default forgotPasswordSlice.reducer;

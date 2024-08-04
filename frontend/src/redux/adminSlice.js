import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  countNumberOfAccountByName,
  countNumberOfPostByUserId,
  getAccountByName,
  getPostByUserId,
} from "../api/AdminService";

const ACCOUNT_PAGE_SIZE = 6;
const POST_PAGE_SIZE = 6;

const adminSlice = createSlice({
  name: "admin",
  initialState: {
    account: {
      currentAccount: null,
      accountList: [],
      totalPage: 0,
      page: 0,
      isLoading: false,
    },
    post: {
      currentPost: null,
      postList: [],
      totalPage: 0,
      page: 0,
      isLoading: false,
    },
    filter: {
      userName: "",
    },
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getAccountListThunk.pending, (state) => {
        state.account.isLoading = true;
      })
      .addCase(getAccountListThunk.fulfilled, (state, action) => {
        state.account.isLoading = false;
        if (Array.isArray(action.payload)) {
          const [accountResult, countResult] = action.payload;
          state.account.totalPage = countResult.data / ACCOUNT_PAGE_SIZE + 1;
          state.account.accountList = accountResult.data;
        } else {
          console.log(action.payload);
        }
      })
      .addCase(getPostListThunk.pending, (state) => {
        state.post.isLoading = true;
      })
      .addCase(getPostListThunk.fulfilled, (state, action) => {
        state.post.isLoading = false;
        if (Array.isArray(action.payload)) {
          const [postResult, countResult] = action.payload;
          state.post.postList = postResult.data;
          state.post.totalPage = countResult.data / POST_PAGE_SIZE + 1;
        } else {
          console.log(action.payload);
        }
      });
  },
});

export const getAccountListThunk = createAsyncThunk(
  "admin/getAccountListThunk",
  async ({ name = "", page = 0 }) => {
    const res = await Promise.all([
      getAccountByName(name, page, ACCOUNT_PAGE_SIZE),
      countNumberOfAccountByName(name),
    ]);
    return res;
  }
);

export const getPostListThunk = createAsyncThunk(
  "admin/getPostListThunk",
  async ({ userId, page }) => {
    const res = await Promise.all([
      getPostByUserId(userId, page, POST_PAGE_SIZE),
      countNumberOfPostByUserId(userId),
    ]);
    return res;
  }
);

export const {} = adminSlice.actions;
export default adminSlice.reducer;

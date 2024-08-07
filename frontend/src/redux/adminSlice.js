import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  countNumberOfAccountByName,
  countNumberOfPostByUserId,
  getAccountByName,
  getPostByUserId,
  manageAccount,
  managePost,
} from "../api/AdminService";
import { setPostList } from "./postSlice";
import { socket, socketAdmin } from "../socket";

const ACCOUNT_PAGE_SIZE = 2;
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
  reducers: {
    setCurrentAccount: (state, action) => {
      state.account.currentAccount = action.payload;
    },
    clearPostList: (state) => {
      state.post.postList = [];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(getAccountListThunk.pending, (state) => {
        state.account.isLoading = true;
      })
      .addCase(getAccountListThunk.fulfilled, (state, action) => {
        state.account.isLoading = false;
        if (Array.isArray(action.payload)) {
          const [accountResult, countResult] = action.payload;
          state.account.totalPage =
            Math.floor(countResult.data / ACCOUNT_PAGE_SIZE) + 1;
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
      })
      .addCase(manageAccountThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          const index = state.account.accountList.findIndex(
            (item) => item.username === action.payload.data.username
          );
          if (index != -1) {
            state.account.accountList[index] = action.payload.data;
          }
          socketAdmin.emit("FORCE LOGOUT", action.payload.data.user.id);
        } else {
          console.log(action.payload);
        }
      })
      .addCase(managePostThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          const index = state.post.postList.findIndex(
            (item) => item.id === action.payload.data.id
          );
          if (index != -1) {
            state.post.postList[index] = action.payload.data;
          }
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

export const manageAccountThunk = createAsyncThunk(
  "admin/manageAccountThunk",
  async ({ username, isActive }) => {
    return await manageAccount(username, isActive);
  }
);

export const managePostThunk = createAsyncThunk(
  "admin/managePostThunk",
  async ({ postId, isActive }) => {
    return await managePost(postId, isActive);
  }
);

export const { setCurrentAccount, clearPostList } = adminSlice.actions;
export default adminSlice.reducer;

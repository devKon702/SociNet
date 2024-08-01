import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getUserInfo } from "../api/UserService";
import { getPostByUserId } from "../api/PostService";
import {
  checkIsFriend,
  getFriendList,
  makeInvitation,
} from "../api/FriendService";

const personalSlice = createSlice({
  name: "personal",
  initialState: {
    user: null,
    postList: [],
    friendList: [],
    isLoading: false,
    friendStatus: "NO",
  },
  reducers: {
    setInfo: (state, action) => {
      state.user = action.payload;
    },
  },

  extraReducers: (builder) => {
    builder
      .addCase(getPersonalInfo.fulfilled, (state, action) => {
        state.user = action.payload.data;
      })
      .addCase(getPersonalPost.fulfilled, (state, action) => {
        state.postList = action.payload.data;
      })
      .addCase(getFriendListThunk.fulfilled, (state, action) => {
        state.friendList = action.payload.data;
      })
      .addCase(preparePersonalInfoThunk.fulfilled, (state, action) => {
        const [
          personalInfoResult,
          postResult,
          friendResult,
          friendStatusResult,
        ] = action.payload;
        state.user = personalInfoResult.data;
        state.postList = postResult.data;
        state.friendList = friendResult.data;
        state.friendStatus = friendStatusResult.data;
      })
      .addCase(preparePersonalInfoThunk.rejected, (state, action) => {
        console.log(action.error);
      })
      .addCase(createInvitationThunk.pending, (state, action) => {
        state.friendStatus = "INVITED";
      })
      .addCase(createInvitationThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          console.log(action.payload.message);
        } else {
          console.log(action.payload.message);
        }
      });
  },
});

export const getPersonalInfo = createAsyncThunk(
  "personal/getPersonalInfo",
  async (userId) => {
    const res = await getUserInfo(userId);
    return res;
  }
);

export const getPersonalPost = createAsyncThunk(
  "personal/getPersonalPost",
  async (userId) => {
    const res = await getPostByUserId(userId);
    return res;
  }
);

export const getFriendListThunk = createAsyncThunk(
  "personal/getFriendListThunk",
  async (userId) => {
    const res = await getFriendList(userId);
    return res;
  }
);

export const preparePersonalInfoThunk = createAsyncThunk(
  "personal, preparePersonalInfoThunk",
  async (userId) => {
    return await Promise.all([
      getUserInfo(userId),
      getPostByUserId(userId),
      getFriendList(userId),
      checkIsFriend(userId),
    ]);
  }
);

export const createInvitationThunk = createAsyncThunk(
  "personal/createInvitationThunk",
  async (userId) => {
    return await makeInvitation(userId);
  }
);

export const { setInfo } = personalSlice.actions;
export default personalSlice.reducer;

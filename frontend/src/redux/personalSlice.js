import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getUserInfo } from "../api/UserService";
import { getPostByUserId } from "../api/PostService";
import {
  checkIsFriend,
  getFriendList,
  makeInvitation,
} from "../api/FriendService";
import { socket } from "../socket";

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
    setFriendStatus: (state, action) => {
      state.friendStatus = action.payload;
    },
    addNewPost: (state, action) => {
      !state.postList.some((item) => item.id == action.payload.id)
        ? state.postList.unshift(action.payload)
        : null;
    },
    removePersonalPost: (state, action) => {
      state.postList = state.postList.filter(
        (post) => post.id != action.payload
      );
    },
    updatePersonalPost: (state, action) => {
      const index = state.postList.findIndex(
        (item) => item.id == action.payload.id
      );
      if (index != -1) state.postList[index] = action.payload;
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
        if (
          Array.isArray(action.payload) &&
          action.payload.every((result) => result.isSuccess)
        ) {
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
        } else {
          console.log(action.payload);
        }
      })
      .addCase(preparePersonalInfoThunk.rejected, (state, action) => {
        console.log(action.error);
      })
      .addCase(createInvitationThunk.pending, (state) => {
        state.friendStatus = "INVITED";
      })
      .addCase(createInvitationThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          socket.emit(
            "NEW INVITATION",
            action.payload.data,
            action.payload.sender,
            action.payload.receiverId
          );
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
    const res = await Promise.all([
      getUserInfo(userId),
      getPostByUserId(userId),
      getFriendList(userId),
      checkIsFriend(userId),
    ]);
    return res;
  }
);

export const createInvitationThunk = createAsyncThunk(
  "personal/createInvitationThunk",
  async (userId, { getState }) => {
    const res = await makeInvitation(userId);
    const storeState = getState();
    return { ...res, sender: storeState.auth.user.user, receiverId: +userId };
  }
);

export const {
  setInfo,
  setFriendStatus,
  addNewPost,
  removePersonalPost,
  updatePersonalPost,
} = personalSlice.actions;
export default personalSlice.reducer;

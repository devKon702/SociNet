import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getUserInfo } from "../api/UserService";
import { getPostByUserId } from "../api/PostService";

const personalSlice = createSlice({
  name: "personal",
  initialState: {
    user: null,
    postList: [],
    isLoading: false,
  },
  reducers: {
    setInfo: (state, action) => {
      state.user = action.payload;
    },
  },

  extraReducers: (builder) => {
    builder
      .addCase(getPersonalInfo.fulfilled, (state, action) => {
        state.user = action.payload;
      })
      .addCase(getPersonalPost.fulfilled, (state, action) => {
        state.postList = action.payload;
      });
  },
});

export const getPersonalInfo = createAsyncThunk(
  "personal/getPersonalInfo",
  async (userId) => {
    const res = await getUserInfo(userId);
    return res.data;
  }
);

export const getPersonalPost = createAsyncThunk(
  "personal/getPersonalPost",
  async (userId) => {
    const res = await getPostByUserId(userId);
    return res.data;
  }
);

export const { setInfo } = personalSlice.actions;
export default personalSlice.reducer;

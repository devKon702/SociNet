import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { editPost, getPost, removePost } from "../api/PostService";

const postSlice = createSlice({
  name: "post",
  initialState: {
    postList: [],
    isLoading: false,
    error: null,
  },
  reducers: {
    setPostList: (state, action) => {
      state.postList = action.payload;
    },
    addPost: (state, action) => {
      state.postList.unshift(action.payload);
    },
    updatePost: (state, action) => {
      const index = state.postList.findIndex(
        (post) => post.id === action.payload.id
      );
      state.postList[index] = action.payload;
    },
    setLoading: (state) => {
      state.isLoading = true;
    },
    setSuccess: (state) => {
      state.isLoading = false;
      state.error = null;
    },
    setError: (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(updatePostThunk.fulfilled, (state, action) => {
        const index = state.postList.findIndex(
          (post) => post.id === action.payload.id
        );
        state.postList[index] = action.payload;
      })
      .addCase(editPostThunk.fulfilled, (state, action) => {
        const index = state.postList.findIndex(
          (post) => post.id === action.payload.id
        );
        state.postList[index] = action.payload;
      })
      .addCase(removePostThunk.fulfilled, (state, action) => {
        state.postList = state.postList.filter(
          (post) => post.id !== action.payload
        );
      });
  },
});

export const updatePostThunk = createAsyncThunk(
  "post/updatePostThunk",
  async (postId, _) => {
    const response = await getPost(postId);
    return response.data;
  }
);

export const editPostThunk = createAsyncThunk(
  "post/editPostThunk",
  async ({ postId, caption, file }) => {
    const res = await editPost(postId, caption, file, null);
    return res.data;
  }
);

export const removePostThunk = createAsyncThunk(
  "post/removePostThunk",
  async (postId) => {
    const res = await removePost(postId);
    return postId;
  }
);

export const {
  setPostList,
  addPost,
  updatePost,
  setLoading,
  setError,
  setSuccess,
} = postSlice.actions;

export default postSlice.reducer;

import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getCommentsOfPost } from "../api/CommentService";

const commentSlice = createSlice({
  name: "comment",
  initialState: {
    commentList: [],
    currentPostId: null,
    isReplying: false,
    replyCommentId: null,
  },
  reducers: {
    setCommentList: (state, action) => {
      state.commentList = action.payload;
    },
    updateComment: (state, action) => {
      const index = state.commentList.findIndex(
        (comment) => comment.id === action.payload.id
      );
      state.commentList[index] = action.payload;
    },
    setCurrentPostId: (state, action) => {
      state.currentPostId = action.payload;
    },
    addComment: (state, action) => {
      state.commentList = [action.payload, ...state.commentList];
    },
    setReply: (state, action) => {
      state.isReplying = action.payload.isReplying;
      state.replyCommentId = action.payload.replyCommentId;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(updateCommentListThunk.fulfilled, (state, action) => {
      state.commentList = action.payload;
    });
  },
});

export const updateCommentListThunk = createAsyncThunk(
  "comment/updateCommentListThunk",
  async (postId) => {
    const res = await getCommentsOfPost(postId);
    return res.data;
  }
);

export const {
  setCommentList,
  updateComment,
  setCurrentPostId,
  addComment,
  setReply,
} = commentSlice.actions;

export default commentSlice.reducer;

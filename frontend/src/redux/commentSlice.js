import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  editComment,
  getCommentsOfPost,
  removeComment,
} from "../api/CommentService";

const commentSlice = createSlice({
  name: "comment",
  initialState: {
    commentList: [],
    currentPostId: null,
    action: "COMMENT", // [COMMENT, REPLY, EDIT]
    currentCommentId: null,
    currentComment: null,
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
    setReplyAction: (state, action) => {
      state.action = "REPLY";
      state.currentCommentId = action.payload;
      state.currentComment = state.commentList.find(
        (item) => item.id === action.payload
      );
    },
    setEditAction: (state, action) => {
      state.action = "EDIT";
      state.currentCommentId = action.payload;
      // Kiểm tra comment cha
      for (let item of state.commentList) {
        if (item.id === action.payload) {
          state.currentComment = item;
          return;
        } else {
          // Kiểm tra comment con
          for (let child of item.childComments) {
            if (child.id === action.payload) {
              state.currentComment = child;
              return;
            }
          }
        }
      }
    },
    setCommentAction: (state, action) => {
      state.action = "COMMENT";
      state.currentCommentId = null;
      state.currentComment = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(updateCommentListThunk.fulfilled, (state, action) => {
        state.commentList = action.payload;
      })
      .addCase(editCommentThunk.fulfilled, (state, action) => {
        const index = state.commentList.findIndex(
          (item) => item.id === action.payload.id
        );
        state.commentList[index] = action.payload;
      })
      .addCase(removeCommentThunk.fulfilled, (state, action) => {
        state.commentList = state.commentList.filter(
          (item) => item.id !== action.payload
        );
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

export const editCommentThunk = createAsyncThunk(
  "comment/editCommentThunk",
  async ({ commentId, content }) => {
    const res = await editComment(commentId, content);
    return res.data;
  }
);

export const removeCommentThunk = createAsyncThunk(
  "comment/removeCommentThunk",
  async (commentId) => {
    await removeComment(commentId);
    return commentId;
  }
);

export const {
  setCommentList,
  updateComment,
  setCurrentPostId,
  addComment,
  setReplyAction,
  setCommentAction,
  setEditAction,
} = commentSlice.actions;

export default commentSlice.reducer;

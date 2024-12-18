import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { createPost, editPost, getPost, removePost } from "../api/PostService";
import { showSnackbar } from "./snackbarSlice";
import {
  addNewPost,
  removePersonalPost,
  updatePersonalPost,
} from "./personalSlice";
import { resetDataStore } from "./store";

const initialState = {
  postList: [],
  currentScrollPosition: 0,
  isLoading: false,
  error: null,
  action: {
    create: "",
    edit: "",
    remove: "",
    share: "",
  },
};

const postSlice = createSlice({
  name: "post",
  initialState,
  reducers: {
    setAction: (state, action) => {
      state.action = { ...state.action, ...action.payload };
    },
    setPostList: (state, action) => {
      state.postList = action.payload;
    },
    setCurrentScroll: (state, action) => {
      state.currentScrollPosition = action.payload;
    },
    addPost: (state, action) => {
      state.postList.unshift(action.payload);
    },
    removeFromPostList: (state, action) => {
      state.postList = state.postList.filter(
        (post) => post.id !== action.payload
      );
    },
    updatePost: (state, action) => {
      const index = state.postList.findIndex(
        (post) => post.id === action.payload.id
      );
      state.postList[index] = action.payload;
    },
    updateOwnerNameOfPost: (state, action) => {
      const { userId, name, avatarUrl } = action.payload;
      state.postList.forEach((post) => {
        if (post.user.id == userId) {
          post.user.name = name;
          post.user.avatarUrl = avatarUrl;
        }
        if (post.sharedPost != null && post.sharedPost.user.id == userId) {
          post.sharedPost.user.name = name;
          post.sharedPost.user.avatarUrl = avatarUrl;
        }
      });
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
      .addCase(resetDataStore, () => initialState)
      .addCase(createPostThunk.pending, (state) => {
        state.action.create = "creating";
        state.action.share = "sharing";
      })
      .addCase(createPostThunk.fulfilled, (state, action) => {
        state.isLoading = false;
        if (action.payload.isSuccess) {
          state.postList.unshift(action.payload.data);
          state.action.create = "created";
          state.action.share = "shared";
        } else {
          state.action.create = "error";
          state.action.share = "error";
        }
      })
      .addCase(updatePostThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          const index = state.postList.findIndex(
            (post) => post.id === action.payload.data.id
          );
          state.postList[index] = action.payload.data;
        } else {
          console.log(action.payload);
        }
      })
      .addCase(editPostThunk.pending, (state) => {
        state.action.edit = "editing";
      })
      .addCase(editPostThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          state.action.edit = "edited";
          const index = state.postList.findIndex(
            (post) => post.id === action.payload.data.id
          );
          state.postList[index] = action.payload.data;
        } else {
          if (action.payload.message == "POST NOT FOUND") {
            state.action.edit = "edited";
          }
          state.action.edit = "error";
        }
      })
      .addCase(removePostThunk.fulfilled, (state, action) => {
        state.postList = state.postList.filter(
          (post) => post.id !== action.payload
        );
      });
  },
});

export const createPostThunk = createAsyncThunk(
  "post/createPostThunk",
  async ({ caption, file, sharedPostId }, { dispatch, getState }) => {
    const res = await createPost(caption, file, sharedPostId);
    if (res.isSuccess) {
      dispatch(
        showSnackbar({
          message: sharedPostId
            ? "Chia sẻ thành công"
            : "Tạo bài viết thành công",
          type: "success",
        })
      );
      if (getState().personal.user?.id == getState().auth.user.user.id) {
        dispatch(addNewPost(res.data));
      }
    } else {
      let message = "";
      switch (res.message) {
        case "SHARED POST NOT FOUND":
          message = "Bài viết được chia sẻ không còn tồn tại";
          dispatch(removeFromPostList(sharedPostId));
          if (getState().personal.user) {
            dispatch(removePersonalPost(sharedPostId));
          }
          break;
        case "OVERSIZE IMAGE":
          message = "Ảnh quá lớn";
          break;
        case "OVERSIZE VIDEO":
          message = "Video quá lớn";
          break;
        case "UNSUPPORTED FILE":
          message = "Định dạng file không hỗ trợ";
          break;
        default:
          message = "Thêm bài viết thất bại";
      }
      dispatch(showSnackbar({ message, type: "error" }));
    }
    return res;
  }
);

export const updatePostThunk = createAsyncThunk(
  "post/updatePostThunk",
  async (postId, { getState, dispatch }) => {
    const response = await getPost(postId);

    if (!response.isSuccess) {
      switch (response.message) {
        case "POST NOT FOUND":
          if (getState().personal.user) {
            dispatch(removePersonalPost(postId));
          }
          dispatch(removeFromPostList(postId));
          dispatch(
            showSnackbar({ message: "Bài viết không tồn tại", type: "error" })
          );
          break;
        default:
          dispatch(
            showSnackbar({
              message: "Lấy thông tin bài viết thất bại",
              type: "error",
            })
          );
      }
    } else {
      dispatch(updatePersonalPost(response.data));
    }

    return response;
  }
);

export const editPostThunk = createAsyncThunk(
  "post/editPostThunk",
  async ({ postId, caption, file }, { dispatch, getState }) => {
    const res = await editPost(postId, caption, file);
    if (res.isSuccess) {
      dispatch(
        showSnackbar({ message: "Chỉnh sửa thành công", type: "success" })
      );
      dispatch(updatePersonalPost(res.data));
    } else {
      let message = "";
      switch (res.message) {
        case "POST NOT FOUND":
          message = "Bài viết không còn tồn tại";
          if (getState().personal.user?.id == getState().auth.user.user.id) {
            dispatch(removePersonalPost(postId));
          }
          dispatch(removeFromPostList(postId));
          break;
        case "OVERSIZE IMAGE":
          message = "Ảnh quá lớn";
          break;
        case "OVERSIZE VIDEO":
          message = "Video quá lớn";
          break;
        case "UNSUPPORTED FILE":
          message = "Định dạng file không hỗ trợ";
          break;
        default:
          message = "Chỉnh sửa bài viết thất bại";
      }
      dispatch(showSnackbar({ message, type: "error" }));
    }

    return res;
  }
);

export const removePostThunk = createAsyncThunk(
  "post/removePostThunk",
  async (postId, { dispatch, getState }) => {
    const res = await removePost(postId);
    if (res.isSuccess) {
      dispatch(showSnackbar({ message: "Gỡ thành công", type: "success" }));
    } else {
      dispatch(
        showSnackbar({ message: "Bài viết không còn tồn tại", type: "error" })
      );
    }
    if (getState().personal.user) dispatch(removePersonalPost(+postId));
    return postId;
  }
);

export const {
  resetPostSlice,
  setAction,
  setPostList,
  setCurrentScroll,
  addPost,
  removeFromPostList,
  updatePost,
  updateOwnerNameOfPost,
  setLoading,
  setError,
  setSuccess,
} = postSlice.actions;

export default postSlice.reducer;

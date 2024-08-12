import axios from "axios";
import {
  setError,
  setLoading,
  setSuccess,
  setPostList,
} from "../redux/postSlice";

const PostService = {
  getPost: async (postId) =>
    axios
      .get("api/v1/posts/" + postId)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  getPosts: async () => axios.get("api/v1/posts").then((res) => res.data),

  reactPost: async ({ postId, type }) =>
    axios
      .post("api/v1/reactions", { postId, type })
      .then((res) => res.data)
      .catch((e) => e.response.data),

  deleteReactPost: async (postId) =>
    axios
      .delete("api/v1/reactions/post/" + postId)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  createPost: async (caption, file, sharedPostId) =>
    axios
      .post(
        "api/v1/posts",
        { caption, file, sharedPostId },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),

  editPost: async (postId, caption, file) =>
    axios
      .put(
        "api/v1/posts/" + postId,
        { caption, file },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),

  removePost: async (postId) =>
    axios
      .delete("/api/v1/posts/" + postId)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  getPostByUserId: async (userId) =>
    axios.get("api/v1/posts/user/" + userId).then((res) => res.data),
};

export const {
  getPost,
  getPosts,
  reactPost,
  deleteReactPost,
  createPost,
  editPost,
  removePost,
  getPostByUserId,
} = PostService;

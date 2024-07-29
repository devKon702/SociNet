import axios from "axios";
import {
  setError,
  setLoading,
  setSuccess,
  setPostList,
} from "../redux/postSlice";

const PostService = {
  getPost: async (postId) =>
    axios.get("api/v1/posts/" + postId).then((res) => res.data),

  getPosts: async () => axios.get("api/v1/posts").then((res) => res.data),

  reactPost: async ({ postId, type }) =>
    axios.post("api/v1/reactions", { postId, type }).then((res) => res.data),

  deleteReactPost: async (postId) =>
    axios.delete("api/v1/reactions/post/" + postId).then((res) => res.data),

  createPost: async (caption, image, video, sharedPostId) =>
    axios
      .post(
        "api/v1/posts",
        { caption, image, video, sharedPostId },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data),

  getPostByUserId: async (userId) =>
    axios.get("api/v1/posts/user/" + userId).then((res) => res.data),
};

export const {
  getPost,
  getPosts,
  reactPost,
  deleteReactPost,
  createPost,
  getPostByUserId,
} = PostService;

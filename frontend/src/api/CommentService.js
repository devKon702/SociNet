import axios from "axios";

const CommentService = {
  getCommentsOfPost: async (postId) =>
    axios
      .get("api/v1/comments/post/" + postId)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  createComment: async (postId, content, parentCommentId) =>
    axios
      .post("api/v1/comments", { postId, content, parentCommentId })
      .then((res) => res.data)
      .catch((e) => e.response.data),

  editComment: async (commentId, content) =>
    axios
      .put(
        "api/v1/comments/" + commentId,
        { content },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),

  removeComment: async (commentId) =>
    axios
      .delete("api/v1/comments/" + commentId)
      .then((res) => res.data)
      .catch((e) => e.response.data),
};

export const { getCommentsOfPost, createComment, editComment, removeComment } =
  CommentService;

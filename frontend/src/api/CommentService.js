import axios from "axios";

const CommentService = {
  getCommentsOfPost: async (postId) =>
    axios.get("api/v1/comments/post/" + postId).then((res) => res.data),

  createComment: async (postId, content, parentCommentId) =>
    axios
      .post("api/v1/comments", { postId, content, parentCommentId })
      .then((res) => res.data),
};

export const { getCommentsOfPost, createComment } = CommentService;

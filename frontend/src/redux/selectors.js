export const authSelector = (state) => state.auth;
export const userInfoSelector = (state) => state.auth?.user?.user;

// POST
export const postSelector = (state) => state.post;

// COMMENT
export const commentSelector = (state) => state.comment;
export const commentListSelector = (state) => state.comment.commentList;

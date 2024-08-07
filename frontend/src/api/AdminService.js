import axios from "axios";

const BASE = "api/v1/admin";

const AdminService = {
  getAccountByName: async (name, page = 0, size = 5) =>
    axios
      .get(BASE + "/accounts", { params: { name, page, size } })
      .then((res) => res.data)
      .catch((e) => e.response.data),

  countNumberOfAccountByName: async (name) =>
    axios
      .get(BASE + "/count/accounts", { params: { name } })
      .then((res) => res.data)
      .catch((e) => e.response.data),

  getPostByUserId: async (userId, page = 0, size = 5) =>
    axios
      .get(BASE + "/posts", { params: { userId, page, size } })
      .then((res) => res.data)
      .catch((e) => e.response.data),

  countNumberOfPostByUserId: async (userId) =>
    axios
      .get(BASE + "/count/posts", { params: { userId } })
      .then((res) => res.data)
      .catch((e) => e.response.data),
  manageAccount: async (username, isActive) =>
    axios
      .put(
        BASE + "/accounts/" + username,
        {},
        {
          params: { action: isActive ? "activate" : "inactivate" },
        }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),
  managePost: async (postId, isActive) =>
    axios
      .put(
        BASE + "/posts/" + postId,
        {},
        {
          params: { action: isActive ? "activate" : "inactivate" },
        }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),
};

export const {
  getAccountByName,
  getPostByUserId,
  countNumberOfAccountByName,
  countNumberOfPostByUserId,
  manageAccount,
  managePost,
} = AdminService;

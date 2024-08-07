import axios from "axios";

const UserService = {
  getUserInfo: async (userId) =>
    axios
      .get("api/v1/users/" + userId)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  getUsersByName: async (name, page, size) =>
    axios
      .get("api/v1/users", { params: { name, page, size } })
      .then((res) => res.data)
      .catch((e) => e.response.data),

  updateUserInfo: async (name, phone, school, address, isMale, avatar) =>
    axios
      .put(
        "api/v1/users/me",
        {
          name,
          phone,
          school,
          address,
          isMale,
          avatar,
        },
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),
};

export const { getUserInfo, getUsersByName, updateUserInfo } = UserService;

import axios from "axios";

const UserService = {
  getUserInfo: async (userId) =>
    axios.get("api/v1/users/" + userId).then((res) => res.data),

  getUserInfos: async () => axios.get("api/v1/users").then((res) => res.data),

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
      .then((res) => res.data),
};

export const { getUserInfo, getUserInfos, updateUserInfo } = UserService;

import axios from "axios";

const FriendService = {
  getFriendList: async (userId) =>
    axios
      .get("api/v1/friend/" + userId)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  getInvitations: async () =>
    axios
      .get("api/v1/friend/invitation")
      .then((res) => res.data)
      .catch((e) => e.response.data),

  makeInvitation: async (userId) =>
    axios
      .post("api/v1/friend/" + userId)
      .then((res) => res.data)
      .catch((e) => e.response.data),
  responseInvitation: async (id, isAccept) =>
    axios
      .put(`api/v1/friend/${id}?isAccept=${isAccept}`)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  checkIsFriend: async (userId) =>
    axios
      .get(`api/v1/friend/check/${userId}`)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  getFriendSuggestion: async () =>
    axios
      .get("api/v1/friend/suggestion")
      .then((res) => res.data)
      .catch((e) => e.response.data),
};

export const {
  getFriendList,
  getInvitations,
  makeInvitation,
  responseInvitation,
  checkIsFriend,
  getFriendSuggestion,
} = FriendService;

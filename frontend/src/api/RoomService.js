import axios from "axios";

const BASE = "api/v1/rooms";
const RoomService = {
  getJoinedRoom: async () =>
    axios
      .get(BASE)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  getRoom: async (roomId) =>
    axios
      .get(BASE + "/" + roomId)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  createRoom: async (name, file) =>
    axios
      .post(
        BASE,
        { name, avatarFile: file },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),

  updateRoom: async (roomId, name, file) =>
    axios
      .put(
        BASE + "/" + roomId,
        { name, avatarFile: file },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),

  deleteRoom: async (roomId) =>
    axios
      .delete(BASE + "/" + roomId)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  getActivitiesOfRoom: async (roomId) =>
    axios
      .get(BASE + "/" + roomId + "/activities")
      .then((res) => res.data)
      .catch((e) => e.response.data),

  createChat: async (roomId, content, file) =>
    axios
      .post(
        BASE + "/" + roomId + "/chat",
        { content, file },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),

  updateChat: async (id, content) =>
    axios
      .put(
        BASE + "/chat/" + id,
        { content },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),

  removeChat: async (id) =>
    axios
      .delete(BASE + "/chat/" + id)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  inviteJoinRoom: async (roomId, usersId) =>
    axios
      .post(
        `${BASE}/${roomId}/invite`,
        { usersId },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),

  removeMember: async (memberId) =>
    axios
      .delete(BASE + "/kick?memberId=" + memberId)
      .then((res) => res.data)
      .catch((e) => e.response.data),
  quitRoom: async (roomId) =>
    axios
      .delete(`${BASE}/${roomId}/quit`)
      .then((res) => res.data)
      .catch((e) => e.response.data),
};

export const {
  getJoinedRoom,
  getRoom,
  createRoom,
  updateRoom,
  deleteRoom,
  getActivitiesOfRoom,
  createChat,
  updateChat,
  removeChat,
  inviteJoinRoom,
  removeMember,
  quitRoom,
} = RoomService;

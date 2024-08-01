import axios from "axios";

const BASE = "api/v1/conversations";

const ConversationService = {
  getConversationList: async (userId) =>
    axios
      .get(`${BASE}?userId=${userId}`)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  getConversation: async (id) =>
    axios
      .get(`${BASE}/${id}`)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  createConversation: async (receiverId, content, file) =>
    axios
      .post(
        BASE,
        { receiverId, content, file },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),

  updateConversation: async (id, content) =>
    axios
      .put(
        `${BASE}/${id}`,
        { content },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),

  removeConversation: async (id) =>
    axios
      .delete(`${BASE}/${id}`)
      .then((res) => res.data)
      .catch((e) => e.response.data),
};

export const {
  getConversationList,
  getConversation,
  createConversation,
  updateConversation,
  removeConversation,
} = ConversationService;

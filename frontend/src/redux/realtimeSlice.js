import { createAsyncThunk, createSlice, isPending } from "@reduxjs/toolkit";
import store from "./store";
import {
  getFriendList,
  getInvitations,
  responseInvitation,
} from "../api/FriendService";
import {
  createConversation,
  getConversationList,
  removeConversation,
  updateConversation,
} from "../api/ConversationService";
import { socket } from "../socket";
import { getUserInfo } from "../api/UserService";

const realtimeSlice = createSlice({
  name: "realtime",
  initialState: {
    invitations: [],
    realtimeFriends: [], // {...user, realtimeStatus}
    newInvitationNumber: 0,
    conversation: {
      currentUser: null,
      currentMessage: null,
      messageList: [],
      isLoading: false,
      action: "CREATE",
    },
    isLoading: true,
  },
  reducers: {
    setNewInvitationNumber: (state, action) => {
      state.newInvitationNumber = action.payload;
    },
    setConversationAction: (state, action) => {
      state.conversation.action = action.payload;
    },
    setCurrentMessage: (state, action) => {
      state.conversation.currentMessage = action.payload;
    },
    setInvitationStatus: (state, action) => {
      const invite = state.invitations.find(
        (item) => item.id === action.payload.inviteId
      );
      invite.status = action.payload.status;
    },
    updateFriendStatus: (state, action) => {
      const index = state.realtimeFriends.findIndex(
        (item) => action.payload.user.id === item.id
      );
      if (index != -1) {
        state.realtimeFriends[index].realtimeStatus = action.payload.status;
        if (
          state.conversation.currentUser &&
          state.conversation.currentUser.id === action.payload.user.id
        )
          state.conversation.currentUser = state.realtimeFriends[index];
      }
    },
    // action.payload : list id các friend online
    setOnlineFriend: (state, action) => {
      state.realtimeFriends.forEach(
        (item) =>
          (item.realtimeStatus = action.payload.includes(item.id)
            ? "ONLINE"
            : "OFFLINE")
      );
      state.conversation.currentUser = state.realtimeFriends.find(
        (item) => item.id === state.conversation.currentUser?.id
      );
      state.isLoading = false;
    },
    setCurrentConversationUser: (state, action) => {
      state.conversation.currentUser = state.realtimeFriends.find(
        (item) => item.id === action.payload
      );
    },
    setNewMessage: (state, action) => {
      const newMessage = action.payload;
      if (newMessage.senderId === state.conversation.currentUser.id) {
        state.conversation.messageList.unshift(newMessage);
      }
    },
    setUpdateMessage: (state, action) => {
      const updatedMessage = action.payload;
      if (
        state.conversation.currentUser &&
        updatedMessage.senderId === state.conversation.currentUser.id
      ) {
        const index = state.conversation.messageList.findIndex(
          (item) => item.id === updatedMessage.id
        );
        if (index != -1) state.conversation.messageList[index] = updatedMessage;
      }
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(prepareRealtimeDataThunk.fulfilled, (state, action) => {
        const [invitationResult, friendResult] = action.payload;
        state.invitations = invitationResult.data.map((item) => ({
          ...item,
          status: "idle",
        }));
        state.realtimeFriends = friendResult.data;
        state.newInvitationNumber = invitationResult.data.length;
        socket.emit(
          "FILTER ONLINE FRIEND",
          friendResult.data.map((item) => item.id)
        );
      })
      .addCase(responseInvitationThunk.fulfilled, (state, action) => {
        console.log(action.payload.message);
      })
      .addCase(getConversationThunk.fulfilled, (state, action) => {
        if (Array.isArray(action.payload)) {
          const [userResult, conversationResult] = action.payload;
          const friend = state.realtimeFriends.find(
            (item) => item.id === userResult.data.id
          );
          if (friend) {
            state.conversation.currentUser = friend;
          } else {
            state.conversation.currentUser = {
              ...userResult.data,
              realtimeStatus: "STRANGE",
            };
            state.realtimeFriends.unshift({
              ...userResult.data,
              realtimeStatus: "STRANGE",
            });
          }
          state.conversation.messageList = conversationResult.data;
        } else {
          console.log(action.payload.message);
        }
      })
      .addCase(createConversationThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          state.conversation.messageList.unshift(action.payload.data);
          state.conversation.action = "CREATE";
          socket.emit("NEW MESSAGE", action.payload.data);
        } else {
          alert(action.payload.message);
        }
      })
      .addCase(editConversationThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          const index = state.conversation.messageList.findIndex(
            (item) => item.id === action.payload.data.id
          );
          if (index !== -1) {
            state.conversation.messageList[index] = action.payload.data;
          }
          socket.emit("UPDATE MESSAGE", action.payload.data);
        } else {
          console.log(action.payload.message);
          return;
        }
      })
      .addCase(removeConversationThunk.fulfilled, (state, action) => {
        if (!action.payload.isSuccess) {
          console.log(action.payload.message);
          return;
        }
        const index = state.conversation.messageList.findIndex(
          (item) => item.id === action.payload.data.id
        );
        if (index !== -1) {
          state.conversation.messageList[index] = action.payload.data;
          socket.emit("UPDATE MESSAGE", action.payload.data);
        }
      });
  },
});

export const prepareRealtimeDataThunk = createAsyncThunk(
  "realtime/prepareRealtimeDataThunk",
  async () => {
    const user = store.getState().auth.user?.user;
    return await Promise.all([getInvitations(), getFriendList(user.id)]);
  }
);

export const responseInvitationThunk = createAsyncThunk(
  "realtime/responseInvitationThunk",
  async ({ invitationId, isAccept }) => {
    const res = await responseInvitation(invitationId, isAccept);
    return res;
  }
);

export const getConversationThunk = createAsyncThunk(
  "realtime/getConversationThunk",
  async (userId) => {
    return await Promise.all([
      getUserInfo(userId),
      getConversationList(userId),
    ]).catch((e) => e.response.data);
  }
);

export const createConversationThunk = createAsyncThunk(
  "realtime/createConversationThunk",
  async ({ receiverId, content, file }) => {
    return await createConversation(receiverId, content, file);
  }
);

export const editConversationThunk = createAsyncThunk(
  "realtime/editConversationThunk",
  async ({ id, content }) => {
    return await updateConversation(id, content);
  }
);

export const removeConversationThunk = createAsyncThunk(
  "realtime/removeConversationThunk",
  async (id) => {
    return await removeConversation(id);
  }
);

export const {
  setNewInvitationNumber,
  setInvitationStatus,
  setOnlineFriend,
  setCurrentConversationUser,
  setConversationAction,
  setCurrentMessage,
  updateFriendStatus,
  setNewMessage,
  setUpdateMessage,
} = realtimeSlice.actions;
export default realtimeSlice.reducer;

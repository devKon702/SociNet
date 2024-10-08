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
import { showSnackbar } from "./snackbarSlice";
import { setFriendStatus } from "./personalSlice";

const realtimeSlice = createSlice({
  name: "realtime",
  initialState: {
    invitations: [],
    realtimeFriends: [], // {...user, realtimeStatus, hasUnreadMessage}
    newInvitationNumber: 0,
    conversation: {
      currentUser: null,
      currentMessage: null,
      messageList: [],
      isLoading: false,
      action: "CREATE",
      filter: "",
    },
    isLoading: true,
  },
  reducers: {
    setNewInvitationNumber: (state, action) => {
      state.newInvitationNumber = action.payload;
    },
    setNewInvitation: (state, action) => {
      if (!state.invitations.find((item) => item.id == action.payload.id)) {
        state.invitations.unshift(action.payload);
        state.newInvitationNumber += 1;
      }
    },
    setConversationAction: (state, action) => {
      state.conversation.action = action.payload;
    },
    setCurrentMessage: (state, action) => {
      state.conversation.currentMessage = action.payload;
    },
    setConversationFilter: (state, action) => {
      state.conversation.filter = action.payload;
    },
    setInvitationStatus: (state, action) => {
      const invite = state.invitations.find(
        (item) => item.id === action.payload.inviteId
      );
      invite ? (invite.status = action.payload.status) : null;
    },
    updateFriendStatus: (state, action) => {
      if (!action.payload.user) return;
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
    setFriendsStatus: (state, action) => {
      state.realtimeFriends.forEach((item) => {
        item.realtimeStatus = action.payload.onlineIdList.includes(item.id)
          ? "ONLINE"
          : "OFFLINE";
        item.hasUnreadMessage = action.payload.unreadList.includes(item.id);
      });
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
    clearConversation: (state) => {
      state.conversation = {
        currentUser: null,
        currentMessage: null,
        messageList: [],
        isLoading: false,
        action: "CREATE",
        filter: "",
      };
    },
    setNewMessage: (state, action) => {
      const newMessage = action.payload;
      if (
        newMessage.senderId === state.conversation.currentUser.id ||
        newMessage.receiverId === state.conversation.currentUser.id
      ) {
        state.conversation.messageList.unshift(newMessage);
      }
    },
    setHasUnreadStatus: (state, { payload: { sender, status } }) => {
      const index = state.realtimeFriends.findIndex(
        (item) => item.id === sender.id
      );

      // Tồn tại thì cập nhật trạng thái
      if (index != -1) {
        state.realtimeFriends[index].hasUnreadMessage = status;
      } // Chưa thì thêm vào realtime Friend List
      else {
        state.realtimeFriends.unshift({
          ...sender,
          realtimeStatus: "STRANGE",
          hasUnreadMessage: status,
        });
      }
    },
    setUpdateMessage: (state, action) => {
      const updatedMessage = action.payload;
      // if (
      //   state.conversation.currentUser &&
      //   updatedMessage.senderId === state.conversation.currentUser.id
      // ) {
      const index = state.conversation.messageList.findIndex(
        (item) => item.id === updatedMessage.id
      );
      if (index != -1) state.conversation.messageList[index] = updatedMessage;
      // }
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
          "FILTER STATUS FRIEND",
          friendResult.data.map((item) => item.id)
        );
      })
      .addCase(getRealtimeFriendsThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          state.realtimeFriends = action.payload.data;
          socket.emit(
            "FILTER STATUS FRIEND",
            action.payload.data.map((item) => item.id)
          );
        }
      })
      .addCase(responseInvitationThunk.fulfilled, (state, action) => {
        console.log(action.payload.message);
      })
      .addCase(getConversationThunk.fulfilled, (state, action) => {
        if (
          Array.isArray(action.payload) &&
          action.payload.every((item) => item.isSuccess)
        ) {
          const [userResult, conversationResult] = action.payload;
          const index = state.realtimeFriends.findIndex(
            (item) => item.id === userResult.data.id
          );
          if (index != -1) {
            state.realtimeFriends[index].hasUnreadMessage = false;
            state.conversation.currentUser = state.realtimeFriends[index];
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
          socket.emit("READ CONVERSATION", userResult.data.id);
        }
      })
      .addCase(createConversationThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          // state.conversation.messageList.unshift(action.payload.data);
          state.conversation.action = "CREATE";
          socket.emit("NEW MESSAGE", action.payload.data);
        }
      })
      .addCase(editConversationThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          // const index = state.conversation.messageList.findIndex(
          //   (item) => item.id === action.payload.data.id
          // );
          // if (index !== -1) {
          //   state.conversation.messageList[index] = action.payload.data;
          // }
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
        // const index = state.conversation.messageList.findIndex(
        //   (item) => item.id === action.payload.data.id
        // );
        // if (index !== -1) {
        //   state.conversation.messageList[index] = action.payload.data;
        // }
        socket.emit("UPDATE MESSAGE", action.payload.data);
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

export const getRealtimeFriendsThunk = createAsyncThunk(
  "realtime/getFriendListThunk",
  async () => {
    const user = store.getState().auth.user?.user;
    return await getFriendList(user.id);
  }
);

export const responseInvitationThunk = createAsyncThunk(
  "realtime/responseInvitationThunk",
  async ({ invitation, isAccept }, { dispatch }) => {
    const res = await responseInvitation(invitation.id, isAccept);
    if (res.isSuccess) {
      dispatch(getRealtimeFriendsThunk());
      if (store.getState().personal.user?.id == invitation.user.id) {
        dispatch(setFriendStatus(isAccept ? "FRIEND" : "NO"));
      }
      socket.emit("RESPONSE INVITATION", invitation.user.id, isAccept);
    } else {
      if (res.message == "ACCEPTED INVITATION" && !isAccept) {
        dispatch(
          setInvitationStatus({ inviteId: invitation.id, status: "accepted" })
        );
        dispatch(
          showSnackbar({
            message: "Lời mời đã được chấp nhận, không thể từ chối",
            type: "error",
          })
        );
      } else if (res.message == "INVITATION NOT EXIST" && isAccept) {
        dispatch(
          setInvitationStatus({ inviteId: invitation.id, status: "rejected" })
        );
        dispatch(
          showSnackbar({
            message: "Lời mời đã được từ chối",
            type: "error",
          })
        );
      }
    }
    return res;
  }
);

export const getConversationThunk = createAsyncThunk(
  "realtime/getConversationThunk",
  async (userId, { dispatch }) => {
    return await Promise.all([getUserInfo(userId), getConversationList(userId)])
      .then((res) => {
        const failResponse = res.find((item) => !item.isSuccess);
        if (failResponse) {
          switch (failResponse.message) {
            case "USER NOT FOUND":
              dispatch(
                showSnackbar({
                  message: "Người dùng không tồn tại",
                  type: "error",
                })
              );
              break;
            default:
              dispatch(
                showSnackbar({
                  message: "Mở đoạn tin nhắn thất bại",
                  type: "error",
                })
              );
          }
        }
        return res;
      })
      .catch((e) => {
        return e.response.data;
      });
  }
);

export const createConversationThunk = createAsyncThunk(
  "realtime/createConversationThunk",
  async ({ receiverId, content, file }, { dispatch }) => {
    const res = await createConversation(receiverId, content, file);
    if (!res.isSuccess) {
      let message = "";
      switch (res.message) {
        case "USER NOT EXIST":
          message = "Người dùng không tồn tại";
          break;
        case "UNSUPPORTED FILE":
          message = "Định dạng file không hỗ trợ";
          break;
        case "OVERSIZE FILE":
          message = "File quá lớn";
          break;
        case "CANNOT CREATE":
          message = "Không thể tạo gửi tin nhắn này";
          break;
        default:
          message = "Tạo tin nhắn thất bại";
      }
      dispatch(showSnackbar({ message, type: "error" }));
    }
    return res;
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
  setNewInvitation,
  setInvitationStatus,
  setFriendsStatus,
  setCurrentConversationUser,
  setConversationAction,
  setCurrentMessage,
  setConversationFilter,
  updateFriendStatus,
  setNewMessage,
  setHasUnreadStatus,
  setUpdateMessage,
  clearConversation,
} = realtimeSlice.actions;
export default realtimeSlice.reducer;

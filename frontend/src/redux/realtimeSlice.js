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
import {
  createRoom,
  getActivitiesOfRoom,
  getJoinedRoom,
  getRoom,
  inviteJoinRoom,
  updateRoom,
} from "../api/RoomService";

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
    realtimeRooms: [],
    roomActivity: {
      currentRoom: null,
      activities: [],
      currentMessage: null,
      action: "CREATE",
      fetchStatus: "",
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
    setRoomAction: (state, action) => {
      state.roomActivity.action = action.payload;
    },
    removeRoom: (state, action) => {
      state.realtimeRooms = state.realtimeRooms.filter(
        (item) => item.id != action.payload
      );
    },
    setCurrentRoom: (state, action) => {
      state.roomActivity.currentRoom = {
        ...state.roomActivity.currentRoom,
        ...action.payload,
      };
      state.realtimeRooms.forEach((item) => {
        if (item.id == action.payload.id) {
          item.hasUnreadMessage = false;
        }
      });
    },
    clearRoomActivity: (state) => {
      state.roomActivity = {
        currentRoom: null,
        activities: [],
        currentMessage: null,
        action: "CREATE",
        fetchStatus: "",
      };
    },
    setCurrentRoomMessage: (state, action) => {
      state.roomActivity.currentMessage = action.payload;
    },
    addNewRoomActivity: (state, action) => {
      state.roomActivity.activities.unshift(action.payload);
    },
    updateRoomMessage: (state, action) => {
      const message = action.payload;
      const index = state.roomActivity.activities.findIndex(
        (item) => item.id === message.id
      );
      if (index != -1) {
        state.roomActivity.activities[index] = message;
      }
    },
    setRoomReadStatus: (state, action) => {
      const index = state.realtimeRooms.findIndex(
        (room) => room.id == action.payload.roomId
      );
      if (index != -1) {
        state.realtimeRooms[index]["hasUnread"] = action.payload.hasUnread;
      }
    },
    newRealtimeRoomMessage: (state, action) => {
      const { roomId, activity } = action.payload;
      if (state.roomActivity.currentRoom?.id == roomId) {
        state.roomActivity.activities.unshift(activity);
      } else {
        state.realtimeRooms.forEach((item) => {
          if (item.id == roomId) item.hasUnreadMessage = true;
        });
      }
    },
    newRealtimeRoomMember: (state, action) => {
      const { roomId, activity } = action.payload;
      if (state.roomActivity.currentRoom?.id == roomId) {
        state.roomActivity.activities.unshift(activity);
        state.roomActivity.currentRoom.members.push(activity.receiver);
      }
    },
    newRealtimeJoinedRoom: (state, action) => {
      const room = action.payload;
      // if room have not been in the realtimeRooms yet
      if (!state.realtimeRooms.some((item) => item.id == room.id)) {
        state.realtimeRooms.unshift(room);
      }
    },
    newRealtimeMemberQuitActivity: (state, action) => {
      const { roomId, activity } = action.payload;
      // if room is now currentRoom
      if (state.roomActivity.currentRoom?.id == roomId) {
        state.roomActivity.currentRoom.members =
          state.roomActivity.currentRoom.members.filter(
            (member) => member.user.id != activity.sender.id
          );

        state.roomActivity.activities.unshift(activity);
      }
    },
    disableRoom: (state, action) => {
      const roomId = action.payload;
      if (state.roomActivity.currentRoom?.id == roomId) {
        state.roomActivity.currentRoom.isActive = false;
      }
    },
    setUnreadRoom: (state, action) => {
      const unreadRoomIdList = action.payload;
      // if each room exist in the unread array -> hasUnreadMessage = true
      state.realtimeRooms.forEach((item) => {
        if (unreadRoomIdList.some((roomId) => roomId == item.id)) {
          item.hasUnreadMessage = true;
        }
      });
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(prepareRealtimeDataThunk.fulfilled, (state, action) => {
        const [invitationResult, friendResult, joinedRoomResult] =
          action.payload;
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
        socket.emit("GET ROOM STATUS");
        state.realtimeRooms = joinedRoomResult.data;
        socket.emit(
          "JOIN MULTI ROOM",
          joinedRoomResult.data.map((room) => room.id)
        );
        state.isLoading = false;
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
      })
      .addCase(getRoomThunk.fulfilled, (state, action) => {
        if (
          Array.isArray(action.payload) &&
          action.payload.every((item) => item.isSuccess)
        ) {
          const [roomResult, activityResult] = action.payload;
          state.roomActivity.currentRoom = roomResult.data;
          state.roomActivity.activities = activityResult.data;
          socket.emit("READ ROOM MESSAGE", roomResult.data.id);
          state.realtimeRooms.forEach((item, index, arr) => {
            if (item.id == roomResult.data.id) arr[index] = roomResult.data;
          });
        }
      })
      .addCase(createRoomThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          state.realtimeRooms.push(action.payload.data);
          location.replace("/conversation/room/" + action.payload.data.id);
        }
      })
      .addCase(inviteJoinRoomThunk.fulfilled, (state, action) => {
        if (action.payload.isSuccess) {
          action.payload.data.forEach((activity) => {
            state.roomActivity.currentRoom.members.push({
              user: activity.receiver,
              isAdmin: false,
            });
            state.roomActivity.activities.unshift(activity);
            socket.emit(
              "INVITE TO ROOM",
              state.roomActivity.currentRoom.id,
              activity
            );
          });
        }
      })
      .addCase(updateRoomThunk.pending, (state, action) => {
        state.roomActivity.fetchStatus = "UPDATING";
      })
      .addCase(updateRoomThunk.fulfilled, (state, action) => {
        state.roomActivity.fetchStatus = "";
        if (action.payload.isSuccess) {
          state.roomActivity.currentRoom = action.payload.data;
        }
      });
  },
});

export const prepareRealtimeDataThunk = createAsyncThunk(
  "realtime/prepareRealtimeDataThunk",
  async () => {
    const user = store.getState().auth.user?.user;
    return await Promise.all([
      getInvitations(),
      getFriendList(user.id),
      getJoinedRoom(),
    ]);
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

export const getRoomThunk = createAsyncThunk(
  "realtime/getRoomThunk",
  async (id, { dispatch }) => {
    return await Promise.all([getRoom(id), getActivitiesOfRoom(id)])
      .then((res) => {
        const failResponse = res.find((item) => !item.isSuccess);
        if (failResponse) {
          switch (failResponse.message) {
            case "ROOM NOT EXIST":
              dispatch(
                showSnackbar({
                  message: "Phòng chat không tồn tại",
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

export const createRoomThunk = createAsyncThunk(
  "realtime/createRoomThunk",
  async ({ name, file }, { dispatch }) => {
    const res = await createRoom(name, file);
    if (res.isSuccess) {
      dispatch(
        showSnackbar({
          message: "Tạo nhóm thành công",
          type: "success",
        })
      );
    } else {
      dispatch(
        showSnackbar({
          message: "Tạo nhóm thất bại",
          type: "error",
        })
      );
    }
    return res;
  }
);

export const inviteJoinRoomThunk = createAsyncThunk(
  "realtime/inviteJoinRoomThunk",
  async ({ roomId, userIdList }, { dispatch }) => {
    console.log(userIdList);
    const res = await inviteJoinRoom(roomId, userIdList);

    if (res.isSuccess) {
      dispatch(showSnackbar({ message: "Mời thành công", type: "success" }));
    } else {
      dispatch(showSnackbar({ message: "Mời thất bại", type: "error" }));
      console.log(res);
    }
    return res;
  }
);

export const updateRoomThunk = createAsyncThunk(
  "realtime/updateRoomThunk",
  async ({ roomId, name, file }, { dispatch }) => {
    const res = await updateRoom(roomId, name, file);
    if (res.isSuccess) {
      dispatch(
        showSnackbar({ message: "Cập nhật thành công", type: "success" })
      );
    } else {
      dispatch(showSnackbar({ message: "Cập nhật thất bại", type: "error" }));
      console.log(res);
    }
    return res;
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
  setRoomAction,
  removeRoom,
  setCurrentRoom,
  clearRoomActivity,
  setCurrentRoomMessage,
  addNewRoomActivity,
  updateRoomMessage,
  newRealtimeRoomMessage,
  setRoomReadStatus,
  newRealtimeRoomMember,
  newRealtimeJoinedRoom,
  newRealtimeMemberQuitActivity,
  disableRoom,
  setUnreadRoom,
} = realtimeSlice.actions;
export default realtimeSlice.reducer;

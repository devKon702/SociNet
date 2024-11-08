import { io } from "socket.io-client";
import store from "./redux/store";
import {
  setHasUnreadStatus,
  setNewInvitation,
  setNewMessage,
  setFriendsStatus,
  setUpdateMessage,
  updateFriendStatus,
  getRealtimeFriendsThunk,
  newRealtimeRoomMessage,
  updateRoomMessage,
  newRealtimeRoomMember,
  newRealtimeJoinedRoom,
  newRealtimeMemberQuitActivity,
  disableRoom,
  setUnreadRoom,
} from "./redux/realtimeSlice";
import { signout } from "./redux/authSlice";
import { setFriendStatus } from "./redux/personalSlice";
import { removeAllListenersExcept } from "./helper";
import { getUserInfo } from "./api/UserService";
import { getRoom } from "./api/RoomService";

const USER_URL = "http://localhost:3000";
const ADMIN_URL = "http://localhost:3000/admin";

export const socket = io(USER_URL, {
  autoConnect: false,
});
export const socketAdmin = io(ADMIN_URL, {
  autoConnect: false,
});

const onConnect = () => {
  socket.on("FILTER STATUS FRIEND", (onlineIdList, unreadList) => {
    store.dispatch(setFriendsStatus({ onlineIdList, unreadList }));
  });

  socket.on("NEW ONLINE USER", (user) => {
    store.dispatch(updateFriendStatus({ user, status: "ONLINE" }));
  });

  socket.on("NEW OFFLINE USER", (user) => {
    store.dispatch(updateFriendStatus({ user, status: "OFFLINE" }));
  });

  socket.on("NEW MESSAGE", (conversation) => {
    const currentUser = store.getState().auth.user.user;
    const currentChatUser = store.getState().realtime.conversation.currentUser;
    // Đang mở chat với người đó -> thông báo socket đã đọc tin nhắn
    if (
      (conversation.senderId == currentUser.id &&
        conversation.receiverId == currentChatUser?.id) ||
      (conversation.senderId == currentChatUser?.id &&
        conversation.receiverId == currentUser.id)
    ) {
      store.dispatch(setNewMessage(conversation));
      socket.emit("READ CONVERSATION", currentChatUser.id);
    }
    // Đang không mở chat và là người nhận tin nhắn -> thông báo tin nhắn mới
    else if (
      conversation.receiverId == currentUser.id &&
      (!currentChatUser || conversation.senderId != currentChatUser.id)
    ) {
      getUserInfo(conversation.senderId).then((res) => {
        if (res.isSuccess) {
          store.dispatch(
            setHasUnreadStatus({ sender: res.data, status: true })
          );
        }
      });
    }
  });

  socket.on("UPDATE MESSAGE", (conversation) => {
    store.dispatch(setUpdateMessage(conversation));
  });

  socket.on("FORCE LOGOUT", () => {
    store.dispatch(signout());
    location.reload();
  });

  socket.on("NEW INVITATION", (invitation) => {
    store.dispatch(setNewInvitation(invitation));

    const personal = store.getState().personal;
    if (personal.user?.id == invitation.user.id) {
      store.dispatch(setFriendStatus("INVITED"));
    }
  });

  socket.on("RESPONSE INVITATION", (userId, isAccept) => {
    if (isAccept) store.dispatch(getRealtimeFriendsThunk());

    if (store.getState().personal.user?.id == userId) {
      store.dispatch(setFriendStatus(isAccept ? "FRIEND" : "NO"));
    }
  });

  socket.on("GET ROOM STATUS", (unreadRoomIdList) => {
    store.dispatch(setUnreadRoom(unreadRoomIdList));
  });

  socket.on("NEW ROOM MESSAGE", (roomId, activity) => {
    store.dispatch(newRealtimeRoomMessage({ roomId, activity }));
  });

  socket.on("UPDATE ROOM MESSAGE", (roomId, activity) => {
    store.dispatch(updateRoomMessage(activity));
  });

  socket.on("NEW MEMBER", (roomId, activity) => {
    store.dispatch(newRealtimeRoomMember({ roomId, activity }));
  });

  socket.on("INVITE TO ROOM", (roomId) => {
    getRoom(roomId).then((res) => {
      if (res.isSuccess) {
        store.dispatch(newRealtimeJoinedRoom(res.data));
      } else {
        console.log(res);
      }
    });
    socket.emit("JOIN ROOM", roomId);
  });

  socket.on("MEMBER QUIT", (roomId, activity) => {
    store.dispatch(newRealtimeMemberQuitActivity({ roomId, activity }));
  });

  socket.on("DISABLE ROOM", (roomId) => {
    store.dispatch(disableRoom(roomId));
  });

  socket.on("disconnect", () => [removeAllListenersExcept(socket, "connect")]);
};

socket.on("connect", onConnect);

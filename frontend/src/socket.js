import { io } from "socket.io-client";
import store from "./redux/store";
import {
  setNewMessage,
  setOnlineFriend,
  setUpdateMessage,
  updateFriendStatus,
} from "./redux/realtimeSlice";

const USER_URL = "http://localhost:3000";
const ADMIN_URL = "http://localhost:3000";

export const socket = io(USER_URL, {
  autoConnect: false,
});
export const socketAdmin = io(ADMIN_URL, {
  autoConnect: false,
});

const onConnect = () => {
  socket.on("FILTER ONLINE FRIEND", (onlineIdList) => {
    store.dispatch(setOnlineFriend(onlineIdList));
  });

  socket.on("NEW ONLINE USER", (user) => {
    store.dispatch(updateFriendStatus({ user, status: "ONLINE" }));
  });

  socket.on("NEW OFFLINE USER", (user) => {
    store.dispatch(updateFriendStatus({ user, status: "OFFLINE" }));
  });

  socket.on("NEW MESSAGE", (conversation) => {
    store.dispatch(setNewMessage(conversation));
  });

  socket.on("UPDATE MESSAGE", (conversation) => {
    store.dispatch(setUpdateMessage(conversation));
  });
};

socket.on("connect", onConnect);

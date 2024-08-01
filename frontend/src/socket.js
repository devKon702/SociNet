import { io } from "socket.io-client";
import store from "./redux/store";
import { setOnlineFriend } from "./redux/realtimeSlice";

const URL = "http://localhost:3000";

export const socket = io(URL, {
  autoConnect: false,
});

const onConnect = () => {
  socket.on("FILTER ONLINE FRIEND", (onlineIdList) => {
    store.dispatch(setOnlineFriend(onlineIdList));
  });
};

socket.on("connect", onConnect);

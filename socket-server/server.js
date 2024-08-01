const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const PORT = 3000;

// const app = express();
// const server = http.createServer(app);
const io = new Server({
  cors: {
    origin: "http://localhost:5173",
  },
});
io.listen(3000);
const socketManager = {}; // {userId: socket.id}
const userIo = io.of("/");
const adminIo = io.of("/admin");

userIo.on("connection", (socket) => {
  socket.on("NOTIFY ONLINE", (user) => {
    console.log("User " + user.name + " has just connected");
    socketManager[user.id] = socket.id;
    userIo.emit("NEW ONLINE USER", user);
  });

  socket.on("NOTIFY OFFLINE", (user) => {
    socketManager[user.id] = null;
    userIo.emit("NEW OFFLINE USER", user);
  });

  socket.on("FILTER ONLINE FRIEND", (idList) => {
    socket.emit(
      "FILTER ONLINE FRIEND",
      idList.filter((item) => Object.keys(socketManager).includes(item))
    );
  });

  socket.on("NEW MESSAGE", (conversation) => {
    userIo
      .to(socketManager[conversation.receiverId])
      .emit("NEW MESSAGE", conversation);
  });

  socket.on("UPDATE MESSAGE", (conversation) => {
    userIo
      .to(socketManager[conversation.receiverId])
      .emit("UPDATE MESSAGE", conversation);
  });

  socket.on("NEW INVITATION", (invitation, receiverId) => {
    userIo.to(socketManager[receiverId]).emit("NEW INVITATION", invitation);
  });
});

adminIo.on("connection", (socket) => {
  console.log("ADMIN has just connected");

  socket.on("FORCE LOGOUT", (userId) => {
    userIo.to(socketManager[userId].emit("FORCE LOGOUT"));
  });
});

// app.listen(PORT, () => {
//   console.log(`Express server running at http://localhost:${PORT}/`);
// });

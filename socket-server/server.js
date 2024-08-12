const express = require("express");
const http = require("http");
const { connect } = require("http2");
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
const connectedUsers = {}; // {user.id: {newConversation: [sender.id,...], isOnline: boolean}}
const userIo = io.of("/");
const adminIo = io.of("/admin");

userIo.on("connection", (socket) => {
  let socketUser = null;

  socket.on("NOTIFY ONLINE", (user) => {
    console.log("User " + user.name + " has just connect");
    socketUser = user;
    // connectedUsers[user.id] = socket.id;
    // join room with name {user.id} for multiple tab of an user
    socket.join(user.id);
    // if user is new, add info, if not, update online status
    connectedUsers[user.id]
      ? (connectedUsers[user.id].isOnline = true)
      : (connectedUsers[user.id] = { newConversation: [], isOnline: true });
    socket.broadcast.emit("NEW ONLINE USER", user);
  });

  socket.on("disconnect", () => {
    console.log("User " + socketUser?.name + " has just disconnect");
    // delete connectedUsers[socketUser?.id];
    // update online status
    socketUser ? (connectedUsers[socketUser.id].isOnline = false) : null;
    socket.broadcast.emit("NEW OFFLINE USER", socketUser);
  });

  socket.on("FILTER STATUS FRIEND", (idList) => {
    // List of online users id
    const onlineList = Object.keys(connectedUsers).filter(
      (key) => connectedUsers[key].isOnline
    );
    const unreadSet = new Set(connectedUsers[socketUser.id].newConversation);
    socket.emit(
      "FILTER STATUS FRIEND",
      idList.filter((item) => onlineList.includes("" + item)),
      [...unreadSet]
    );
  });

  socket.on("NEW MESSAGE", (conversation) => {
    // userIo
    //   .to(connectedUsers[conversation.receiverId])
    //   .emit("NEW MESSAGE", conversation);
    userIo
      .to(conversation.receiverId)
      .to(socketUser.id)
      .emit("NEW MESSAGE", conversation);
    // Add to new conversation list of receiver
    connectedUsers[conversation.receiverId]
      ? connectedUsers[conversation.receiverId].newConversation.push(
          socketUser.id
        )
      : (connectedUsers[conversation.receiverId] = {
          newConversation: [socketUser.id],
          isOnline: false,
        });
  });

  socket.on("UPDATE MESSAGE", (conversation) => {
    // userIo
    //   .to(connectedUsers[conversation.receiverId])
    //   .emit("UPDATE MESSAGE", conversation);
    userIo
      .to(conversation.receiverId)
      .to(socketUser.id)
      .emit("UPDATE MESSAGE", conversation);
  });

  // Event to return an array of unread message's sender.id
  socket.on("GET UNREAD CONVERSATION", () => {
    const idSet = new Set(connectedUsers[socketUser.id].newConversation);
    socket.emit("UNREAD CONVERSATION", [...idSet]);
  });

  socket.on("READ CONVERSATION", (userId) => {
    connectedUsers[socketUser.id].newConversation = connectedUsers[
      socketUser.id
    ].newConversation.filter((item) => item != userId);
  });

  socket.on("NEW INVITATION", (invitation, sender, receiverId) => {
    invitation.user = sender;
    // userIo.to(connectedUsers[receiverId]).emit("NEW INVITATION", invitation);
    userIo.to(receiverId).emit("NEW INVITATION", invitation);
  });

  socket.on("RESPONSE INVITATION", (senderId, isAccept) => {
    console.log({ isAccept });

    userIo.to(senderId).emit("RESPONSE INVITATION", socketUser.id, isAccept);
  });
});

adminIo.on("connection", (socket) => {
  console.log("ADMIN has just connected");

  socket.on("FORCE LOGOUT", (userId) => {
    userIo.to(userId).emit("FORCE LOGOUT");
  });
});

// app.listen(PORT, () => {
//   console.log(`Express server running at http://localhost:${PORT}/`);
// });

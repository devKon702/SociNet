const express = require("express");
require("dotenv").config();
const http = require("http");
const { connect } = require("http2");
const { Server } = require("socket.io");
const { connection: mysqlConnection } = require("./database");
const PORT = 3000;
// const app = express();
// const server = http.createServer(app);
const io = new Server({
  cors: {
    origin: process.env.FRONTEND_ORIGIN,
  },
});

mysqlConnection.connect((err) => {
  if (err) throw err;
  mysqlConnection.query(
    `SELECT * FROM socialnet.room_member`,
    (error, results, fields) => {
      // Hiển thị kết quả của truy vấn
      results.forEach((item) => {
        if (!roomManager[item.room_id]) roomManager[item.room_id] = {};
        roomManager[item.room_id][item.user_id] = [];
      });
      // Đóng kết nối
      // connection.end();
    }
  );
});

io.listen(3000);
const connectedUsers = {}; // {user.id: {newConversation: [sender.id,...], isOnline: boolean}}
const roomManager = {}; // {room.id: {user.id: [activity.id]}}
const userIo = io.of("/");
const adminIo = io.of("/admin");

userIo.on("connection", (socket) => {
  let socketUser = null;

  socket.on("NOTIFY ONLINE", (user) => {
    console.log("User " + user.name + " has just connect");
    socketUser = user;
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
    socketUser ? socket.leave(socketUser.id) : null;
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
    userIo.to(senderId).emit("RESPONSE INVITATION", socketUser.id, isAccept);
  });

  socket.on("GET ROOM STATUS", () => {
    const joinedRoomId = Object.keys(roomManager).filter(
      (roomId) => !!roomManager[roomId][socketUser.id]
    );
    const roomStatusList = joinedRoomId.map((roomId) => {
      const isUnread = roomManager[roomId][socketUser.id].length > 0;
      return { roomId, isUnread };
    });
    const unreadRoomIdList = roomStatusList
      .filter((item) => item.isUnread)
      .map((item) => item.roomId);
    socket.emit("GET ROOM STATUS", unreadRoomIdList);
  });

  socket.on("NEW ROOM", (roomId) => {
    if (roomManager[roomId] == null) {
      roomManager[roomId] = {};
      roomManager[roomId][socketUser.id] = [];
      socket.join("R" + roomId);
    }
  });

  socket.on("NEW ROOM MESSAGE", (roomId, activity) => {
    // if room info is not exist
    if (!roomManager[roomId]) {
      // Temp save activity data to room prop
      // roomManager[roomId] = { tempActivity: activity }; // Luu tam activityId
      // socket.emit("PROVIDE ROOM MEMBER", roomId);
      console.log("Room id " + roomId + " is not exist");
    }
    // have info
    else {
      // push activity to each member of room
      Object.keys(roomManager[roomId]).forEach((userId) => {
        roomManager[roomId][userId].push(activity);
      });
      socket.to(`R${roomId}`).emit("NEW ROOM MESSAGE", roomId, activity);
    }
  });

  socket.on("REGISTER ROOM", (roomId, userIdList) => {
    // if this event is after NEW ROOM MESSAGE
    const tempActivity = roomManager[roomId]?.tempActivity;
    if (tempActivity) {
      // push activity saved from last NEW ROOM MESSAGE event to each member of room
      userIdList.forEach((userId) => {
        roomManager[roomId][userId] = [tempActivity];
      });
      socket.to(`R${roomId}`).emit("NEW ROOM MESSAGE", roomId, tempActivity);
      delete roomManager[roomId].tempActivity;
    }
  });

  socket.on("UPDATE ROOM MESSAGE", (roomId, activity) => {
    socket.to(`R${roomId}`).emit("UPDATE ROOM MESSAGE", roomId, activity);
  });

  socket.on("READ ROOM MESSAGE", (roomId) => {
    if (roomManager[roomId]) {
      roomManager[roomId][socketUser.id] = [];
    } else {
      console.log("Room id " + roomId + " is not exist");
    }
  });

  socket.on("INVITE TO ROOM", (roomId, activity) => {
    socket.to(`R${roomId}`).emit("NEW MEMBER", roomId, activity);
    socket.to(activity.receiver.id).emit("INVITE TO ROOM", roomId);
    roomManager[roomId][activity.receiver.id] = [];
  });

  socket.on("JOIN ROOM", (roomId) => {
    socket.join(`R${roomId}`);
  });

  socket.on("JOIN MULTI ROOM", (roomIdList) => {
    roomIdList.forEach((roomId) => socket.join("R" + roomId));
  });

  socket.on("QUIT ROOM", (roomId, activity) => {
    socket.to(`R${roomId}`).emit("MEMBER QUIT", roomId, activity);
    socket.leave("R" + roomId);
    delete roomManager[roomId][socketUser.id];
  });

  socket.on("DISABLE ROOM", (roomId) => {
    socket.to(`R${roomId}`).emit("DISABLE ROOM", roomId);
  });

  socket.on("KICK MEMBER", (roomId, activity) => {
    socket.to(activity.receiver.id).emit("KICKED", roomId, activity);
    socket.to(`R${roomId}`).emit("MEMBER KICKED", roomId, activity);
    delete roomManager[roomId][activity.receiver.id];
  });
  socket.on("LEAVE ROOM", (roomId) => {
    socket.leave("R" + roomId);
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

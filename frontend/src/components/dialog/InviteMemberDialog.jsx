import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { realtimeSelector } from "../../redux/selectors";
import Dialog from "./Dialog";
import { inviteJoinRoomThunk, setRoomAction } from "../../redux/realtimeSlice";

const InviteMemberDialog = ({ handleClose }) => {
  const dispatch = useDispatch();
  const {
    realtimeFriends,
    roomActivity: { currentRoom },
  } = useSelector(realtimeSelector);

  function handleCheck(userId) {
    checkUsers[userId] = true;
  }

  function handleUncheck(userId) {
    delete checkUsers[userId];
  }

  function handleInvite() {
    dispatch(
      inviteJoinRoomThunk({
        roomId: currentRoom.id,
        userIdList: Object.keys(checkUsers),
      })
    );
    dispatch(setRoomAction("CREATE"));
  }

  const checkUsers = {};

  const joinableFriends = realtimeFriends.filter(
    (item) => !currentRoom?.members.some((member) => member.user.id === item.id)
  );

  return (
    <Dialog handleClose={handleClose} title="Mời tham gia">
      <>
        <div className="w-full md:w-[550px] grid grid-cols-2 p-3 gap-3">
          {joinableFriends.map((item, index) => (
            <FriendItem
              handleCheck={() => handleCheck(item.id)}
              handleUncheck={() => handleUncheck(item.id)}
              user={item}
              key={index}
            ></FriendItem>
          ))}
        </div>
        <button
          className="w-11/12 mt-auto mx-auto mb-2 p-3 rounded-lg bg-green-400 text-white font-bold"
          onClick={handleInvite}
        >
          Mời
        </button>
      </>
    </Dialog>
  );
};

const FriendItem = ({ handleCheck, handleUncheck, user }) => {
  const [isCheck, setCheck] = useState(false);
  return (
    <div className="flex items-center gap-2 bg-lightGray rounded-lg p-2">
      <div className="rounded-full overflow-hidden size-10">
        <img
          src={user.avatarUrl || "/unknown-avatar.png"}
          alt=""
          className="size-full object-cover"
        />
      </div>
      <span className="font-bold">{user.name}</span>
      {isCheck ? (
        <button
          className="text-red-400 border border-red-400 font-bold text-center rounded-md py-1 px-3 ml-auto"
          onClick={() => {
            handleUncheck();
            setCheck(false);
          }}
        >
          Hủy
        </button>
      ) : (
        <button
          className="bg-blue-400 text-white font-bold text-center rounded-md py-1 px-3 ml-auto"
          onClick={() => {
            handleCheck();
            setCheck(true);
          }}
        >
          Thêm
        </button>
      )}
    </div>
  );
};

export default InviteMemberDialog;

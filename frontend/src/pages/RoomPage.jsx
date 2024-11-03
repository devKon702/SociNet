import { ClickAwayListener, Collapse, TextareaAutosize } from "@mui/material";
import EmojiPicker from "emoji-picker-react";
import { useEffect, useRef, useState } from "react";
import RoomMessage from "../components/conversation/RoomMessage";
import { useNavigate, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {
  addNewRoomActivity,
  clearRoomActivity,
  getRoomThunk,
  removeRoom,
  setCurrentRoom,
  setRoomAction,
  updateRoomMessage,
} from "../redux/realtimeSlice";
import InviteMemberDialog from "../components/dialog/InviteMemberDialog";
import UpdateRoomDialog from "../components/dialog/UpdateRoomDialog";
import { dateDetailFormated } from "../helper";
import useFetch from "../hooks/useFetch";
import {
  createChat,
  deleteRoom,
  quitRoom,
  updateChat,
} from "../api/RoomService";
import { showSnackbar } from "../redux/snackbarSlice";
import useImageSelect from "../hooks/useImageSelect";
import { socket } from "../socket";

const RoomPage = () => {
  const { id } = useParams("id");
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { currentRoom, activities, action, currentMessage } = useSelector(
    (state) => state.realtime.roomActivity
  );
  const [content, setContent] = useState("");
  const [showEmojiPicker, setShowEmojiPicker] = useState(false);
  const [showRoomInfo, setShowRoomInfo] = useState(false);
  const [showMember, setShowMember] = useState(false);
  const activitiesRef = useRef(null);
  const { file, src, handleFileChange, sizeError, setSrc } = useImageSelect({
    initialSrc: "",
    maxSize: 5 * 1024 * 1024,
  });
  function handleButtonSendClick() {
    switch (action) {
      case "CREATE":
        console.log("Gửi chat");
        handleCreateChat();
        break;
      case "EDIT":
        console.log("Chỉnh sửa chat");
        handleEditChat();
        break;
    }
  }

  // Remove Room
  const { callFetch: handleRemoveRoom, loading: isRemoving } = useFetch({
    handleFetch: deleteRoom,
    params: [id],
    handleSuccess: (res) => {
      if (res.isSuccess) {
        dispatch(showSnackbar({ message: "Đã giải tán nhóm", type: "error" }));
        dispatch(setCurrentRoom({ isActive: false }));
        socket.emit("DISABLE ROOM", id);
      }
    },
  });
  // Quit Room
  const { callFetch: handleQuitRoom, loading: isQuitting } = useFetch({
    handleFetch: quitRoom,
    params: [id],
    handleSuccess: (res) => {
      if (res.isSuccess) {
        socket.emit("QUIT ROOM", id, res.data);
        dispatch(
          showSnackbar({ message: "Rời nhóm thành công", type: "error" })
        );
        dispatch(removeRoom(id));
        navigate("/conversation/room");
      }
    },
  });
  // Create Chat
  const { callFetch: handleCreateChat, loading: isCreatingChat } = useFetch({
    handleFetch: createChat,
    params: [id, content, file],
    handleSuccess: (res) => {
      if (res.isSuccess) {
        setContent("");
        setSrc("");
        dispatch(addNewRoomActivity(res.data));
        socket.emit("NEW ROOM MESSAGE", id, res.data);
      } else {
        dispatch(
          showSnackbar({ message: "Gửi tin nhắn thất bại", type: "error" })
        );
        console.log(res);
      }
    },
  });
  // Edit Chat
  const { callFetch: handleEditChat, loading: isEditingChat } = useFetch({
    handleFetch: updateChat,
    params: [currentMessage?.id, content],
    handleSuccess: (res) => {
      if (res.isSuccess) {
        dispatch(setRoomAction("CREATE"));
        dispatch(updateRoomMessage(res.data));
        socket.emit("UPDATE ROOM MESSAGE", id, res.data);
      } else {
        dispatch(
          showSnackbar({
            message: "Chỉnh sửa tin nhắn thất bại",
            type: "error",
          })
        );
        console.log(res);
      }
    },
  });

  useEffect(() => {
    if (!isNaN(id)) dispatch(getRoomThunk(id));
    return () => {
      dispatch(clearRoomActivity());
    };
  }, [id]);

  useEffect(() => {
    if (action === "EDIT") {
      setSrc(currentMessage.fileUrl);
      setContent(currentMessage.content);
    }
    if (action === "CREATE") {
      setSrc("");
      setContent("");
    }
  }, [action]);

  useEffect(() => {
    activitiesRef.current
      ? activitiesRef.current.scrollTo(0, activitiesRef.current.scrollHeight)
      : null;
  }, [activities]);

  if (!currentRoom) return <div className=""></div>;

  return (
    <>
      {action === "INVITE" && (
        <InviteMemberDialog
          handleClose={() => dispatch(setRoomAction(""))}
        ></InviteMemberDialog>
      )}
      {action === "UPDATE" && (
        <UpdateRoomDialog
          handleClose={() => dispatch(setRoomAction(""))}
          room={currentRoom}
        ></UpdateRoomDialog>
      )}
      <div className="flex h-full relative">
        {!currentRoom.isActive && (
          <>
            <div className="absolute z-10 inset-0 bg-black opacity-40"></div>
            <div
              className="absolute p-4 z-20 rounded-lg border-2 bg-white border-red-600 opacity-80 text-red-600 top-1/2 left-1/2 -translate-y-1/2 -translate-x-1/2 cursor-pointer"
              onClick={() => {
                dispatch(removeRoom(id));
                navigate("/conversation/room");
              }}
            >
              Nhóm đã giải tán. Xóa?
            </div>
          </>
        )}
        <section className="flex-1 flex flex-col justify-between h-full bg-white relative">
          {!showRoomInfo && (
            <div
              className="bg-lightGray rounded-full absolute top-0 right-0 mt-1 mr-2 cursor-pointer size-10 grid place-items-center"
              onClick={() => setShowRoomInfo(true)}
            >
              <i className="bx bx-dots-horizontal-rounded"></i>
            </div>
          )}
          <section className="flex px-3 py-1 items-center gap-2 shadow-lg">
            <div className="md:hidden size-10 rounded-full grid place-items-center cursor-pointer">
              <i className="bx bx-menu text-2xl"></i>
            </div>
            <div className="rounded-full overflow-hidden size-10">
              <img
                src={currentRoom?.avatarUrl || `/unknown-avatar.png`}
                alt=""
                className="size-full object-cover"
              />
            </div>
            <div>
              <p className="font-bold">{currentRoom?.name}</p>
            </div>

            <i className="bx bxs-eclipsis"></i>
          </section>
          <section
            className="px-3 py-2 flex-1 flex flex-col-reverse gap-3 custom-scroll overflow-scroll shadow-md"
            ref={activitiesRef}
          >
            {activities.map((item) => {
              switch (item.type) {
                case "CHAT":
                  return (
                    <RoomMessage key={item.id} message={item}></RoomMessage>
                  );
                case "INVITE":
                  return (
                    <p
                      key={item.id}
                      className="opacity-50 text-center"
                      title={dateDetailFormated(item.createdAt)}
                    >{`${item.sender.name} đã mời ${item.receiver.name} tham gia`}</p>
                  );
                case "KICK":
                  return (
                    <p
                      key={item.id}
                      className="opacity-50 text-center"
                      title={dateDetailFormated(item.createdAt)}
                    >{`${item.sender.name} đã xóa ${item.receiver.name}`}</p>
                  );
                case "QUIT":
                  return (
                    <p
                      key={item.id}
                      className="opacity-50 text-center"
                      title={dateDetailFormated(item.createdAt)}
                    >{`${item.sender.name} đã rời nhóm`}</p>
                  );
                default:
                  <p
                    key={item.id}
                    className="opacity-50 text-center"
                    title={dateDetailFormated(item.createdAt)}
                  >{`${item.sender.name} ${item.content}`}</p>;
              }
            })}
          </section>
          <section className="px-3 py-2">
            <div className="">
              {action === "EDIT" && (
                <div className="flex justify-between items-center mb-3">
                  <p>Đang chỉnh sửa</p>
                  <button
                    className="rounded-full hover:bg-gray-200 size-9 flex items-center justify-center"
                    onClick={() => {
                      dispatch(setRoomAction("CREATE"));
                    }}
                  >
                    <i className="bx bx-x text-2xl"></i>
                  </button>
                </div>
              )}
              {src && action === "CREATE" && (
                <div className="relative size-fit">
                  <img
                    src={src}
                    alt=""
                    className="max-w-[200px] max-h-[100px] rounded-md shadow-lg m-2"
                  />
                  <button
                    className="rounded-full bg-gray-200 size-9 flex items-center justify-center absolute top-0 right-0 translate-x-1/3 -translate-y-1/3"
                    onClick={() => {
                      if (action === "CREATE") setSrc("");
                    }}
                  >
                    <i className="bx bx-x text-2xl"></i>
                  </button>
                </div>
              )}
            </div>
            <div className="flex gap-4 items-end relative">
              {action != "EDIT" && (
                <label htmlFor="input-file" className="cursor-pointer">
                  <i className="bx bxs-image text-2xl size-10 flex items-center justify-center rounded-full hover:bg-gray-200"></i>
                </label>
              )}
              <input
                type="file"
                id="input-file"
                accept="image/*"
                hidden
                onChange={handleFileChange}
              />
              <div className="flex-1 bg-gray-300 rounded-3xl flex items-center relative">
                <TextareaAutosize
                  maxRows={5}
                  placeholder="Soạn nội dung"
                  className="resize-none py-2 px-3 outline-none bg-transparent w-full"
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                ></TextareaAutosize>
                <i
                  className="bx bx-smile mr-3 cursor-pointer text-xl"
                  onClick={() => setShowEmojiPicker(true)}
                ></i>
                {showEmojiPicker && (
                  <ClickAwayListener
                    onClickAway={() => setShowEmojiPicker(false)}
                  >
                    <div className="absolute right-0 top-0 -translate-y-full">
                      <EmojiPicker
                        open={true}
                        onEmojiClick={(e) => {
                          setContent((prev) => (prev += e.emoji));
                        }}
                      ></EmojiPicker>
                    </div>
                  </ClickAwayListener>
                )}
              </div>
              <button
                className="bg-secondary px-4 py-2 rounded-lg text-white h-fit"
                onClick={handleButtonSendClick}
                disabled={isCreatingChat || isEditingChat}
              >
                <i className="bx bxs-send mr-2 cursor-pointer"></i>
                Gửi
              </button>
            </div>
          </section>
        </section>
        {showRoomInfo && (
          <section className="w-1/3 h-full bg-white border-l-2 py-4 px-2 flex flex-col overflow-y-auto custom-scroll relative">
            <div
              className="bg-lightGray rounded-full absolute top-0 right-0 mt-1 mr-2 cursor-pointer size-10 grid place-items-center"
              onClick={() => setShowRoomInfo(false)}
            >
              <i className="bx bx-x text-2xl"></i>
            </div>
            <div className="mb-4">
              <div className="rounded-full overflow-hidden size-20 mx-auto">
                <img
                  src={currentRoom?.avatarUrl}
                  alt=""
                  className="object-cover size-full"
                />
              </div>
              <p className="font-bold text-2xl text-center">
                {currentRoom?.name}
              </p>
            </div>
            <div className="flex-1 overflow-y-scroll custom-scroll">
              {currentRoom.isAdmin && (
                <div
                  className="flex items-center justify-start gap-2 hover:bg-lightGray cursor-pointer p-2 rounded-lg"
                  onClick={() => dispatch(setRoomAction("UPDATE"))}
                >
                  <i className="bx bxs-pencil"></i>
                  <span className="font-bold">Chỉnh sửa thông tin nhóm</span>
                </div>
              )}
              <div
                className="flex items-center justify-start gap-2 hover:bg-lightGray cursor-pointer p-2 rounded-lg"
                onClick={() => setShowMember(!showMember)}
              >
                <i className="bx bxs-group"></i>
                <span className="font-bold">Thành viên</span>
                <i
                  className={`bx bxs-chevron-${
                    showMember ? "down" : "right"
                  } ml-auto`}
                ></i>
              </div>
              <Collapse in={showMember}>
                <div>
                  {currentRoom?.members.map((item, index) => (
                    <MemberItem key={index} member={item}></MemberItem>
                  ))}
                  <div
                    className="flex items-center justify-center gap-2 hover:bg-lightGray cursor-pointer p-2 rounded-lg"
                    onClick={() => dispatch(setRoomAction("INVITE"))}
                  >
                    <i className="bx bxs-user-plus"></i>
                    <span className="font-bold">Thêm thành viên</span>
                  </div>
                </div>
              </Collapse>
            </div>

            {currentRoom.isAdmin ? (
              <button
                className="flex items-center justify-start gap-2 hover:bg-lightGray cursor-pointer p-2 rounded-lg mt-auto"
                onClick={() => {
                  const check = confirm("Bạn có chắc muốn giải tán nhóm?");
                  check && handleRemoveRoom();
                }}
                disabled={isRemoving}
              >
                <i className="bx bxs-x-square text-red-400"></i>
                <span className="font-bold text-red-400">Giải tán nhóm</span>
              </button>
            ) : (
              <button
                className="flex items-center justify-start gap-2 hover:bg-lightGray cursor-pointer p-2 rounded-lg mt-auto"
                onClick={() => {
                  const check = confirm("Bạn có chắc muốn thoát nhóm?");
                  if (check) {
                    handleQuitRoom();
                  }
                }}
                disabled={isQuitting}
              >
                <i className="bx bx-log-out-circle text-red-400"></i>
                <span className="font-bold text-red-400">Rời nhóm</span>
              </button>
            )}
          </section>
        )}
      </div>
    </>
  );
};

const MemberItem = ({ member }) => {
  return (
    <div className="p-2 rounded-lg hover:bg-lightGray cursor-pointer flex gap-2">
      <div className="size-10 rounded-full overflow-hidden">
        <img
          src={member?.user?.avatarUrl || "/unknown-avatar.png"}
          alt=""
          className="object-cover size-full"
        />
      </div>
      <div>
        <p className="font-bold">{member.user.name}</p>
        <p className="text-sm text-darkGray">
          {member.isAdmin ? "Quản trị viên" : "Thành viên"}
        </p>
      </div>
    </div>
  );
};

export default RoomPage;

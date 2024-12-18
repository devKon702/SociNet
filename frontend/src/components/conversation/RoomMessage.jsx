import { useDispatch, useSelector } from "react-redux";
import { dateDetailFormated } from "../../helper";
import { realtimeRoomSelector, userInfoSelector } from "../../redux/selectors";
import {
  setCurrentRoomMessage,
  setRoomAction,
  updateRoomMessage,
} from "../../redux/realtimeSlice";
import useFetch from "../../hooks/useFetch";
import { removeChat } from "../../api/RoomService";
import { showSnackbar } from "../../redux/snackbarSlice";
import Avatar from "../common/Avatar";
import { socket } from "../../socket";

const RoomMessage = ({ message }) => {
  const user = useSelector(userInfoSelector);
  return (
    <>
      {user.id === message.sender.id ? (
        <SelfMessage message={message}></SelfMessage>
      ) : (
        <OtherMessage message={message}></OtherMessage>
      )}
    </>
  );
};

const OtherMessage = ({ message }) => {
  if (!message.isActive)
    return (
      <div className="w-fit self-start flex gap-2 items-end">
        <Avatar
          src={message.sender.avatarUrl || "/unknown-avatar.png"}
          size="size-6"
        ></Avatar>
        <div
          className="w-fit p-3 bg-gray-300 rounded-2xl"
          title={
            message.createdAt !== message.updatedAt
              ? `Đã thu hồi ${dateDetailFormated(message.updatedAt)}`
              : dateDetailFormated(message.createdAt)
          }
        >
          Tin nhắn đã thu hồi
        </div>
      </div>
    );
  return (
    <div className="flex items-end gap-2">
      <div
        className="rounded-full size-6 overflow-hidden cursor-pointer"
        title={message.sender.name}
      >
        <img
          src={message.sender.avatarUrl || "/unknown-avatar.png"}
          alt=""
          className="size-full object-cover"
        />
      </div>
      <div className="flex flex-col gap-1">
        {message.fileUrl ? (
          <div className="min-h-[100px] max-h-[300px] max-w-[300px] rounded-md overflow-hidden self-start">
            <img src={message.fileUrl} alt="" className="object-cover w-full" />
          </div>
        ) : null}
        {message.content && (
          <div className="w-fit self-start">
            <div
              className="w-fit p-3 bg-gray-300 rounded-2xl whitespace-pre-line"
              title={
                message.createdAt !== message.updatedAt
                  ? `Đã chỉnh sửa ${dateDetailFormated(message.updatedAt)}`
                  : dateDetailFormated(message.createdAt)
              }
            >
              {message.content}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

const SelfMessage = ({ message }) => {
  const dispatch = useDispatch();
  const { currentRoom } = useSelector((state) => state.realtime.roomActivity);
  // Remove Chat
  const { callFetch: handleRemove, loading } = useFetch({
    handleFetch: removeChat,
    params: [message.id],
    handleSuccess: (res) => {
      if (res.isSuccess) {
        dispatch(updateRoomMessage(res.data));
        socket.emit("UPDATE ROOM MESSAGE", currentRoom.id, res.data);
      } else {
        dispatch(
          showSnackbar({ message: "Thu hồi tin nhắn thất bại", type: "error" })
        );
      }
    },
  });

  if (!message.isActive)
    return (
      <div className="w-fit self-end">
        <div
          className="p-3 w-fit bg-secondary text-white rounded-2xl rounded-ee-none ml-auto"
          title={
            message.createdAt !== message.updatedAt
              ? `Đã thu hồi ${dateDetailFormated(message.updatedAt)}`
              : dateDetailFormated(message.createdAt)
          }
        >
          Tin nhắn đã thu hồi
        </div>
      </div>
    );

  return (
    <div className="flex flex-col gap-1">
      {message.fileUrl ? (
        <div className="max-h-[300px] max-w-[300px] rounded-md overflow-hidden self-end">
          <img src={message.fileUrl} alt="" className="object-cover w-full" />
        </div>
      ) : null}
      {message.content && (
        <div className="w-fit self-end">
          <div
            className="p-3 w-fit bg-secondary text-white rounded-2xl rounded-ee-none hover:relative ml-auto whitespace-pre-line"
            title={
              message.createdAt !== message.updatedAt
                ? `Đã chỉnh sửa ${dateDetailFormated(message.updatedAt)}`
                : dateDetailFormated(message.createdAt)
            }
          >
            {message.content}
            <div className="px-2 h-fit absolute top-1/2 left-0 -translate-x-full -translate-y-1/2 flex gap-2 text-gray-800">
              <i
                className="bx bx-trash rounded-full p-2 hover:bg-gray-200 cursor-pointer"
                onClick={() => {
                  const check = confirm(
                    "Bạn có chắc muốn thu hồi tin nhắn này?"
                  );
                  if (check) {
                    handleRemove();
                  }
                }}
              ></i>
              <i
                className="bx bxs-pencil rounded-full p-2 hover:bg-gray-200 cursor-pointer"
                onClick={() => {
                  dispatch(setRoomAction("EDIT"));
                  dispatch(setCurrentRoomMessage(message));
                }}
              ></i>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default RoomMessage;

import { TextareaAutosize } from "@mui/material";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { realtimeSelector, userInfoSelector } from "../../redux/selectors";
import { dateDetailFormated, dateFormated } from "../../helper";
import {
  removeConversationThunk,
  setConversationAction,
  setCurrentMessage,
} from "../../redux/realtimeSlice";

const MessageItem = ({ message }) => {
  const user = useSelector(userInfoSelector);
  return (
    <>
      {user.id === message.senderId ? (
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
      <div className="w-fit self-start">
        <div className="w-fit p-3 bg-gray-300 rounded-2xl rounded-ss-none">
          Tin nhắn đã thu hồi
        </div>
        <span className="text-xs ml-3">
          {message.createdAt !== message.updatedAt
            ? `Đã chỉnh sửa ${dateDetailFormated(message.updatedAt)}`
            : dateDetailFormated(message.createdAt)}
        </span>
      </div>
    );
  return (
    <div className="flex flex-col gap-1">
      {message.fileUrl ? (
        <div className="min-h-[100px] max-h-[300px] max-w-[300px] rounded-md overflow-hidden self-start">
          <img src={message.fileUrl} alt="" className="object-cover w-full" />
        </div>
      ) : null}
      {message.content && (
        <div className="w-fit self-start">
          <div className="w-fit p-3 bg-gray-300 rounded-2xl rounded-ss-none">
            {message.content}
          </div>
          <span className="text-xs ml-3">
            {message.createdAt !== message.updatedAt
              ? `Đã chỉnh sửa ${dateDetailFormated(message.updatedAt)}`
              : dateDetailFormated(message.createdAt)}
          </span>
        </div>
      )}
    </div>
  );
};

const SelfMessage = ({ message }) => {
  const dispatch = useDispatch();
  const handleEdit = () => {
    dispatch(setCurrentMessage(message));
    dispatch(setConversationAction("EDIT"));
  };
  const handleRemove = () => {
    const check = confirm("Bạn muốn thu hồi tin nhắn này");
    if (check) dispatch(removeConversationThunk(message.id));
  };

  if (!message.isActive)
    return (
      <div className="w-fit self-end">
        <div className="p-3 w-fit bg-secondary rounded-2xl rounded-ee-none ml-auto">
          Tin nhắn đã thu hồi
        </div>
        <span className="text-xs float-end mr-2">
          {message.createdAt !== message.updatedAt
            ? `Đã chỉnh sửa ${dateDetailFormated(message.updatedAt)}`
            : dateDetailFormated(message.createdAt)}
        </span>
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
          <div className="p-3 w-fit bg-secondary rounded-2xl rounded-ee-none hover:relative ml-auto">
            {message.content}
            <div className="px-2 h-fit absolute top-1/2 left-0 -translate-x-full -translate-y-1/2 flex gap-2">
              <i
                className="bx bx-trash rounded-full p-2 bg-gray-200 cursor-pointer"
                onClick={handleRemove}
              ></i>
              <i
                className="bx bxs-pencil rounded-full p-2 bg-gray-200 cursor-pointer"
                onClick={handleEdit}
              ></i>
            </div>
          </div>
          <span className="text-xs float-end mr-2">
            {message.createdAt !== message.updatedAt
              ? `Đã chỉnh sửa ${dateDetailFormated(message.updatedAt)}`
              : dateDetailFormated(message.createdAt)}
          </span>
        </div>
      )}
    </div>
  );
};

export default MessageItem;

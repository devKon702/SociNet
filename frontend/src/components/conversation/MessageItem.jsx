import { TextareaAutosize } from "@mui/material";
import React from "react";

const MessageItem = ({ isSelf, message }) => {
  return (
    <>
      {isSelf ? (
        <SelfMessage message={message}></SelfMessage>
      ) : (
        <OtherMessage message={message}></OtherMessage>
      )}
    </>
  );
};

const OtherMessage = ({ message }) => {
  return (
    <>
      {message.fileUrl ? (
        <div className="size-28 rounded-md overflow-hidden self-end">
          <img src={message.fileUrl} alt="" className="object-cover w-full" />
        </div>
      ) : null}
      <div className="w-fit self-start">
        <div className="p-3 bg-gray-300 rounded-2xl rounded-ss-none">
          {message.content}
        </div>
        <span className="text-xs ml-3">20:21</span>
      </div>
    </>
  );
};

const SelfMessage = ({ message }) => {
  return (
    <>
      {message.fileUrl ? (
        <div className="rounded-md self-end w-[300px]">
          <img src={message.fileUrl} alt="" className="object-contain w-full" />
        </div>
      ) : null}
      <div className="w-fit self-end">
        <div className="p-3 bg-secondary rounded-2xl rounded-ee-none hover:relative">
          {message.content}
          <div className="px-2 h-fit absolute top-1/2 left-0 -translate-x-full -translate-y-1/2 flex gap-2">
            <i className="bx bx-trash rounded-full p-2 bg-gray-200 cursor-pointer"></i>
            <i className="bx bxs-pencil rounded-full p-2 bg-gray-200 cursor-pointer"></i>
          </div>
        </div>
        <span className="text-xs float-end mr-2">20:21</span>
      </div>
    </>
  );
};

export default MessageItem;

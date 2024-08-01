import { TextareaAutosize } from "@mui/material";
import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import MessageItem from "../components/conversation/MessageItem";
import { useDispatch, useSelector } from "react-redux";
import { realtimeSelector } from "../redux/selectors";
import {
  createConversationThunk,
  getConversationThunk,
  setCurrentConversationUser,
} from "../redux/realtimeSlice";

const messageData = [
  { isSelf: false, content: "Hôm này thế nào" },
  { isSelf: true, content: "Nay tôi đi cafe với bạn 😁" },
  {
    isSelf: true,
    content: "Nhưng mà trời mưa nên phải ngồi lại đó tận 8h đêm",
  },
  { isSelf: false, content: "Vậy à, bạn cấp 3 hay sao?" },
  {
    isSelf: true,
    content: "Đúng r, có dịp nó lên sài gòn nên t rủ nó đi chơi",
  },
  {
    isSelf: true,
    content: "Mà có việc gì à, thường ngày đâu có tự dưng nhắn tin t đâu",
  },
  { isSelf: false, content: "À, tôi định hỏi về cái vụ hôm trước trên trường" },
  { isSelf: false, content: "Rồi sau đó giải quyết như nào rồi" },
  {
    isSelf: true,
    content: "À, tôi làm gần xong rồi, chắc mai là xong",
  },
  {
    isSelf: true,
    content: "Xem thử hình này",
    fileUrl: "/dev1.png",
  },
];

const ConversationPage = () => {
  const { id } = useParams();
  const {
    conversation: {
      messageList,
      currentUser,
      isLoading,
      currentMessage,
      action,
    },
  } = useSelector(realtimeSelector);
  const dispatch = useDispatch();

  const [content, setContent] = useState("");
  const [files, setFiles] = useState(null);
  const [imgPreviewSrc, setImgPreviewSrc] = useState(null);

  const handleSend = () => {
    if (!content.trim() || files.length == 0) return;
    switch (action) {
      case "CREATE":
        dispatch(
          createConversationThunk({
            receiverId: currentUser.id,
            content,
            file: files[0],
          })
        );
        setContent("");
        break;
      case "EDIT":
        break;
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    const reader = new FileReader();
    reader.onload = (e) => {
      setImgPreviewSrc(e.target.result);
    };
    reader.readAsDataURL(file);
    setFiles(file);
  };

  useEffect(() => {
    if (action === "CREATE") setContent("");
    else if (action === "EDIT") setContent(currentMessage.content);
  }, [action]);

  useEffect(() => {
    dispatch(getConversationThunk(id));
  }, [id]);

  if (!currentUser) return <div className=""></div>;

  return (
    <div className="flex flex-col justify-between h-full">
      <section className="flex px-3 py-1 items-center gap-2 shadow-lg">
        <div className="rounded-full overflow-hidden size-10">
          <img
            src={currentUser.avatarUrl || "/unknown-avatar.png"}
            alt=""
            className="size-full object-cover"
          />
        </div>
        <div>
          <Link
            to={`/user/${currentUser.id}`}
            className="font-bold hover:underline"
          >
            {currentUser.name}
          </Link>
          {currentUser.realtimeStatus === "ONLINE" ? (
            <p className="text-sm font-bold text-secondary">Online</p>
          ) : currentUser.realtimeStatus === "OFFLINE" ? (
            <p className="text-sm font-bold text-red-400">Offline</p>
          ) : (
            <p className="text-sm text-gray-500">Người lạ</p>
          )}
        </div>

        <i className="bx bxs-eclipsis"></i>
      </section>
      <section className="px-3 py-2 flex-1 flex flex-col-reverse gap-3 custom-scroll overflow-scroll shadow-md">
        {messageList.map((message, index) => (
          <MessageItem key={index} message={message}></MessageItem>
        ))}
      </section>
      <section className="px-3 py-2">
        <div className="p-2">
          {action === "CREATE" && (
            <div className="flex justify-between">
              <p>Đang chỉnh sửa</p>
              <button className="rounded-full hover:bg-gray-200 size-9 flex items-center justify-center">
                <i className="bx bx-x text-2xl"></i>
              </button>
            </div>
          )}
          {imgPreviewSrc && (
            <img
              src={imgPreviewSrc}
              alt=""
              className="max-w-[200px] max-h-[100px] rounded-md shadow-lg"
            />
          )}
        </div>
        <div className="flex gap-4 items-end">
          <label htmlFor="input-file" className="cursor-pointer">
            <i className="bx bxs-image text-2xl size-10 flex items-center justify-center rounded-full hover:bg-gray-200"></i>
          </label>
          <input
            type="file"
            id="input-file"
            accept="image/*"
            hidden
            onChange={handleFileChange}
          />
          <div className="flex-1 bg-gray-300 rounded-3xl flex items-center">
            <TextareaAutosize
              maxRows={5}
              placeholder="Soạn nội dung"
              className="resize-none py-2 px-3 outline-none bg-transparent w-full"
              value={content}
              onChange={(e) => setContent(e.target.value)}
            ></TextareaAutosize>
            <i className="bx bx-smile mr-3 cursor-pointer text-xl"></i>
          </div>
          <button
            className="bg-secondary px-4 py-2 rounded-lg text-white h-fit"
            onClick={handleSend}
          >
            <i className="bx bxs-send mr-2 cursor-pointer"></i>
            Gửi
          </button>
        </div>
      </section>
    </div>
  );
};

export default ConversationPage;

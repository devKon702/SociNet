import { TextareaAutosize } from "@mui/material";
import React from "react";
import { Link, useParams } from "react-router-dom";
import MessageItem from "../components/conversation/MessageItem";

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
  return (
    <div className="flex flex-col justify-between h-full">
      <section className="flex px-3 py-1 items-center gap-2 shadow-lg">
        <div className="rounded-full overflow-hidden size-10">
          <img src="/dev1.png" alt="" className="size-full object-cover" />
        </div>
        <div>
          <Link to="/user/2" className="font-bold hover:underline">
            Nguyễn Nhật Kha
          </Link>
          <p className="text-sm font-bold text-secondary">Online</p>
        </div>

        <i className="bx bxs-eclipsis"></i>
      </section>
      <section className="px-3 py-2 flex-1 flex flex-col gap-3 custom-scroll overflow-scroll shadow-md">
        {messageData.map((message, index) => (
          <MessageItem
            key={index}
            isSelf={message.isSelf}
            message={message}
          ></MessageItem>
        ))}
      </section>
      <section className="px-3 py-2 flex gap-4 items-end">
        <label htmlFor="input-file" className="cursor-pointer">
          <i className="bx bxs-image text-2xl size-10 flex items-center justify-center rounded-full hover:bg-gray-200"></i>
        </label>
        <input type="file" id="input-file" hidden />
        <div className="flex-1 bg-gray-300 rounded-3xl flex items-center">
          <TextareaAutosize
            maxRows={5}
            placeholder="Soạn nội dung"
            className="resize-none py-2 px-3 outline-none bg-transparent w-full"
          ></TextareaAutosize>
          <i className="bx bx-smile mr-3 cursor-pointer text-xl"></i>
        </div>
        <button className="bg-secondary px-4 py-2 rounded-lg text-white h-fit">
          <i className="bx bxs-send mr-2 cursor-pointer"></i>
          Gửi
        </button>
      </section>
    </div>
  );
};

export default ConversationPage;

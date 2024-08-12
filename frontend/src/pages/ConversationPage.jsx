import { ClickAwayListener, TextareaAutosize } from "@mui/material";
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import MessageItem from "../components/conversation/MessageItem";
import { useDispatch, useSelector } from "react-redux";
import { realtimeSelector } from "../redux/selectors";
import {
  createConversationThunk,
  editConversationThunk,
  getConversationThunk,
  setConversationAction,
} from "../redux/realtimeSlice";
import EmojiPicker from "emoji-picker-react";

const ConversationPage = () => {
  const { id } = useParams();
  const {
    conversation: { messageList, currentUser, currentMessage, action },
  } = useSelector(realtimeSelector);
  const dispatch = useDispatch();

  const [content, setContent] = useState("");
  const [files, setFiles] = useState([]);
  const [imgPreviewSrc, setImgPreviewSrc] = useState(null);
  const [showEmojiPicker, setShowEmojiPicker] = useState(false);

  const handleSend = () => {
    if (!content.trim() && !files) return;
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
        dispatch(editConversationThunk({ id: currentMessage.id, content }));
        dispatch(setConversationAction("CREATE"));
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
  }, [action, currentMessage]);

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
            <p className="text-sm font-bold text-green-400">Online</p>
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
        <div className="">
          {action === "EDIT" && (
            <div className="flex justify-between">
              <p>Đang chỉnh sửa</p>
              <button
                className="rounded-full hover:bg-gray-200 size-9 flex items-center justify-center"
                onClick={() => dispatch(setConversationAction("CREATE"))}
              >
                <i className="bx bx-x text-2xl"></i>
              </button>
            </div>
          )}
          {imgPreviewSrc && (
            <div className="relative size-fit">
              <img
                src={imgPreviewSrc}
                alt=""
                className="max-w-[200px] max-h-[100px] rounded-md shadow-lg m-2"
              />
              <button
                className="rounded-full bg-gray-200 size-9 flex items-center justify-center absolute top-0 right-0 translate-x-1/3 -translate-y-1/3"
                onClick={() => {
                  setFiles([]);
                  setImgPreviewSrc(null);
                }}
              >
                <i className="bx bx-x text-2xl"></i>
              </button>
            </div>
          )}
        </div>
        <div className="flex gap-4 items-end">
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
              <ClickAwayListener onClickAway={() => setShowEmojiPicker(false)}>
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

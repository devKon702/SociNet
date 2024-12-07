import { TextareaAutosize } from "@mui/material";
import React, { useEffect, useState } from "react";
import Dialog from "./Dialog";
import { useDispatch, useSelector } from "react-redux";
import { createPostThunk } from "../../redux/postSlice";
import { postSelector, userInfoSelector } from "../../redux/selectors";

const SharePostDialog = ({ post, handleClose }) => {
  const dispatch = useDispatch();
  const {
    action: { share },
  } = useSelector(postSelector);
  const user = useSelector(userInfoSelector);
  const [caption, setCaption] = useState("");
  const handleShare = () => {
    dispatch(createPostThunk({ caption, sharedPostId: post.id }));
    handleClose();
  };

  useEffect(() => {
    if (share === "shared") handleClose();
  }, [share]);
  return (
    <Dialog handleClose={handleClose} title="Chia sẻ bài viết">
      <>
        <div className="p-4 flex-1 w-full md:w-[550px] overflow-auto custom-scroll">
          <div className="flex items-center gap-3 my-2">
            <div className="rounded-full overflow-hidden size-10">
              <img
                src={user.avatarUrl || "/unknown-avatar.png"}
                alt=""
                className="object-cover w-full h-full"
              />
            </div>
            <span className="font-bold">{user.name}</span>
          </div>
          <TextareaAutosize
            minRows={3}
            className="text-black outline-none resize-none w-full border-[1px] p-4"
            placeholder="Bạn nghĩ gì về bài viết này"
            value={caption}
            onChange={(e) => setCaption(e.target.value)}
          />
        </div>
        <button
          className="w-full py-2 bg-secondary text-white font-bold rounded-md"
          onClick={handleShare}
          disabled={share === "sharing"}
        >
          {share === "sharing" ? "Đang chia sẻ" : "Chia sẻ"}
        </button>
      </>
    </Dialog>
  );
};

export default SharePostDialog;

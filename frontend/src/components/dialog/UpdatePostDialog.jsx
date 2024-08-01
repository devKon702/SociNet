import React, { useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { userInfoSelector } from "../../redux/selectors";
import Dialog from "./Dialog";
import { TextareaAutosize } from "@mui/material";
import { editPostThunk } from "../../redux/postSlice";

const UpdatePostDialog = ({ handleClose, post }) => {
  const [imageSrc, setImageSrc] = useState(post.imageUrl);
  const [imageSize, setImageSize] = useState(null);
  const fileInputRef = useRef(null);
  const [caption, setCaption] = useState(post.caption);
  const dispatch = useDispatch();

  function handleFileChange(e) {
    const file = e.target.files[0];
    const fileSize = (file.size / 1024 / 1024).toFixed(2);
    if (file.type.startsWith("image/")) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setImageSrc(e.target.result);
        setImageSize(fileSize);
      };
      reader.readAsDataURL(file);
    }
  }

  function handleUpdate() {
    dispatch(
      editPostThunk({
        postId: post.id,
        caption,
        file: fileInputRef.current.files[0],
      })
    );
    handleClose();
  }

  return (
    <Dialog handleClose={handleClose} title="Chỉnh sửa bài viết">
      <>
        <div className="p-4 flex-1 w-[550px] overflow-auto custom-scroll">
          <div className="flex items-center gap-3 my-2">
            <div className="rounded-full overflow-hidden size-10">
              <img
                src={post.user.avatarUrl || "/unknown-avatar.png"}
                alt=""
                className="object-cover w-full h-full"
              />
            </div>
            <span className="font-bold">{post.user.name}</span>
          </div>
          <TextareaAutosize
            minRows={3}
            className="text-black outline-none resize-none w-full border-[1px] p-4"
            placeholder="Viết tiêu đề tại đây"
            value={caption}
            onChange={(e) => setCaption(e.target.value)}
          />
          <div>
            {imageSrc ? (
              <div className="w-full h-[200px] relative">
                <img
                  src={imageSrc}
                  alt=""
                  className="object-cover w-full h-full"
                />
                <button
                  className="rounded-full absolute top-0 right-0 bg-slate-500 text-white p-3 size-10 translate-x-1/4 -translate-y-1/4 flex items-center justify-center"
                  onClick={() => {
                    // Xóa file trong input
                    setImageSrc(null);
                    document.getElementById("file-input").value = null;
                  }}
                >
                  <i className="bx bx-x text-3xl"></i>
                </button>
              </div>
            ) : null}

            <span className="ml-3 text-sm float-end py-2">
              {imageSize ? `${imageSize}MB` : null}
            </span>
            <label
              htmlFor="file-input"
              className="w-full hover:bg-gray-100 cursor-pointer p-3 flex items-center"
            >
              <i className="bx bxs-image-add text-2xl text-secondary"></i>
              Chọn ảnh hoặc video
            </label>
            <input
              ref={fileInputRef}
              type="file"
              id="file-input"
              hidden={true}
              onChange={handleFileChange}
            />
          </div>
        </div>
        <button
          className="w-full py-2 bg-secondary text-white font-bold rounded-md"
          onClick={handleUpdate}
        >
          Cập nhật
        </button>
      </>
    </Dialog>
  );
};

export default UpdatePostDialog;

import { useEffect, useRef, useState } from "react";
import Dialog from "./Dialog";
import { useDispatch, useSelector } from "react-redux";
import { postSelector, userInfoSelector } from "../../redux/selectors";
import { createPost } from "../../api/PostService";
import { TextareaAutosize } from "@mui/material";
import { addPost, createPostThunk } from "../../redux/postSlice";

const CreatePostDialog = ({ handleClose }) => {
  const [fileSrc, setFileSrc] = useState(null);
  const [fileSize, setFileSize] = useState(null);
  const fileInputRef = useRef(null);
  const captionRef = useRef(null);
  const currentUser = useSelector(userInfoSelector);
  const dispatch = useDispatch();
  const {
    action: { create },
  } = useSelector(postSelector);

  function handleFileChange(e) {
    const file = e.target.files[0];
    const size = (file.size / 1024 / 1024).toFixed(2);
    if (file.type.startsWith("image/") || file.type.startsWith("video/")) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setFileSrc(e.target.result);
        setFileSize(size);
      };
      reader.readAsDataURL(file);
    }
  }

  function handlePost() {
    const caption = captionRef.current.value;
    const file = fileInputRef.current.files[0];
    if (!caption.trim() && !file) return;
    dispatch(createPostThunk({ caption, file }));
    // createPost(caption, file, null).then((res) => {
    //   if (res.isSuccess) {
    //     dispatch(addPost(res.data));
    //     handleClose();
    //   }
    // });
  }
  useEffect(() => {
    if (create == "created") handleClose();
  }, [create]);

  return (
    <Dialog handleClose={handleClose} title="Tạo bài đăng">
      <>
        <div className="p-4 flex-1 w-[550px] overflow-auto custom-scroll">
          <div className="flex items-center gap-3 my-2">
            <div className="rounded-full overflow-hidden size-10">
              <img
                src={
                  currentUser.avatarUrl
                    ? currentUser.avatarUrl
                    : "/unknown-avatar.png"
                }
                alt=""
                className="object-cover w-full h-full"
              />
            </div>
            <span className="font-bold">{currentUser.name}</span>
          </div>
          <TextareaAutosize
            ref={captionRef}
            minRows={3}
            className="text-black outline-none resize-none w-full border-[1px] p-4"
            placeholder="Viết tiêu đề tại đây"
          />
          <div>
            {fileInputRef.current?.files[0] ? (
              <div className="w-full h-[200px] relative">
                {fileInputRef.current.files[0].type.startsWith("image/") && (
                  <img
                    src={fileSrc}
                    alt=""
                    className="object-cover w-full h-full"
                  />
                )}
                {fileInputRef.current.files[0].type.startsWith("video/") && (
                  <video controls className="w-full h-full">
                    <source src={fileSrc} />
                  </video>
                )}
                <button
                  className="rounded-full absolute top-0 right-0 bg-slate-500 text-white p-3 size-10 translate-x-1/4 -translate-y-1/4 flex items-center justify-center"
                  onClick={() => {
                    // Xóa file trong input
                    setFileSrc(null);
                    document.getElementById("file-input").value = null;
                  }}
                >
                  <i className="bx bx-x text-3xl"></i>
                </button>
              </div>
            ) : null}

            <span className="ml-3 text-sm float-end py-2">
              {fileSrc ? `${fileSize}MB` : null}
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
              accept="image/*,video/*"
            />
          </div>
        </div>
        <button
          className="w-full py-2 bg-secondary text-white font-bold rounded-md"
          onClick={handlePost}
          disabled={create === "creating"}
        >
          {create === "creating" ? "Dang đăng..." : "Đăng"}
        </button>
      </>
    </Dialog>
  );
};

export default CreatePostDialog;

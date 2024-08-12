import { useEffect, useState } from "react";
import Dialog from "../dialog/Dialog";
import CommentList from "./CommentList";
import { useDispatch, useSelector } from "react-redux";
import { createComment } from "../../api/CommentService";
import {
  addComment,
  editCommentThunk,
  setCommentAction,
  updateCommentListThunk,
} from "../../redux/commentSlice";
import { updatePostThunk } from "../../redux/postSlice";
import { TextareaAutosize } from "@mui/material";
import { userInfoSelector } from "../../redux/selectors";
import { showSnackbar } from "../../redux/snackbarSlice";

const CommentModal = ({ handleClose }) => {
  const [commentValue, setCommentValue] = useState("");
  const { currentPostId, action, currentCommentId, currentComment } =
    useSelector((state) => state.comment);
  const user = useSelector(userInfoSelector);
  const dispatch = useDispatch();
  const handleAdd = () => {
    if (commentValue.trim() == "") return;
    switch (action) {
      case "COMMENT":
        createComment(currentPostId, commentValue, null).then((res) => {
          dispatch(updateCommentListThunk(currentPostId));
        });
        break;
      case "REPLY":
        createComment(currentPostId, commentValue, currentCommentId).then(
          (res) => {
            if (!res.isSuccess) {
              if (res.message == "PARENT COMMENT NOT FOUND")
                dispatch(
                  showSnackbar({
                    message: "Bình luận không tồn tại",
                    type: "error",
                  })
                );
            }
            dispatch(updateCommentListThunk(currentPostId));
          }
        );
        break;
      case "EDIT":
        dispatch(
          editCommentThunk({
            commentId: currentCommentId,
            content: commentValue,
          })
        );
        break;
    }
    setCommentValue("");
    dispatch(setCommentAction());
    dispatch(updatePostThunk(currentPostId));
  };

  useEffect(() => {
    action == "COMMENT" && setCommentValue("");
    action == "EDIT" && setCommentValue(currentComment.content);
  }, [action, currentComment]);

  return (
    <Dialog handleClose={handleClose} title="Bình luận">
      <>
        <CommentList></CommentList>
        {/* User comment place */}
        <div className="divider"></div>
        {["EDIT", "REPLY"].includes(action) && (
          <div className="flex justify-between items-center px-3">
            {action === "EDIT" && <p>Đang chỉnh sửa</p>}
            {action === "REPLY" && <p>Đang trả lời</p>}
            <button
              className="rounded-full hover:bg-gray-200 size-9 flex items-center justify-center"
              onClick={() => dispatch(setCommentAction("CREATE"))}
            >
              <i className="bx bx-x text-2xl"></i>
            </button>
          </div>
        )}
        <div className="flex p-3 gap-2 items-end">
          <div className="size-10 rounded-full overflow-hidden">
            <img
              src={user.avatarUrl || "/unknown-avatar.png"}
              alt=""
              className="w-full h-full object-cover"
            />
          </div>

          <TextareaAutosize
            maxRows={4}
            value={commentValue}
            className="bg-slate-200 rounded-xl outline-none resize-none text-gray-800 p-2 flex-1"
            placeholder={action == "REPLY" ? "Trả lời" : "Bình luận"}
            onChange={(e) => setCommentValue(e.target.value)}
            // onKeyUp={(e) => (e.code == "Enter" ? handleAdd() : null)}
          />
          <button
            className="py-2 px-3 bg-backgroundSecondary h-fit"
            onClick={handleAdd}
          >
            <i className="bx bxs-send text-secondary"></i>
          </button>
        </div>
      </>
    </Dialog>
  );
};

export default CommentModal;

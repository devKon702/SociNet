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
          dispatch(updatePostThunk(currentPostId));
        });
        break;
      case "REPLY":
        createComment(currentPostId, commentValue, currentCommentId).then(
          (res) => {
            dispatch(updateCommentListThunk(currentPostId));
            dispatch(updatePostThunk(currentPostId));
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
  };

  useEffect(() => {
    action == "EDIT" && setCommentValue(currentComment.content);
  }, [action, currentComment]);

  return (
    <Dialog handleClose={handleClose} title="Bình luận">
      <>
        <CommentList></CommentList>
        {/* User comment place */}
        <div className="divider"></div>
        <div className="flex p-3 min-h-20 gap-2">
          <div className="size-10 rounded-full overflow-hidden">
            <img src={user.avatarUrl || "unknown-avatar.png"} alt="" />
          </div>

          <TextareaAutosize
            value={commentValue}
            className="bg-slate-200 rounded-xl outline-none resize-none text-gray-800 p-2 flex-1"
            placeholder={action == "REPLY" ? "Trả lời" : "Bình luận"}
            onChange={(e) => setCommentValue(e.target.value)}
            onKeyUp={(e) => (e.code == "Enter" ? handleAdd() : null)}
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

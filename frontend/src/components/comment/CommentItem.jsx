import ChildCommentItem from "./ChildCommentItem";
import { dateFormated } from "../../helper";
import { useDispatch, useSelector } from "react-redux";
import {
  removeCommentThunk,
  setCommentAction,
  setEditAction,
  setReplyAction,
} from "../../redux/commentSlice";
import { useState } from "react";

const CommentItem = ({ comment }) => {
  const dispatch = useDispatch();
  const { currentCommentId, action } = useSelector((state) => state.comment);
  const user = useSelector((state) => state.auth.user.user);
  const handleReply = () => {
    if (action == "REPLY") {
      currentCommentId === comment.id
        ? dispatch(setCommentAction())
        : dispatch(setReplyAction(comment.id));
    } else {
      dispatch(setReplyAction(comment.id));
    }
  };
  const handleEdit = () => {
    dispatch(setEditAction(comment.id));
  };
  const handleRemove = () => {
    const check = confirm("Bạn muốn thu hồi bình luận này?");
    check && dispatch(removeCommentThunk(comment.id));
  };
  return (
    <>
      <div
        className={`flex gap-2 items-start justify-start my-3 text-gray-800 rounded-md ${
          ["REPLY", "EDIT"].includes(action) && currentCommentId === comment.id
            ? "bg-gray-100 p-3"
            : ""
        }`}
      >
        <div className="avatar rounded-full overflow-hidden size-10">
          <img
            src={comment.user.avatarUrl || "/unknown-avatar.png"}
            alt=""
            className="size-full object-cover"
          />
        </div>
        <div>
          <div className="comment rounded-2xl bg-slate-200 p-2 w-fit popup-container">
            <p className="font-bold">{comment.user.name}</p>
            <p className="whitespace-pre-line">{comment.content}</p>

            {user.id === comment.user.id && (
              <div className="px-2 popup top-1/2 right-0 translate-x-full -translate-y-1/2 flex gap-2 w-20">
                <i
                  className="bx bxs-pencil rounded-full p-2 hover:bg-gray-200 cursor-pointer"
                  onClick={handleEdit}
                ></i>
                <i
                  className="bx bx-trash rounded-full p-2 hover:bg-gray-200 cursor-pointer"
                  onClick={handleRemove}
                ></i>
              </div>
            )}
          </div>
          <div>
            <span
              className="hover:underline cursor-pointer ml-4"
              onClick={handleReply}
            >
              Trả lời
            </span>
            <span className="text-sm ml-4">
              {comment.createdAt == comment.updatedAt
                ? dateFormated(comment.createdAt)
                : `Đã chỉnh sửa ${dateFormated(comment.updatedAt)}`}
            </span>
          </div>
          {/* Child Comment List */}
        </div>
      </div>
      <div className="ml-12">
        {comment.childComments.map((child) => (
          <ChildCommentItem key={child.id} comment={child}></ChildCommentItem>
        ))}
      </div>
    </>
  );
};

export default CommentItem;

import ChildCommentItem from "./ChildCommentItem";
import { dateFormated } from "../../helper";
import { useDispatch, useSelector } from "react-redux";
import { setReply } from "../../redux/commentSlice";
import { useState } from "react";

const CommentItem = ({ comment }) => {
  const [showAction, setShowAction] = useState(false);

  const dispatch = useDispatch();
  const { replyCommentId, isReplying } = useSelector((state) => state.comment);
  const user = useSelector((state) => state.auth.user.user);
  const handleReply = () => {
    if (isReplying) {
      dispatch(setReply({ isReplying: false, replyCommentId: null }));
    } else {
      dispatch(setReply({ isReplying: true, replyCommentId: comment.id }));
    }
  };
  return (
    <div
      className={`flex gap-2 items-start justify-start my-3 text-gray-800 rounded-md ${
        replyCommentId === comment.id ? "bg-gray-100 p-3" : ""
      }`}
    >
      <div className="avatar rounded-full overflow-hidden size-10">
        <img src="/dev1.png" alt="" />
      </div>
      <div>
        <div
          className="comment rounded-2xl bg-slate-200 p-2 w-fit relative"
          onMouseEnter={() => setShowAction(true)}
          onMouseLeave={() => setShowAction(false)}
        >
          <p className="font-bold">{comment.user.name}</p>
          <p>{comment.content}</p>
          {showAction && (
            <div className="px-2 h-fit absolute top-1/2 right-0 translate-x-full -translate-y-1/2 flex gap-2">
              <i className="bx bxs-pencil rounded-full p-2 hover:bg-gray-200 cursor-pointer"></i>
              <i className="bx bx-trash rounded-full p-2 hover:bg-gray-200 cursor-pointer "></i>
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
            {dateFormated(comment.createdAt)}
          </span>
        </div>
        {/* Child Comment List */}
        <div>
          {comment.childComments.map((child) => (
            <ChildCommentItem key={child.id} comment={child}></ChildCommentItem>
          ))}
        </div>
      </div>
    </div>
  );
};

export default CommentItem;

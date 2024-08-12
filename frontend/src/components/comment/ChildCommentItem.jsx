import { useDispatch, useSelector } from "react-redux";
import { dateFormated } from "../../helper";
import { commentSelector, userInfoSelector } from "../../redux/selectors";
import { removeCommentThunk, setEditAction } from "../../redux/commentSlice";

const ChildCommentItem = ({ comment }) => {
  const user = useSelector(userInfoSelector);
  const { currentCommentId, action } = useSelector(commentSelector);
  const dispatch = useDispatch();
  const handleEdit = () => {
    dispatch(setEditAction(comment.id));
  };
  const handleRemove = () => {
    const check = confirm("Bạn muốn thu hồi bình luận này?");
    check && dispatch(removeCommentThunk(comment.id));
  };
  return (
    <div
      className={`flex gap-2 items-start justify-start my-3 text-gray-800 ${
        currentCommentId == comment.id && "bg-gray-100 p-2 rounded-md"
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
        <div className="comment rounded-2xl bg-slate-200 p-2 popup-container w-fit">
          <p className="font-bold">{comment.user.name}</p>
          <p>{comment.content}</p>

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
        <p className="text-sm ml-4">
          {comment.createdAt == comment.updatedAt
            ? dateFormated(comment.createdAt)
            : `Đã chỉnh sửa ${dateFormated(comment.updatedAt)}`}
        </p>
      </div>
    </div>
  );
};

export default ChildCommentItem;

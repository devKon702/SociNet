import { dateFormated } from "../../helper";

const ChildCommentItem = ({ comment }) => {
  return (
    <div className="flex gap-2 items-start justify-start my-3 text-gray-800">
      <div className="avatar rounded-full overflow-hidden size-10">
        <img src="/dev1.png" alt="" />
      </div>
      <div>
        <div className="comment rounded-2xl bg-slate-200 p-2">
          <p className="font-bold">{comment.user.name}</p>
          <p>{comment.content}</p>
        </div>
        <p className="text-sm ml-4">{dateFormated(comment.createdAt)}</p>
      </div>
    </div>
  );
};

export default ChildCommentItem;

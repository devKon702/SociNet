import { Link } from "react-router-dom";
import { dateFormated, isImage, isVideo } from "../../helper";

const ChildPost = ({ post }) => {
  return (
    <div className="post-item bg-white w-11/12 rounded-xl p-3 text-gray-800 border-[2px] m-auto text-sm">
      {/* title */}
      <div className="flex gap-2">
        <div className="size-10 rounded-full overflow-hidden">
          <img
            src={post.user.avatarUrl || "/unknown-avatar.png"}
            alt=""
            className="w-full h-full object-cover"
          />
        </div>

        <div className="flex flex-col">
          <Link
            className="font-bold hover:underline"
            to={`/user/${post.user.id}`}
          >
            {post.user.name}
          </Link>
          <span className="text-xs text-slate-500">
            {dateFormated(post.createdAt)}
          </span>
        </div>
      </div>
      {/* Caption */}
      <p className="my-2">{post.caption}</p>
      {/* Image / video */}
      {post.imageUrl ? (
        <div className="h-80 w-full rounded-lg overflow-hidden">
          {isImage(post.imageUrl) && (
            <img
              src={post.imageUrl}
              alt=""
              className="w-full h-full object-cover"
            />
          )}
          {isVideo(post.imageUrl) && (
            <video controls className="size-full">
              <source src={post.imageUrl}></source>
            </video>
          )}
        </div>
      ) : null}
    </div>
  );
};

export default ChildPost;

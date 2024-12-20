import { useState } from "react";
import CommentModal from "../comment/CommentModal";
import ChildPost from "./ChildPost";
import { dateFormated, isImage, isVideo } from "../../helper";
import { deleteReactPost, reactPost } from "../../api/PostService";
import { useDispatch, useSelector } from "react-redux";
import {
  removePostThunk,
  setAction,
  updatePostThunk,
} from "../../redux/postSlice";
import { setCommentAction, setCurrentPostId } from "../../redux/commentSlice";
import { createComment } from "../../api/CommentService";
import { TextareaAutosize } from "@mui/material";
import { Link } from "react-router-dom";
import UpdatePostDialog from "../dialog/UpdatePostDialog";
import { authSelector } from "../../redux/selectors";
import SharePostDialog from "../dialog/SharePostDialog";
import { removePersonalPost } from "../../redux/personalSlice";

const PostItem = ({ post }) => {
  const [isShowComment, setIsShowComment] = useState(false);
  const [isShowEmojies, setIsShowEmojies] = useState(false);
  const [showUpdateDialog, setShowUpdateDialog] = useState(false);
  const [showShareDialog, setShowShareDialog] = useState(false);
  const [commentValue, setCommentValue] = useState("");

  const dispatch = useDispatch();
  const { user } = useSelector(authSelector);

  const handleComment = () => {
    if (commentValue.trim() == "") return;
    createComment(post.id, commentValue, null).then((res) => {
      dispatch(updatePostThunk(post.id));
    });
    setCommentValue("");
  };

  const handleDelete = () => {
    const check = confirm("Bạn có chắc muốn xóa bài viết này?");
    if (check) {
      dispatch(removePostThunk(post.id));
    }
  };

  return (
    <>
      {isShowComment && (
        <CommentModal
          handleClose={() => {
            setIsShowComment(false);
            dispatch(setCommentAction());
          }}
        ></CommentModal>
      )}

      {showUpdateDialog && (
        <UpdatePostDialog
          post={post}
          handleClose={() => {
            dispatch(setAction({ share: "", edit: "" }));
            setShowUpdateDialog(false);
          }}
        ></UpdatePostDialog>
      )}
      {showShareDialog && (
        <SharePostDialog
          post={post}
          handleClose={() => {
            dispatch(setAction({ create: "", share: "" }));
            setShowShareDialog(false);
          }}
        ></SharePostDialog>
      )}
      <div className="post-item bg-white w-full rounded-xl p-3 text-gray-800">
        {/* title */}
        <div className="flex gap-2 items-center">
          <div className="size-12 rounded-full overflow-hidden">
            <img
              src={post.user.avatarUrl || "/unknown-avatar.png"}
              alt=""
              className="w-full h-full object-cover"
            />
          </div>

          <div className="flex flex-col">
            <Link
              className="font-bold hover:underline cursor-pointer w-fit"
              to={`/user/${post.user.id}`}
            >
              {post.user.name}
            </Link>
            <span className="text-sm text-slate-500">
              {post.createdAt == post.updatedAt
                ? dateFormated(post.createdAt)
                : `Đã chỉnh sửa ${dateFormated(post.updatedAt)}`}
            </span>
          </div>
          {/* Actions */}
          {user.user.id === post.user.id && (
            <div className="cursor-pointer ml-auto popup-container">
              <div className="size-8 hover:bg-gray-100 rounded-full grid place-items-center">
                <i className="bx bx-dots-horizontal-rounded"></i>
              </div>
              <div className="popup rounded-md bg-gray-200 top-full right-0 w-36 overflow-hidden p-2 z-10">
                {post.isActive && (
                  <div
                    className="px-3 py-2 hover:bg-gray-300 flex items-center gap-3 rounded-md"
                    onClick={() => setShowUpdateDialog(true)}
                  >
                    <i className="bx bx-pencil"></i>Chỉnh sửa
                  </div>
                )}
                <div
                  className="px-3 py-2 hover:bg-gray-300 flex items-center gap-3 rounded-md"
                  onClick={handleDelete}
                >
                  <i className="bx bxs-trash"></i>Gỡ
                </div>
              </div>
            </div>
          )}
        </div>
        {post.isActive ? (
          <>
            {/* Caption */}
            <p className="my-2">{post.caption}</p>
            {/* Image / video */}
            {post.sharedPost ? (
              <div className="h-auto w-full rounded-lg overflow-hidden">
                <ChildPost post={post.sharedPost}></ChildPost>
              </div>
            ) : (
              post.imageUrl && (
                <div className="h-80 w-full rounded-lg overflow-hidden">
                  {/* Image */}
                  {isImage(post.imageUrl) && (
                    <img
                      src={post.imageUrl}
                      alt="Ảnh bài viết"
                      onError={(e) => (e.target.src = "/black-img.jpg")}
                      className="w-full h-full object-cover"
                    />
                  )}
                  {/* Video */}
                  {isVideo(post.imageUrl) && (
                    <video controls className="w-full h-full">
                      <source src={post.imageUrl}></source>
                    </video>
                  )}
                </div>
              )
            )}
            {/* Reaction & Comment count */}
            <div className="w-full flex justify-end gap-4 text-slate-500 my-2">
              <span>
                {`
            ${Object.values(post.numberOfReactions).reduce(
              (pre, current) => current + pre,
              0
            )} tương tác`}
              </span>
              <span>{post.numberOfComments} bình luận</span>
            </div>
            {/* Buttons */}
            <div className="h-[2px] w-full bg-slate-200 my-2"></div>
            <div
              className={`w-full grid text-slate-500 ${
                post.sharedPost ? "grid-cols-2" : "grid-cols-3"
              }`}
            >
              {post.selfReaction ? (
                <ReactionButton
                  type={post.selfReaction}
                  postId={post.id}
                  handleEnter={() => setIsShowEmojies(true)}
                  handleLeave={() => setIsShowEmojies(false)}
                >
                  {isShowEmojies ? (
                    <EmojiList postId={post.id}></EmojiList>
                  ) : null}
                </ReactionButton>
              ) : (
                <button
                  className=" hover:bg-slate-100 flex items-center justify-center gap-1 py-2 relative"
                  onMouseEnter={() => {
                    setIsShowEmojies(true);
                  }}
                  onMouseLeave={() => {
                    setIsShowEmojies(false);
                  }}
                >
                  {isShowEmojies ? (
                    <EmojiList postId={post.id}></EmojiList>
                  ) : null}
                  <i className="bx bx-like"></i>
                  <span className="hidden md:inline-block">Tương tác</span>
                </button>
              )}
              <button
                className=" hover:bg-slate-100 flex items-center justify-center gap-1"
                onClick={() => {
                  dispatch(setCurrentPostId(post.id));
                  setIsShowComment(true);
                }}
              >
                <i className="bx bx-chat"></i>
                <span className="hidden md:inline-block">Bình luận</span>
              </button>
              {!post.sharedPost && (
                <button
                  className=" hover:bg-slate-100 flex items-center justify-center gap-1"
                  onClick={() => setShowShareDialog(true)}
                >
                  <i className="bx bx-share-alt"></i>
                  <span className="hidden md:inline-block">Chia sẻ</span>
                </button>
              )}
            </div>
            {/* Comment input */}
            <div className="h-[2px] w-full bg-slate-200 my-2"></div>
            <div className="flex w-full gap-2">
              <TextareaAutosize
                type="text"
                className="rounded-lg p-2 bg-slate-100 flex-1 outline-none text-wrap resize-none"
                placeholder="Type your comment"
                value={commentValue}
                onChange={(e) => setCommentValue(e.target.value)}
                onKeyDown={(e) => {
                  if (e.shiftKey && e.key == "Enter") {
                    setCommentValue(commentValue + "\n");
                    e.preventDefault();
                  } else if (e.key == "Enter") {
                    handleComment();
                    e.preventDefault();
                  }
                }}
              />
              <button
                className="py-2 px-3 bg-backgroundSecondary"
                onClick={handleComment}
              >
                <i className="bx bxs-send text-secondary"></i>
              </button>
            </div>
          </>
        ) : (
          <div className="flex bg-gray-100 p-3 rounded-md items-center justify-center gap-2 mt-4">
            <i className="bx bxs-lock-alt text-xl"></i>
            <p>Bài viết đã bị khóa</p>
          </div>
        )}
      </div>
    </>
  );
};

const ReactionButton = ({
  type,
  handleEnter,
  handleLeave,
  postId,
  children,
}) => {
  const dispatch = useDispatch();
  const handleClick = async (e) => {
    try {
      // if (e.target.tagName !== "BUTTON") return;
      await deleteReactPost(postId);
      dispatch(updatePostThunk(postId));
    } catch (e) {
      console.log(e);
    }
  };
  switch (type) {
    case "LIKE":
      return (
        <button
          className="hover:bg-slate-100 flex items-center justify-center gap-2 py-2 text-blue-400 relative"
          onMouseEnter={handleEnter}
          onMouseLeave={handleLeave}
          onClick={handleClick}
        >
          {children}
          <div className="size-6" onClick={(e) => {}}>
            <img src="/like.png" alt="" className="size-full object-cover" />
          </div>
          <span className="hidden md:inline-block" onClick={(e) => {}}>
            Thích
          </span>
        </button>
      );
    case "LOVE":
      return (
        <button
          className="hover:bg-slate-100 flex items-center justify-center gap-2 py-2 text-red-400 relative"
          onMouseEnter={handleEnter}
          onMouseLeave={handleLeave}
          onClick={handleClick}
        >
          {children}
          <div className="size-6">
            <img src="/love.png" alt="" className="size-full object-cover" />
          </div>
          <span className="hidden md:inline-block">Yêu thích</span>
        </button>
      );
    case "HAHA":
      return (
        <button
          className="hover:bg-slate-100 flex items-center justify-center gap-2 py-2 text-yellow-300 relative"
          onMouseEnter={handleEnter}
          onMouseLeave={handleLeave}
          onClick={handleClick}
        >
          {children}
          <div className="size-6">
            <img src="/haha.png" alt="" className="size-full object-cover" />
          </div>
          <span className="hidden md:inline-block">Haha</span>
        </button>
      );
    case "SAD":
      return (
        <button
          className="hover:bg-slate-100 flex items-center justify-center gap-2 py-2 text-yellow-400 relative"
          onMouseEnter={handleEnter}
          onMouseLeave={handleLeave}
          onClick={handleClick}
        >
          {children}
          <div className="size-6">
            <img src="/sad.png" alt="" className="size-full object-cover" />
          </div>
          <span className="hidden md:inline-block">Buồn</span>
        </button>
      );
    default:
      return null;
  }
};

const EmojiList = ({ postId }) => {
  return (
    <div className="absolute top-0 left-0 -translate-y-full flex gap-3 p-4">
      <Emoji path="/like.png" postId={postId} type="LIKE" />
      <Emoji path="/love.png" postId={postId} type="LOVE" />
      <Emoji path="/haha.png" postId={postId} type="HAHA" />
      <Emoji path="/sad.png" postId={postId} type="SAD" />
    </div>
  );
};

const Emoji = ({ path, postId, type }) => {
  const dispatch = useDispatch();
  const handleReact = async () => {
    try {
      await reactPost({ postId, type });
      // const post = await getPost(postId);
      // dispatch(updatePost(post.data));
      dispatch(updatePostThunk(postId));
    } catch (e) {
      console.log(e);
    }
  };
  return (
    <div
      className="size-10 hover:scale-150 transition-all ease-in-out"
      onClick={(e) => {
        e.stopPropagation();
        handleReact();
      }}
    >
      <img src={path} alt="" className="w-full h-full object-cover" />
    </div>
  );
};

export default PostItem;

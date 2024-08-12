import React from "react";
import { dateFormated, isImage, isVideo } from "../../helper";
import { managePostThunk } from "../../redux/adminSlice";
import { useDispatch } from "react-redux";

const AdminPostItem = ({ post }) => {
  const dispatch = useDispatch();
  return (
    <div className="rounded-md bg-white p-4 flex flex-col gap-4 text-gray-800">
      <div className="flex justify-between">
        <div>
          <p className="font-bold text-lg">{`ID: ${post.id}`}</p>
          <p className="text-sm text-gray">{`Ngày đăng: ${dateFormated(
            post.createdAt
          )}`}</p>
        </div>
        {post.isActive ? (
          <i
            className="bx bxs-lock-open-alt text-secondary text-xl cursor-pointer size-9 rounded-full hover:bg-gray-100 grid place-items-center"
            onClick={() => {
              dispatch(managePostThunk({ postId: post.id, isActive: false }));
            }}
          ></i>
        ) : (
          <i
            className="bx bxs-lock-alt text-red-500 text-xl hover:bg-gray-100 grid place-items-center cursor-pointer size-9 rounded-full"
            onClick={() => {
              dispatch(managePostThunk({ postId: post.id, isActive: true }));
            }}
          ></i>
        )}
      </div>
      <div>{post.caption}</div>
      {post.imageUrl && isImage(post.imageUrl) && (
        <img src={post.imageUrl} alt="" className="rounded-md w-full" />
      )}
      {post.imageUrl && isVideo(post.imageUrl) && (
        <video className="w-full h-full rounded-md" controls>
          <source src={post.imageUrl}></source>
        </video>
      )}
      <div className="grid grid-cols-2 gap-3 mt-auto">
        <button className="rounded-md p-2 shadow-lg">{`${Object.values(
          post.numberOfReactions
        ).reduce((prev, val) => (prev += val), 0)} tương tác`}</button>
        <button className="rounded-md p-2 shadow-lg">{`${post.numberOfComments} bình luận`}</button>
        {/* <button className="bg-red-400 text-white flex items-center justify-center gap-2 rounded-md p-2">
          <i className="bx bxs-lock-open-alt"></i>
          Đã khóa
        </button> */}
      </div>
    </div>
  );
};

export default AdminPostItem;

import React from "react";
import { Link } from "react-router-dom";

const FriendItem = ({ user }) => {
  return (
    <Link
      to={`/user/${user.id}`}
      className="user-item flex gap-2 hover:bg-slate-100 p-2 cursor-pointer items-center rounded-md"
    >
      <div className="rounded-full size-10 overflow-hidden relative">
        <img
          src={user.avatarUrl || "/unknown-avatar.png"}
          alt=""
          className="object-cover w-full h-full"
        />
      </div>
      <span className="text-gray-800">{user.name}</span>
    </Link>
  );
};

export default FriendItem;

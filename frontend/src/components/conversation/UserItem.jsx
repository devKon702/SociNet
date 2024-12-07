import React from "react";
import { NavLink } from "react-router-dom";

const UserItem = ({ user }) => {
  return (
    <NavLink
      to={`/conversation/${user.id}`}
      className={({ isActive }) =>
        isActive
          ? "rounded-lg flex items-center gap-3 py-4 px-3 bg-gray-200"
          : "rounded-lg flex items-center gap-3 py-4 px-3 hover:bg-gray-300"
      }
    >
      <div className="size-8 rounded-full relative">
        <img
          src={user.avatarUrl || "/unknown-avatar.png"}
          alt=""
          className="object-cover size-full rounded-full"
        />
        <div
          className={`size-2 rounded-full absolute bottom-0 right-0 ${
            user.realtimeStatus === "ONLINE"
              ? "bg-green-400"
              : user.realtimeStatus === "OFFLINE"
              ? "bg-red-400"
              : ""
          }`}
        ></div>
      </div>
      <div>
        <p className="font-bold text-nowrap text-ellipsis" title={user.name}>
          {user.name}
        </p>
        <p className="opacity-50 font-bold">
          {user.hasUnreadMessage && "Tin nhắn mới"}
        </p>
      </div>
    </NavLink>
  );
};

export default UserItem;

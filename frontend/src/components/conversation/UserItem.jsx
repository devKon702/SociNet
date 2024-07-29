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
      <div className="size-8 rounded-full overflow-hidden">
        <img
          src={user.avatarUrl || "/unknown-avatar.png"}
          alt=""
          className="object-cover size-full"
        />
      </div>

      <p className="font-bold">{user.name}</p>
    </NavLink>
  );
};

export default UserItem;

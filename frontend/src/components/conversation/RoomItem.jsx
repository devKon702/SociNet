import { NavLink } from "react-router-dom";

const RoomItem = ({ room }) => {
  return (
    <NavLink
      to={`/conversation/room/${room.id}`}
      className={({ isActive }) =>
        isActive
          ? "rounded-lg flex items-center gap-3 py-4 px-3 bg-gray-200"
          : "rounded-lg flex items-center gap-3 py-4 px-3 hover:bg-gray-300"
      }
    >
      <div className="size-8 rounded-full relative">
        <img
          src={room.avatarUrl || "/group-placeholder.png"}
          alt=""
          className="object-cover size-full rounded-full"
        />
      </div>
      <div>
        <p className="font-bold">{room.name}</p>
        <p className="opacity-50 font-bold">
          {room.hasUnreadMessage && "Tin nhắn mới"}
        </p>
      </div>
    </NavLink>
  );
};

export default RoomItem;

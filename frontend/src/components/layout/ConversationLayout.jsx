import { Link, Outlet } from "react-router-dom";
import UserItem from "../conversation/UserItem";
import { useDispatch, useSelector } from "react-redux";
import {
  realtimeFriendSelector,
  realtimeRoomSelector,
} from "../../redux/selectors";
import { setConversationFilter } from "../../redux/realtimeSlice";
import { useState } from "react";
import RoomItem from "../conversation/RoomItem";
import CreateRoomDialog from "../dialog/CreateRoomDialog";

const ConversationLayout = () => {
  const realtimeFriends = useSelector(realtimeFriendSelector);
  const realtimeRooms = useSelector(realtimeRoomSelector);
  const [action, setAction] = useState(null);
  const [page, setPage] = useState(
    location.pathname.includes("room") ? "room" : "conversation"
  );
  const dispatch = useDispatch();

  return (
    <>
      {action == "create room" && (
        <CreateRoomDialog
          handleClose={() => setAction(null)}
        ></CreateRoomDialog>
      )}
      <div className="flex h-screen md:py-6 md:px-8 justify-center items-center gap-6 text-gray-800 bg-lightGray">
        <section className="flex flex-col rounded-xl md:w-3/12 h-full bg-white overflow-hidden p-2 gap-2 fixed md:relative left-0 w-[250px] z-10">
          {/* Header */}
          <Link
            to={"/home"}
            className="px-3 py-4 bg-slate-800 rounded-lg flex justify-center items-center h-[100px]"
          >
            <div className="w-1/3 h-full">
              <img
                src="/logo-withoutbg.png"
                alt=""
                className="h-full object-contain mx-auto"
              />
            </div>

            <div className="w-2/3 h-full">
              <img
                src="/logo-name-withoutbg.png"
                alt=""
                className="h-full object-contain mx-auto"
              />
            </div>
          </Link>

          <input
            onChange={(e) => dispatch(setConversationFilter(e.target.value))}
            type="text"
            placeholder="Tìm kiếm..."
            className="rounded-3xl outline-none bg-gray-300 py-3 px-4 w-full"
          />
          <div className="flex justify-center">
            <button
              className={`w-1/2 text-center cursor-pointer p-2 ${
                page === "conversation"
                  ? "border-b-2 border-blue-400 text-blue-400 font-bold"
                  : ""
              }`}
              onClick={() => setPage("conversation")}
              disabled={page === "conversation"}
            >
              Riêng
            </button>
            <button
              className={`w-1/2 text-center cursor-pointer p-2 ${
                page === "room"
                  ? "border-b-2 border-blue-400 text-blue-400 font-bold"
                  : ""
              }`}
              onClick={() => setPage("room")}
              disabled={page === "room"}
            >
              Nhóm
            </button>
          </div>

          {/* List */}
          {page === "conversation" && (
            <ul className="overflow-scroll custom-scroll flex-1">
              {realtimeFriends.map((friend) => (
                <UserItem user={friend} key={friend.id}></UserItem>
              ))}
            </ul>
          )}

          {page === "room" && (
            <>
              <div
                className="p-2 bg-green-400 text-white font-bold rounded-lg flex justify-center mx-auto cursor-pointer w-full"
                onClick={() => setAction("create room")}
              >
                Tạo nhóm
              </div>
              <ul className="overflow-scroll custom-scroll flex-1">
                {realtimeRooms.map((room) => (
                  <RoomItem room={room} key={room.id}></RoomItem>
                ))}
              </ul>
            </>
          )}
        </section>
        <section className="rounded-xl flex-1 h-full overflow-hidden">
          <Outlet></Outlet>
        </section>
      </div>
    </>
  );
};

export default ConversationLayout;

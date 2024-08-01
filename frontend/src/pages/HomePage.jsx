import PostList from "../components/post/PostList";
import { useState } from "react";
import CreatePostDialog from "../components/dialog/CreatePostDialog";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import { realtimeSelector, userInfoSelector } from "../redux/selectors";
import FriendList from "../components/friend/FriendList";

const HomePage = () => {
  const [isShowCreatePostModal, setShowCreatePostModal] = useState(false);
  const user = useSelector(userInfoSelector);
  const { realtimeFriends } = useSelector(realtimeSelector);

  if (user == null) return null;
  return (
    <div className="flex h-full bg-slate-200">
      {isShowCreatePostModal ? (
        <CreatePostDialog
          handleClose={() => setShowCreatePostModal(false)}
        ></CreatePostDialog>
      ) : null}

      <section className="w-3/12 h-full bg-white text py-4 px-2">
        <ToolItem
          icon="bx bxs-home-circle"
          name="Trang cá nhân"
          to={`/user/${user.id}`}
        ></ToolItem>
        <ToolItem
          icon="bx bxs-duplicate text-secondary"
          name="Đăng bài"
          onClick={() => setShowCreatePostModal(true)}
        ></ToolItem>
        <ToolItem
          icon="bx bxs-message-square-dots text-blue-400"
          name="Nhắn tin"
          to="/conversation"
        ></ToolItem>
        <ToolItem
          icon="bx bxs-group text-primary"
          name="Bạn bè"
          to={`/user/${user.id}/friends`}
        ></ToolItem>
        {/* <ToolItem
          icon="bx bxs-like text-blue-700"
          name="Đã tương tác"
        ></ToolItem> */}
      </section>
      <PostList></PostList>
      {/* Online Friend */}
      <section className="w-3/12 h-full bg-white py-4 px-3 text-gray-800 flex flex-col">
        <div className="flex items-start flex-col mb-4 gap-2 justify-between">
          <h1 className="font-bold text-xl text-secondary">Trò chuyện ngay</h1>
          <input
            type="text"
            placeholder="Search"
            className="p-2 outline-none bg-slate-200 rounded-md w-full"
          />
        </div>
        <div className="flex-1 custom-scroll overflow-y-auto">
          {realtimeFriends.map((friend, index) => (
            <Link
              to={`/conversation/${friend.id}`}
              className="rounded-md hover:bg-gray-200 p-2 flex items-center cursor-pointer gap-3"
              key={index}
            >
              <div className="size-8 rounded-full relative">
                <img
                  src={friend.avatarUrl || "unknown-avatar.png"}
                  alt=""
                  className="object-cover rounded-full"
                />
                <div
                  className={`size-2 rounded-full ${
                    friend.realtimeStatus === "ONLINE"
                      ? "bg-secondary"
                      : friend.realtimeStatus === "OFFLINE"
                      ? "bg-red-400"
                      : ""
                  } absolute bottom-0 right-0`}
                ></div>
              </div>
              <p>{friend.name}</p>
            </Link>
          ))}
        </div>
      </section>
    </div>
  );
};

const UserItem = () => {
  return (
    <Link
      to="/user/1"
      className="user-item flex gap-2 hover:bg-slate-100 p-2 cursor-pointer items-center rounded-md"
    >
      <div className="rounded-full size-10 overflow-hidden relative">
        <img src="/dev1.png" alt="" className="object-cover w-full h-full" />
      </div>
      <span className="text-gray-800">Nguyễn Nhật Kha</span>
    </Link>
  );
};

const ToolItem = ({ icon, name, ...props }) => {
  return (
    <Link
      className="flex items-center gap-2 text-gray-700 font-bold p-2 hover:bg-slate-100 cursor-pointer"
      {...props}
    >
      <i className={`${icon} text-[28px]`}></i>
      {name}
    </Link>
  );
};

export default HomePage;

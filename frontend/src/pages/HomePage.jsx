import PostList from "../components/post/PostList";
import { useState } from "react";
import CreatePostDialog from "../components/dialog/CreatePostDialog";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { realtimeSelector, userInfoSelector } from "../redux/selectors";
import { setAction } from "../redux/postSlice";

const HomePage = () => {
  const dispatch = useDispatch();
  const [isShowCreatePostModal, setShowCreatePostModal] = useState(false);
  const [friendSearch, setFriendSearch] = useState("");
  const user = useSelector(userInfoSelector);
  const { realtimeFriends } = useSelector(realtimeSelector);

  if (user == null) return null;
  return (
    <div className="flex justify-center h-full gap-2 px-2">
      {isShowCreatePostModal && (
        <CreatePostDialog
          handleClose={() => {
            dispatch(setAction({ create: "", share: "" }));
            setShowCreatePostModal(false);
          }}
        ></CreatePostDialog>
      )}

      <section className="lg:w-3/12 h-fit bg-white py-4 px-2 rounded-xl md:block w-fit">
        <ToolItem
          icon="bx bxs-home-circle"
          name="Trang cá nhân"
          to={`/user/${user.id}`}
        ></ToolItem>
        <ToolItem
          icon="bx bxs-duplicate text-green-400"
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
      <section className="hidden w-3/12 h-fit bg-white py-4 px-3 rounded-xl text-gray-800 md:flex flex-col">
        <div className="flex items-start flex-col mb-4 gap-2 justify-between">
          <h1 className="font-bold text-xl text-secondary">Trò chuyện ngay</h1>
          <input
            value={friendSearch}
            type="text"
            placeholder="Search"
            className="p-2 outline-none bg-slate-200 rounded-md w-full"
            onChange={(e) => setFriendSearch(e.target.value)}
          />
        </div>
        <div className="flex-1 custom-scroll overflow-y-auto">
          {realtimeFriends
            .filter((friend) => friend.name.includes(friendSearch))
            .map((friend, index) => (
              <Link
                to={`/conversation/${friend.id}`}
                className="rounded-md hover:bg-gray-200 p-2 flex items-center justify-start cursor-pointer gap-3"
                key={index}
                title={friend.name}
              >
                <div className="size-8 rounded-full relative">
                  <img
                    src={friend.avatarUrl || "/unknown-avatar.png"}
                    alt=""
                    className="object-cover w-full h-full rounded-full"
                  />
                  <div
                    className={`size-2 rounded-full ${
                      friend.realtimeStatus === "ONLINE"
                        ? "bg-green-400"
                        : friend.realtimeStatus === "OFFLINE"
                        ? "bg-red-400"
                        : ""
                    } absolute bottom-0 right-0`}
                  ></div>
                </div>
                <div
                  className={`flex flex-1 items-center overflow-hidden ${
                    friend.hasUnreadMessage ? "font-bold" : ""
                  }`}
                >
                  {friend.name}
                </div>
                {friend.hasUnreadMessage && (
                  <div className="size-3 rounded-full bg-secondary"></div>
                )}
              </Link>
            ))}
        </div>
      </section>
      {/* <section className="h-fit w-fit bg-white py-4 px-2 rounded-xl md:hidden">
        <div className="size-fit p-2 md:hidden grid place-items-center bg-white cursor-pointer hover:bg-slate-100">
          <i className="bx bxs-conversation text-secondary text-2xl"></i>
        </div>
      </section> */}
    </div>
  );
};

const ToolItem = ({ icon, name, ...props }) => {
  return (
    <Link
      className="flex items-center gap-2 text-gray-700 font-bold p-2 hover:bg-slate-100 cursor-pointer"
      {...props}
      title={name}
    >
      <i className={`${icon} text-[28px]`}></i>
      <span className="hidden lg:inline-block">{name}</span>
    </Link>
  );
};

export default HomePage;

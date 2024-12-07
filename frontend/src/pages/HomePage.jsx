import PostList from "../components/post/PostList";
import { useEffect, useState } from "react";
import CreatePostDialog from "../components/dialog/CreatePostDialog";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {
  friendSelector,
  realtimeSelector,
  userInfoSelector,
} from "../redux/selectors";
import { setAction } from "../redux/postSlice";
import { Collapse } from "@mui/material";
import useFetch from "../hooks/useFetch";
import { getFriendSuggestion } from "../api/FriendService";
import { setSuggestionList } from "../redux/friendSlice";

const HomePage = () => {
  const dispatch = useDispatch();
  const [isShowCreatePostModal, setShowCreatePostModal] = useState(false);
  const [friendSearch, setFriendSearch] = useState("");
  const user = useSelector(userInfoSelector);
  const { realtimeFriends } = useSelector(realtimeSelector);
  const [showTools, setShowTools] = useState(true);
  const { suggestionList } = useSelector(friendSelector);

  const { callFetch: hanldeGetSuggestion, response } = useFetch({
    handleFetch: getFriendSuggestion,
    params: [],
    handleSuccess: (res) => {
      console.log(response);

      if (res.isSuccess) {
        dispatch(setSuggestionList(res.data));
      }
    },
  });

  useEffect(() => {
    if (suggestionList?.length == 0) hanldeGetSuggestion();
  }, []);

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
      {/* <section
        className={`w-fit h-fit bg-white rounded-xl ${
          showTools ? "lg:w-3/12" : ""
        }`}
      ></section> */}
      <Collapse in={showTools} orientation="horizontal">
        <section className="w-full py-4 px-2 bg-white rounded-xl">
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
      </Collapse>

      <div
        className="h-10 bg-gray-500 fixed grid place-items-center left-0 top-40 opacity-80 rounded-tr-md rounded-br-md"
        onClick={() => setShowTools(!showTools)}
        title="Lối tắt"
      >
        <i className={`bx bx-chevron-${showTools ? "left" : "right"}`}></i>
      </div>

      <PostList></PostList>
      {/* Online Friend */}
      <section className="hidden w-3/12 h-fit max-h-full text-gray-800 md:flex flex-col gap-3 custom-scroll overflow-auto">
        <div className="bg-white py-4 px-3 rounded-xl text-gray-800 flex flex-col">
          <div className="flex items-start flex-col mb-4 gap-2 justify-between">
            <h1 className="font-bold text-xl text-secondary">
              Trò chuyện ngay
            </h1>
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
        </div>
        {suggestionList?.length > 0 && (
          <div className="bg-white py-4 px-3 rounded-xl text-gray-800 flex flex-col">
            <p className="font-bold text-xl text-secondary">Có thể bạn biết</p>
            <div className="custom-scroll h-[200px] overflow-y-auto">
              {suggestionList.map((user) => (
                <FriendSuggestion key={user.id} user={user}></FriendSuggestion>
              ))}
            </div>
          </div>
        )}
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
      <span className="hidden lg:inline-block text-nowrap">{name}</span>
    </Link>
  );
};

const FriendSuggestion = ({ user }) => {
  const navigate = useNavigate();
  return (
    <div className="flex items-center gap-2 hover:bg-lightGray p-2 rounded-md overflow-hidden">
      <div className="rounded-full overflow-hidden size-10">
        <img
          src={user.avatarUrl || "/unknown-avatar.png"}
          className="size-full object-cover"
        />
      </div>
      <div className="w-3/5">
        <p
          className="w-full truncate whitespace-nowrap text-ellipsis overflow-hidden hover:underline cursor-pointer"
          title={user.name}
          onClick={() => navigate("/user/" + user.id)}
        >
          {user.name}
        </p>
        {/* <button
          className="self-center w-full bg-secondary text-white text-sm rounded-md px-3 py-1 cursor-pointer hover:scale-95 duration-300"
          onClick={handleInvite}
        >
          Kết bạn
        </button> */}
      </div>
    </div>
  );
};

export default HomePage;

import { Link, Outlet } from "react-router-dom";
import UserItem from "../conversation/UserItem";
import { useDispatch, useSelector } from "react-redux";
import {
  realtimeFriendSelector,
  realtimeSelector,
} from "../../redux/selectors";
import { setConversationFilter } from "../../redux/realtimeSlice";

const ConversationLayout = () => {
  const realtimeFriends = useSelector(realtimeFriendSelector);
  const dispatch = useDispatch();
  return (
    <div className="flex container justify-center items-center gap-5 text-gray-800">
      <section className="flex flex-col rounded-lg w-3/12 h-full bg-white overflow-hidden p-2 gap-2">
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

        <div className="divider"></div>
        <input
          onChange={(e) => dispatch(setConversationFilter(e.target.value))}
          type="text"
          placeholder="Tìm kiếm..."
          className="rounded-3xl outline-none bg-gray-300 p-3"
        />

        {/* List */}
        <ul className="overflow-scroll custom-scroll flex-1">
          {realtimeFriends.map((friend, index) => (
            <UserItem user={friend} key={index}></UserItem>
          ))}
        </ul>
      </section>
      <section className="rounded-lg flex-1 h-full bg-white overflow-hidden">
        <Outlet></Outlet>
      </section>
    </div>
  );
};

export default ConversationLayout;

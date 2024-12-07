import FriendItem from "./FriendItem";
import { useSelector } from "react-redux";
import { personalSelector } from "../../redux/selectors";

const FriendList = () => {
  const { friendList } = useSelector(personalSelector);

  if (friendList.length == 0)
    return (
      <div className="h-[150px] w-full flex items-center justify-center text-xl text-gray-500 font-bold">
        Hiện chưa có bạn bè nào
      </div>
    );
  else
    return (
      <div className="grid grid-cols-2 sm:grid-cols-3 py-4">
        {friendList.map((friend) => (
          <FriendItem user={friend} key={friend.id}></FriendItem>
        ))}
      </div>
    );
};

export default FriendList;

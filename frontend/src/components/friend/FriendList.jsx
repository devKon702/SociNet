import React, { useEffect, useState } from "react";
import { getUserInfos } from "../../api/UserService";
import FriendItem from "./FriendItem";
import { useSelector } from "react-redux";

const FriendList = () => {
  const [friendList, setFriendList] = useState([]);
  const user = useSelector((state) => state.auth.user.user);

  useEffect(() => {
    getUserInfos().then((res) => setFriendList([...res.data]));
  }, []);
  return (
    <>
      {friendList.map((friend) => {
        if (friend.id === user.id) return;
        return <FriendItem user={friend} key={friend.id}></FriendItem>;
      })}
    </>
  );
};

export default FriendList;

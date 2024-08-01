import { useSelector } from "react-redux";
import PostItem from "./PostItem";
import { useEffect } from "react";

const PersonalPostList = () => {
  const { postList } = useSelector((state) => state.personal);
  useEffect(() => {}, []);

  if (postList.length == 0)
    return (
      <div className="h-[150px] w-full flex items-center justify-center text-xl text-gray-500 font-bold">
        Hiện chưa đăng bài viết nào
      </div>
    );
  else
    return (
      <div className="flex-1 max-h-full overflow-y-auto custom-scroll flex flex-col items-center py-4 gap-8">
        {postList.map((post, index) => (
          <PostItem key={index} post={post}></PostItem>
        ))}
      </div>
    );
};

export default PersonalPostList;

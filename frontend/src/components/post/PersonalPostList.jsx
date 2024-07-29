import { useDispatch, useSelector } from "react-redux";
import PostItem from "./PostItem";
import { postSelector } from "../../redux/selectors";
import { getPosts } from "../../api/PostService";
import { useEffect } from "react";
import {
  setError,
  setLoading,
  setPostList,
  setSuccess,
} from "../../redux/postSlice";
import { getPersonalInfo, getPersonalPost } from "../../redux/personalSlice";

const PersonalPostList = () => {
  const dispatch = useDispatch();
  const { postList, isLoading } = useSelector((state) => state.personal);
  useEffect(() => {}, []);

  return (
    <div className="flex-1 max-h-full overflow-y-auto custom-scroll flex flex-col items-center py-4 gap-8">
      {isLoading ? (
        <div>Loading</div>
      ) : (
        postList.map((post, index) => (
          <PostItem key={index} post={post}></PostItem>
        ))
      )}
    </div>
  );
};

export default PersonalPostList;

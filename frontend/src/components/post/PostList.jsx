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

const PostList = () => {
  const dispatch = useDispatch();
  const { postList, isLoading } = useSelector(postSelector);
  useEffect(() => {
    dispatch(setLoading());
    getPosts()
      .then((res) => {
        dispatch(setSuccess());
        dispatch(setPostList(res.data));
      })
      .catch((e) => {
        dispatch(setError("Get posts error"));
      });
  }, []);

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

export default PostList;

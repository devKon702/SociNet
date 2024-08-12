import { useDispatch, useSelector } from "react-redux";
import PostItem from "./PostItem";
import { postSelector } from "../../redux/selectors";
import { getPosts } from "../../api/PostService";
import { useEffect, useRef } from "react";
import {
  setCurrentScroll,
  setError,
  setLoading,
  setPostList,
  setSuccess,
} from "../../redux/postSlice";

const PostList = () => {
  const dispatch = useDispatch();
  const { postList, isLoading, currentScrollPosition } =
    useSelector(postSelector);
  const ref = useRef(null);
  useEffect(() => {
    if (postList.length == 0) {
      dispatch(setLoading());
      getPosts()
        .then((res) => {
          dispatch(setSuccess());
          dispatch(setPostList(res.data));
        })
        .catch((e) => {
          dispatch(setError("Get posts error"));
        });
    } else {
      ref.current.scrollTo({ top: currentScrollPosition, behavior: "instant" });
    }
  }, []);

  return (
    <div
      ref={ref}
      className="flex-1 max-h-full overflow-y-auto custom-scroll flex flex-col items-center py-4 gap-8"
      onScroll={(e) => dispatch(setCurrentScroll(e.target.scrollTop))}
    >
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

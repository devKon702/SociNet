import CommentItem from "./CommentItem";
import { getCommentsOfPost } from "../../api/CommentService";
import { useDispatch, useSelector } from "react-redux";
import { setCommentList } from "../../redux/commentSlice";
import { useEffect } from "react";

const CommentList = () => {
  const postId = useSelector((state) => state.comment.currentPostId);
  const commentList = useSelector((state) => state.comment.commentList);
  const dispatch = useDispatch();
  useEffect(() => {
    try {
      getCommentsOfPost(postId).then((res) => {
        dispatch(setCommentList(res.data));
      });
    } catch (e) {
      console.log(e);
    }
  }, [dispatch, postId]);
  return (
    <div className="w-[600px] flex-1 p-3 overflow-auto custom-scroll">
      {commentList.map((comment) => (
        <CommentItem key={comment.id} comment={comment}></CommentItem>
      ))}
    </div>
  );
};
export default CommentList;

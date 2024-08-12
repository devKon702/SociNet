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
    getCommentsOfPost(postId).then((res) => {
      if (res.isSuccess) {
        dispatch(setCommentList(res.data));
      } else {
        dispatch(setCommentList([]));
      }
    });
  }, [dispatch, postId]);
  return (
    <div className="w-[600px] flex-1 p-3 overflow-auto custom-scroll pr-24">
      {commentList.map((comment) => (
        <CommentItem key={comment.id} comment={comment}></CommentItem>
      ))}
    </div>
  );
};
export default CommentList;

import { useDispatch, useSelector } from "react-redux";
import { authSelector, realtimeSelector } from "../redux/selectors";
import { Navigate, Outlet } from "react-router-dom";
import { useEffect } from "react";
import { prepareRealtimeDataThunk } from "../redux/realtimeSlice";

const UserPage = () => {
  const { user } = useSelector(authSelector);
  const { isLoading } = useSelector(realtimeSelector);
  const dispatch = useDispatch();
  useEffect(() => {
    dispatch(prepareRealtimeDataThunk());
  }, []);

  if (!user.roles.includes("USER")) return <Navigate to={"/"} />;
  if (!isLoading) return <Outlet></Outlet>;
};

export default UserPage;

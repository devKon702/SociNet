import { useDispatch, useSelector } from "react-redux";
import { authSelector, realtimeSelector } from "../redux/selectors";
import { Navigate, Outlet } from "react-router-dom";
import { useEffect } from "react";
import { prepareRealtimeDataThunk } from "../redux/realtimeSlice";
import { socket } from "../socket";

const UserPage = () => {
  const { user } = useSelector(authSelector);
  const { isLoading } = useSelector(realtimeSelector);
  const dispatch = useDispatch();

  useEffect(() => {
    if (user.roles.includes("USER")) {
      socket.connect();
      socket.emit("NOTIFY ONLINE", user.user);
      dispatch(prepareRealtimeDataThunk());
      return () => {
        socket.disconnect();
      };
    }
  }, []);

  if (!user.roles.includes("USER")) return <Navigate to={"/admin"} />;
  if (!isLoading) return <Outlet></Outlet>;
};

export default UserPage;

import { useDispatch, useSelector } from "react-redux";
import { authSelector, realtimeSelector } from "../redux/selectors";
import { Navigate, Outlet } from "react-router-dom";
import { useEffect } from "react";
import { prepareRealtimeDataThunk } from "../redux/realtimeSlice";
import { socket } from "../socket";
import { getIpInformation } from "../helper";
import { setIP } from "../redux/authSlice";

const UserPage = () => {
  const { user } = useSelector(authSelector);
  const { isLoading } = useSelector(realtimeSelector);
  const dispatch = useDispatch();

  useEffect(() => {
    if (user.roles.includes("USER")) {
      socket.connect();
      socket.emit("NOTIFY ONLINE", user.user);
      dispatch(prepareRealtimeDataThunk());
      getIpInformation().then((res) => dispatch(setIP(res.query)));
      return () => {
        socket.disconnect();
      };
    }
  }, []);

  if (user.roles.includes("ADMIN")) return <Navigate to={"/admin"} />;
  if (!isLoading) return <Outlet></Outlet>;
};

export default UserPage;

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
  const handleOnline = () => {
    getIpInformation().then((res) => dispatch(setIP(res.query)));
  };

  useEffect(() => {
    if (user.roles.includes("USER")) {
      socket.connect();
      socket.emit("NOTIFY ONLINE", user.user);
      dispatch(prepareRealtimeDataThunk());
      handleOnline();
      window.addEventListener("online", handleOnline);
      return () => {
        socket.disconnect();
        window.removeEventListener("online", handleOnline);
      };
    }
  }, []);

  if (user.roles.includes("ADMIN")) return <Navigate to={"/admin"} />;
  if (!isLoading) return <Outlet></Outlet>;
};

export default UserPage;

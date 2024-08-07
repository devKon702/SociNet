import { useSelector } from "react-redux";
import { authSelector } from "../redux/selectors";
import { Navigate, Outlet } from "react-router-dom";
import { useEffect } from "react";
import { socketAdmin } from "../socket";

const AdminPage = () => {
  const { user } = useSelector(authSelector);

  useEffect(() => {
    socketAdmin.connect();
  }, []);

  if (!user.roles.includes("ADMIN")) return <Navigate to={"/"} />;
  return <Outlet></Outlet>;
};

export default AdminPage;

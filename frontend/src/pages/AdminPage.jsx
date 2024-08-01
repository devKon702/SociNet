import { useSelector } from "react-redux";
import { authSelector } from "../redux/selectors";
import { Navigate, Outlet } from "react-router-dom";

const AdminPage = () => {
  const { user } = useSelector(authSelector);
  if (!user.roles.includes("ADMIN")) return <Navigate to={"/"} />;
  return <Outlet></Outlet>;
};

export default AdminPage;

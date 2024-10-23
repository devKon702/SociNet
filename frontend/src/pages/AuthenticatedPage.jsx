import { useSelector } from "react-redux";
import { authSelector } from "../redux/selectors";
import { Navigate, Outlet } from "react-router-dom";

const AuthenticatedPage = () => {
  const { isAuthenticated } = useSelector(authSelector);
  return isAuthenticated ? (
    <Outlet></Outlet>
  ) : location.pathname.includes("auth/") ? (
    <Outlet></Outlet>
  ) : (
    <Navigate to={"auth/signin"} />
  );
};

export default AuthenticatedPage;

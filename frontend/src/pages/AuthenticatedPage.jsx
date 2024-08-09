import { useSelector } from "react-redux";
import { authSelector } from "../redux/selectors";
import { Navigate, Outlet } from "react-router-dom";
import { ErrorBoundary } from "react-error-boundary";
import ErrorPage from "./ErrorPage";

const AuthenticatedPage = () => {
  const { isAuthenticated } = useSelector(authSelector);
  return isAuthenticated ? <Outlet></Outlet> : <Navigate to={"auth/signin"} />;
};

export default AuthenticatedPage;

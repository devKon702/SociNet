import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { authSelector, userInfoSelector } from "../redux/selectors";
import { Navigate, Outlet, useNavigate } from "react-router-dom";
import { signout } from "../redux/authSlice";
import { socket } from "../socket";

const AuthenticatedPage = () => {
  const { isAuthenticated } = useSelector(authSelector);
  const user = useSelector(userInfoSelector);

  return isAuthenticated ? (
    <>
      <Outlet></Outlet>
    </>
  ) : (
    <Navigate to={"auth/signin"} />
  );
};

export default AuthenticatedPage;

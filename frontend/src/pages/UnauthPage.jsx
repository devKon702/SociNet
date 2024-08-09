import React, { useEffect } from "react";
import { useSelector } from "react-redux";
import { authSelector } from "../redux/selectors";
import { Navigate, Outlet, useNavigate } from "react-router-dom";
import { ErrorBoundary } from "react-error-boundary";
import ErrorPage from "./ErrorPage";

const UnauthPage = () => {
  const auth = useSelector(authSelector);

  if (auth.isAuthenticated) {
    if (auth.user.roles.includes("USER")) return <Navigate to={"/"} />;
    if (auth.user.roles.includes("ADMIN")) return <Navigate to={"/admin"} />;
  }

  return <Outlet></Outlet>;
};

export default UnauthPage;

import React from "react";
import { useSelector } from "react-redux";
import { authSelector } from "../redux/selectors";
import { Navigate, Outlet } from "react-router-dom";

const UnauthPage = () => {
  const auth = useSelector(authSelector);
  if (auth.isAuthenticated) {
    if (auth.user.roles.includes("USER")) return <Navigate to={"/"} />;
    if (auth.user.roles.includes("ADMIN")) return <Navigate to={"/admin"} />;
  }
  return <Outlet></Outlet>;
};

export default UnauthPage;

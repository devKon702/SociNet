import React from "react";
import { Outlet, useRouteError } from "react-router-dom";

const RouteErrorPage = () => {
  const error = useRouteError();
  console.log(error);
  return <Outlet></Outlet>;
};

export default RouteErrorPage;

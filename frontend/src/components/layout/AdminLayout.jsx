import React from "react";
import AdminHeader from "./AdminHeader";
import AdminDashboard from "./AdminDashboard";
import { Outlet } from "react-router-dom";

const AdminLayout = () => {
  return (
    <div className="w-screen h-screen flex flex-col">
      <AdminHeader></AdminHeader>
      <div className="flex-1 flex overflow-hidden">
        <AdminDashboard></AdminDashboard>
        <div className="flex-1 overflow-auto custom-scroll bg-gray-100 w-full py-3 px-8">
          <Outlet></Outlet>
        </div>
      </div>
    </div>
  );
};

export default AdminLayout;

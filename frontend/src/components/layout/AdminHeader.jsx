import React from "react";
import { signOut } from "../../api/AuthService";
import { useDispatch } from "react-redux";
import { Link, useNavigate } from "react-router-dom";

const AdminHeader = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  return (
    <div className="w-full h-[50px] flex justify-between items-center bg-slate-700 py-3 px-10">
      <div className="h-full w-fit flex justify-around cursor-pointer gap-1">
        <div className="h-full">
          <img
            src="/logo-withoutbg.png"
            alt=""
            className="w-full h-full object-contain"
          />
        </div>
        <div className="h-full">
          <img
            src="/logo-name-withoutbg.png"
            alt=""
            className="w-full h-full object-contain"
          />
        </div>
      </div>
      <div className="flex gap-4 items-center popup-container">
        <div className="flex gap-4 items-center cursor-pointer relative">
          <span className="font-bold">Admin</span>
          <div className="size-8 rounded-full overflow-hidden">
            <img
              src="/dev1.png"
              alt=""
              className="w-full h-full object-cover"
            />
          </div>
          <div className="popup top-full right-0 rounded-lg bg-gray-700 p-3 w-[200px] shadow-md z-50">
            <Link
              to="/admin/account"
              className="hover:bg-gray-600 rounded-md p-3 flex items-center gap-2"
            >
              <i className="bx bxs-id-card text-xl"></i>
              Tài khoản
            </Link>
            <button
              className="hover:bg-gray-600 rounded-md p-3 flex items-center gap-2 w-full"
              onClick={() => signOut(dispatch, navigate)}
            >
              <i className="bx bx-log-out-circle text-xl"></i>
              Đăng xuất
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminHeader;

import React, { useState } from "react";
import ForgotPasswordDialog from "../components/dialog/ForgotPasswordDialog";
import { Link } from "react-router-dom";

const ForgotPasswordPage = () => {
  const [isShowModal, setShowModal] = useState(false);
  return (
    <div className="w-screen h-screen grid place-items-center text-gray-800">
      <div className="flex flex-col bg-white rounded-lg py-8 px-20 gap-2 relative h-3/4 w-2/5">
        <p className="text-primary font-bold text-3xl text-center">
          Quên mật khẩu
        </p>
        <p className="opacity-70 text-center mb-4">
          Nhập địa chỉ email để tìm lại tài khoản
        </p>
        <input
          type="text"
          placeholder="Địa chỉ email của bạn"
          className="rounded-md outline-none p-2 text-center border border-gray-400"
        />
        <button className="bg-secondary text-white flex items-center justify-center py-2 font-bold gap-1 shadow-lg rounded-md">
          <i className="bx bx-search text-xl"></i>Tìm
        </button>

        <div className="divider"></div>
        <div
          className="flex gap-4 items-center rounded-md p-2 hover:bg-gray-200 cursor-pointer shadow-lg"
          onClick={() => setShowModal(true)}
        >
          <div className="size-10 rounded-full overflow-hidden">
            <img src="/dev1.png" alt="" className="size-full object-cover" />
          </div>
          <p>Nguyễn Nhật Kha</p>
        </div>
        <p className="text-center text-sm text-primary">Nhấn để tiếp tục</p>
        <Link
          to="/auth/signin"
          className="mt-auto flex gap-1 items-center justify-center py-2 rounded-md cursor-pointer hover:bg-gray-200"
        >
          <i className="bx bxs-log-in text-2xl"></i>
          Quay lại trang đăng nhập
        </Link>
      </div>
      {isShowModal ? (
        <ForgotPasswordDialog
          handleClose={() => {
            setShowModal(false);
          }}
        ></ForgotPasswordDialog>
      ) : null}
    </div>
  );
};

export default ForgotPasswordPage;

import React from "react";
import Dialog from "./Dialog";

const ForgotPasswordDialog = ({ handleClose }) => {
  return (
    <Dialog handleClose={handleClose} title={"Quên mật khẩu"}>
      <>
        <div className="flex flex-col px-10 min-w-[400px]">
          <p className="text-sm mb-4 text-center">
            Mã OTP đã được gửi đến email của bạn
          </p>
          <p className="font-bold">Mật khẩu mới</p>
          <input
            type="password"
            className="p-2 outline-none rounded-md border border-gray-500 mb-4"
            placeholder="123@abc"
          />
          <p className="font-bold">Xác nhận lại</p>
          <input
            type="password"
            className="p-2 outline-none rounded-md border border-gray-500"
            placeholder="123@abc"
          />
          <p className="font-bold mt-6">Mã OTP</p>

          <input
            type="text"
            className="flex-1 p-2 outline-none rounded-md border border-gray-500 mb-2"
          />
          <button className="p-2 px-8 border border-secondary rounded-md flex items-center gap-2 mx-auto text-secondary">
            <i className="bx bx-revision"></i>
            Gửi lại mã
          </button>
        </div>
        <button className="mt-auto mb-3 mx-2 p-3 bg-primary text-white rounded-md">
          Đổi mật khẩu
        </button>
      </>
    </Dialog>
  );
};

export default ForgotPasswordDialog;

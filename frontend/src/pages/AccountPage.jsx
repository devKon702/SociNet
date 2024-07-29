import { useState } from "react";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";

const AccountPage = () => {
  const user = useSelector((state) => state.auth.user);
  const [changeEmailProgress, setChangeEmailProgress] = useState(1);

  return (
    <div className="w-full h-full bg-gray-200 py-4">
      <div className="bg-white shadow-md w-3/4 h-full mx-auto text-gray-800 rounded-md px-3 py-4 flex flex-col gap-5 overflow-auto custom-scroll">
        <div className="flex justify-between items-center p-4 w-full rounded-md hover:bg-gray-100 cursor-pointer shadow-lg">
          <p>
            <span className="font-bold">Email: </span>
            {user?.email}
          </p>
          <i className="bx bxs-pencil"></i>
        </div>
        {/* Update Email Progress */}
        <div>
          <div className="progress flex items-center justify-center w-3/4 mx-auto">
            <button
              className=" shadow-md rounded-full border-[8px] border-secondary font-bold size-10 flex items-center justify-center"
              onClick={() => setChangeEmailProgress(1)}
            >
              1
            </button>
            <div
              className={`w-1/3 h-[8px] shadow-md ${
                changeEmailProgress >= 2 ? "bg-secondary" : ""
              }`}
            ></div>
            <button
              className={`shadow-md rounded-full border-[8px] ${
                changeEmailProgress >= 2 ? "border-secondary" : ""
              } font-bold size-10 flex items-center justify-center`}
              onClick={() => setChangeEmailProgress(2)}
            >
              2
            </button>
            <div
              className={`w-1/3 h-[8px] shadow-md ${
                changeEmailProgress >= 3 ? "bg-secondary" : ""
              }`}
            ></div>
            <button
              className={`shadow-md rounded-full border-[8px] ${
                changeEmailProgress >= 3 ? "border-secondary" : ""
              } font-bold size-10 flex items-center justify-center`}
              onClick={() => setChangeEmailProgress(3)}
            >
              3
            </button>
          </div>
          {/*  Progress 1*/}
          {changeEmailProgress == 1 && (
            <div className="progress-1 min-h-[250px] flex flex-col items-center justify-center py-4 gap-8">
              <p className="font-bold text-xl text-center">Chỉnh sửa email</p>
              <div>
                <label htmlFor="input-email" className="font-bold mr-4">
                  Email:
                </label>
                <input
                  type="email"
                  value="nhatkha117@gmail.com"
                  className="p-3 bg-gray-100 rounded-lg outline-none"
                />
              </div>
              <button
                className="mx-auto py-2 px-6 bg-secondary rounded-lg text-white font-bold"
                onClick={() => setChangeEmailProgress(2)}
              >
                Tiếp theo
              </button>
            </div>
          )}

          {/* Progress 2 */}
          {changeEmailProgress == 2 && (
            <div className="progress-2 min-h-[250px] flex flex-col items-center justify-center py-4 gap-8">
              <div className="">
                <p className="font-bold text-xl text-center">Xác nhận OTP</p>
                <p className="text-sm text-center">
                  (Mã OTP đã được gửi đến email của bạn)
                </p>
              </div>
              <div>
                <input
                  type="text"
                  className="p-3 bg-gray-100 rounded-lg text-center outline-none tracking-widest font-bold"
                />
              </div>
              <div className="">
                <button className="mx-auto py-2 px-6 text-secondary rounded-lg shadow-lg font-bold">
                  Gửi lại mã
                </button>
                <button
                  className="mx-auto py-2 px-6 bg-secondary rounded-lg text-white font-bold ms-6"
                  onClick={() => setChangeEmailProgress(3)}
                >
                  Xác nhận
                </button>
              </div>
            </div>
          )}
          {/* Progress 3 */}
          {changeEmailProgress == 3 && (
            <div className="progress-3 min-h-[250px] flex flex-col items-center justify-center py-8 text-gray-800 font-bold text-2xl gap-8">
              <p className="flex items-center gap-2">
                <i className="bx bx-check-circle text-4xl"></i>
                Hoàn thành
              </p>
              <button className="bg-secondary py-2 px-6 text-white rounded-lg">
                Xác nhận
              </button>
            </div>
          )}
        </div>

        <div className="flex justify-between items-center p-4 w-full rounded-md hover:bg-gray-100 cursor-pointer shadow-lg">
          <p>
            <span className="font-bold">Đổi mật khẩu</span>
          </p>
          <i className="bx bxs-pencil"></i>
        </div>

        {/* Change Password */}

        <div className="mx-auto flex flex-col gap-1 w-1/3 my-3">
          <label htmlFor="" className="font-bold mr-4">
            Mật khẩu cũ:
          </label>
          <input
            type="password"
            className="p-3 bg-gray-100 rounded-lg outline-none mb-3"
          />

          <label htmlFor="" className="font-bold mr-4">
            Mật khẩu mới:
          </label>
          <input
            type="password"
            className="p-3 bg-gray-100 rounded-lg outline-none mb-3"
          />
          <label htmlFor="" className="font-bold mr-4">
            Xác nhận lại:
          </label>
          <input
            type="password"
            className="p-3 bg-gray-100 rounded-lg outline-none mb-3"
          />
          <button className="bg-primary text-white p-3 rounded-md w-1/2 mx-auto">
            Cập nhật
          </button>
        </div>
        <Link
          to="/auth/signin"
          className="flex justify-between items-center p-4 w-full rounded-md hover:bg-gray-100 cursor-pointer shadow-lg"
        >
          <p>
            <span className="font-bold">Đăng xuất</span>
          </p>
          <i className="bx bxs-exit"></i>
        </Link>
      </div>
    </div>
  );
};

export default AccountPage;

import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { accountSelector } from "../redux/selectors";
import { signOut } from "../api/AuthService";
import {
  changeEmailThunk,
  changePasswordThunk,
  sendOtpThunk,
  setConfirmPassword,
  setEmailError,
  setNewEmail,
  setNewPassword,
  setOldPassword,
  setOtp,
  setPasswordError,
  setProgress,
} from "../redux/accountSlice";

const AccountPage = () => {
  const account = useSelector((state) => state.auth.user);
  const { manageEmail, managePassword } = useSelector(accountSelector);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [changeEmailProgress, setChangeEmailProgress] = useState(1);
  const [showEmailForm, setShowEmailForm] = useState(false);
  const [showPasswordForm, setShowPasswordForm] = useState(false);

  const handleSignOut = () => {
    const check = confirm("Bạn chắc chắn muốn đăng xuất?");
    check && dispatch(signOut(dispatch, navigate));
  };

  const handleChangeEmail = () => {
    const check = confirm(
      "Bạn muốn thay đổi sang email: " + manageEmail.newEmail + "?"
    );
    check && dispatch(changeEmailThunk({ ...manageEmail }));
  };

  const handleChangePassword = () => {
    if (managePassword.newPassword !== managePassword.confirmPassword)
      dispatch(
        setPasswordError({ errorMessage: "Xác nhận mật khẩu không đúng" })
      );
    else {
      dispatch(changePasswordThunk({ ...managePassword }));
    }
  };

  useEffect(() => {
    dispatch(setEmailError({ errorMessage: "" }));
  }, [manageEmail.progress]);

  return (
    <div className="w-full h-full bg-gray-200 py-4">
      <div className="bg-white shadow-md w-3/4 h-full mx-auto text-gray-800 rounded-md px-3 py-4 flex flex-col gap-5 overflow-auto custom-scroll">
        <div
          className="flex justify-between items-center p-4 w-full rounded-md hover:bg-gray-100 cursor-pointer shadow-lg"
          onClick={() => setShowEmailForm(!showEmailForm)}
        >
          <p>
            <span className="font-bold">Email: </span>
            {account?.email}
          </p>
          <i className="bx bxs-pencil"></i>
        </div>
        {/* Update Email Progress */}
        {showEmailForm && (
          <div>
            <div className="progress flex items-center justify-center w-3/4 mx-auto">
              <button
                className=" shadow-md rounded-full border-[8px] border-secondary font-bold size-10 flex items-center justify-center"
                onClick={() => dispatch(setProgress({ progress: 1 }))}
              >
                1
              </button>
              <div
                className={`w-1/3 h-[8px] shadow-md ${
                  manageEmail.progress >= 2 ? "bg-secondary" : ""
                }`}
              ></div>
              <button
                className={`shadow-md rounded-full border-[8px] ${
                  manageEmail.progress >= 2 ? "border-secondary" : ""
                } font-bold size-10 flex items-center justify-center`}
                onClick={() =>
                  manageEmail.progress > 2 &&
                  dispatch(setProgress({ progress: 2 }))
                }
              >
                2
              </button>
              <div
                className={`w-1/3 h-[8px] shadow-md ${
                  manageEmail.progress >= 3 ? "bg-secondary" : ""
                }`}
              ></div>
              <button
                className={`shadow-md rounded-full border-[8px] ${
                  changeEmailProgress >= 3 ? "border-secondary" : ""
                } font-bold size-10 flex items-center justify-center`}
              >
                3
              </button>
            </div>
            <p className="text-red-500 text-center text-sm mt-3">
              {manageEmail.errorMessage}
            </p>
            {/*  Progress 1*/}
            {manageEmail.progress == 1 && (
              <div className="progress-1 min-h-[250px] flex flex-col items-center justify-center py-4 gap-8">
                <p className="font-bold text-xl text-center">Chỉnh sửa email</p>
                <div>
                  <label htmlFor="input-email" className="font-bold mr-4">
                    Email:
                  </label>
                  <input
                    type="email"
                    value={manageEmail.newEmail}
                    className="p-3 bg-gray-100 rounded-lg outline-none"
                    onChange={(e) =>
                      dispatch(setNewEmail({ newEmail: e.target.value }))
                    }
                  />
                </div>
                <button
                  className="mx-auto py-2 px-6 bg-secondary rounded-lg text-white font-bold"
                  onClick={() => {
                    if (manageEmail.newEmail.trim()) {
                      dispatch(sendOtpThunk(manageEmail.newEmail.trim()));
                      dispatch(setProgress({ progress: 2 }));
                    } else {
                      dispatch(
                        setEmailError({
                          errorMessage: "Hãy điền email muốn thay đổi",
                        })
                      );
                    }
                  }}
                >
                  Tiếp theo
                </button>
              </div>
            )}

            {/* Progress 2 */}
            {manageEmail.progress == 2 && (
              <div className="progress-2 min-h-[250px] flex flex-col items-center justify-center py-4 gap-8">
                <div className="">
                  <p className="font-bold text-xl text-center">Xác nhận OTP</p>
                  <p className="text-sm text-center text-primary">
                    {manageEmail.otpPending
                      ? `(Mã OTP đang gửi đến ${manageEmail.newEmail})`
                      : "(Mã OTP đã được gửi)"}
                  </p>
                </div>
                <div>
                  <input
                    type="text"
                    className="p-3 bg-gray-100 rounded-lg text-center outline-none tracking-widest font-bold"
                    value={manageEmail.otp}
                    onChange={(e) => dispatch(setOtp({ otp: e.target.value }))}
                  />
                </div>
                <div className="">
                  <button
                    className="mx-auto py-2 px-6 text-secondary rounded-lg shadow-lg font-bold outline-none"
                    disabled={manageEmail.otpPending}
                    onClick={() => {
                      dispatch(sendOtpThunk(manageEmail.newEmail.trim()));
                    }}
                  >
                    {manageEmail.otpPending ? "Đang gửi..." : "Gửi lại mã"}
                  </button>
                  <button
                    className="mx-auto py-2 px-6 bg-secondary rounded-lg text-white font-bold ms-6"
                    onClick={handleChangeEmail}
                  >
                    Xác nhận
                  </button>
                </div>
              </div>
            )}
            {/* Progress 3 */}
            {manageEmail.progress == 3 && (
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
        )}

        <div
          className="flex justify-between items-center p-4 w-full rounded-md hover:bg-gray-100 cursor-pointer shadow-lg"
          onClick={() => setShowPasswordForm(!showPasswordForm)}
        >
          <p>
            <span className="font-bold">Đổi mật khẩu</span>
          </p>
          <i className="bx bxs-pencil"></i>
        </div>

        {/* Change Password */}
        {showPasswordForm && (
          <div className="mx-auto flex flex-col gap-1 w-1/3 my-3">
            <label htmlFor="" className="font-bold mr-4">
              Mật khẩu cũ:
            </label>
            <input
              type="password"
              className="p-3 bg-gray-100 rounded-lg outline-none mb-3"
              value={managePassword.oldPassword}
              onChange={(e) =>
                dispatch(setOldPassword({ oldPassword: e.target.value }))
              }
            />

            <label htmlFor="" className="font-bold mr-4">
              Mật khẩu mới:
            </label>
            <input
              type="password"
              className="p-3 bg-gray-100 rounded-lg outline-none mb-3"
              value={managePassword.newPassword}
              onChange={(e) =>
                dispatch(setNewPassword({ newPassword: e.target.value }))
              }
            />
            <label htmlFor="" className="font-bold mr-4">
              Xác nhận lại:
            </label>
            <input
              type="password"
              className="p-3 bg-gray-100 rounded-lg outline-none mb-3"
              value={managePassword.confirmPassword}
              onChange={(e) =>
                dispatch(
                  setConfirmPassword({ confirmPassword: e.target.value })
                )
              }
            />
            <p className="text-red-500 text-sm text-center">
              {managePassword.errorMessage}
            </p>
            <button
              className="bg-primary text-white p-3 rounded-md w-1/2 mx-auto"
              onClick={handleChangePassword}
            >
              Cập nhật
            </button>
          </div>
        )}
        <div
          className="flex justify-between items-center p-4 w-full rounded-md hover:bg-gray-100 cursor-pointer shadow-lg mt-auto"
          onClick={handleSignOut}
        >
          <p>
            <span className="font-bold">Đăng xuất</span>
          </p>
          <i className="bx bxs-exit"></i>
        </div>
      </div>
    </div>
  );
};

export default AccountPage;

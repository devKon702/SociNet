import React, { useState } from "react";
import Dialog from "./Dialog";
import { useDispatch, useSelector } from "react-redux";
import { forgotPasswordSelector } from "../../redux/selectors";
import { forgotPassword, getOtp } from "../../api/AuthService";
import { useNavigate } from "react-router-dom";

const ForgotPasswordDialog = ({ handleClose }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { email } = useSelector(forgotPasswordSelector);
  const [isSending, setSending] = useState(false);
  const [isSubmitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [formValues, setFormValues] = useState({
    newPassword: "",
    confirm: "",
    otp: "",
  });

  const handleResendOtp = async () => {
    setSending(true);
    await getOtp(email);
    setSending(false);
  };

  const handleSubmit = () => {
    if (formValues.confirm !== formValues.newPassword) {
      setError("Xác nhận sai mật khẩu");
      return;
    }
    setSubmitting(true);
    forgotPassword(email, formValues.newPassword, formValues.otp)
      .then((res) => {
        console.log(res);
        navigate("/auth/signin?username=" + res.data.username);
      })
      .catch((e) => {
        setSubmitting(false);
        setError(e.message);
      });
  };

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
            value={formValues.newPassword}
            onChange={(e) =>
              setFormValues({ ...formValues, newPassword: e.target.value })
            }
            required
          />
          <p className="font-bold">Xác nhận lại</p>
          <input
            type="password"
            className="p-2 outline-none rounded-md border border-gray-500"
            placeholder="123@abc"
            value={formValues.confirm}
            onChange={(e) =>
              setFormValues({ ...formValues, confirm: e.target.value })
            }
            required
          />
          <p className="font-bold mt-6">Mã OTP</p>

          <input
            type="text"
            className="flex-1 p-2 outline-none rounded-md border border-gray-500 mb-2"
            value={formValues.otp}
            onChange={(e) =>
              setFormValues({ ...formValues, otp: e.target.value })
            }
            required
          />
          <button
            className="p-2 px-8 border border-secondary rounded-md flex items-center gap-2 mx-auto text-secondary"
            onClick={handleResendOtp}
            disabled={isSending}
          >
            <i className="bx bx-revision"></i>
            {isSending ? "Đang gửi..." : "Gửi lại mã"}
          </button>
        </div>
        <p className="text-red-500 text-sm text-center">{error}</p>
        <button
          className="mt-auto mb-3 mx-2 p-3 bg-primary text-white rounded-md"
          onClick={handleSubmit}
          disabled={isSubmitting}
        >
          {isSubmitting ? "Loading...." : "Đổi mật khẩu"}
        </button>
      </>
    </Dialog>
  );
};

export default ForgotPasswordDialog;

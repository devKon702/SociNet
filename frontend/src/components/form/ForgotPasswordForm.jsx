import React, { useState } from "react";
import { useController, useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as Yup from "yup";
import { forgotPassword, getOtp } from "../../api/AuthService";
import { useDispatch, useSelector } from "react-redux";
import { forgotPasswordSelector } from "../../redux/selectors";
import { useNavigate } from "react-router-dom";
import { showSnackbar } from "../../redux/snackbarSlice";

const schema = Yup.object({
  newPassword: Yup.string()
    .required("Vui lòng nhập mật khẩu mới")
    .min(6, "Độ dài tối thiểu 6 kí tự"),
  confirm: Yup.string()
    .required()
    .oneOf([Yup.ref("newPassword")], "Xác nhận lại mật khẩu sai"),
  otp: Yup.string().required("Vui lòng nhập mã OTP"),
});

const ForgotPasswordForm = () => {
  const { formState, control, trigger, getValues } = useForm({
    mode: "all",
    defaultValues: {
      newPassword: "",
      confirm: "",
      otp: "",
    },
    resolver: yupResolver(schema),
  });
  const [isSending, setSending] = useState(false);
  const [isSubmitting, setSubmitting] = useState(false);
  const { email } = useSelector(forgotPasswordSelector);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const handleResendOtp = async () => {
    setSending(true);
    await getOtp(email);
    setSending(false);
  };

  const handleSubmit = async () => {
    if (await trigger()) {
      setSubmitting(true);
      const { newPassword, otp } = getValues();
      forgotPassword(email, newPassword, otp).then((res) => {
        if (res.isSuccess) {
          dispatch(
            showSnackbar({
              message: "Cập nhật tài khoản thành công",
              type: "success",
            })
          );
          navigate("/auth/signin?username=" + res.data.username);
        } else {
          let message = "";
          switch (res.message) {
            case "INVALID OTP":
              message = "Mã OTP không hợp lệ";
              break;
            case "TOO SHORT PASSWORD":
              message = "Mật khẩu quá ngắn";
              break;
            case "ACCOUNT NOT FOUND":
              message = "Không tìm thấy tài khoản";
              break;
            default:
              message = "Thất bại";
          }
          dispatch(showSnackbar({ message, type: "error" }));
          setSubmitting(false);
        }
      });
    }
  };

  return (
    <form className="">
      <div className="flex flex-col px-10 min-w-[400px]">
        <p className="text-sm mb-4 text-center">
          Mã OTP đã được gửi đến email của bạn
        </p>
        <Input
          label="Mật khẩu mới"
          name="newPassword"
          control={control}
          type="password"
        />
        <Input
          label="Xác nhận lại mật khẩu"
          name="confirm"
          control={control}
          type="password"
        />
        <Input label="Mã OTP" name="otp" control={control} type="text" />

        <button
          type="button"
          className="p-2 px-8 border border-secondary rounded-md flex items-center gap-2 mx-auto text-secondary"
          onClick={handleResendOtp}
          disabled={isSending}
        >
          <i className="bx bx-revision"></i>
          {isSending ? "Đang gửi..." : "Gửi lại mã"}
        </button>
      </div>
      <button
        type="button"
        className="mt-4 mb-3 mx-auto p-3 bg-primary text-white rounded-md block"
        onClick={handleSubmit}
        disabled={isSubmitting}
      >
        {isSubmitting ? "Đang xử lí...." : "Đổi mật khẩu"}
      </button>
    </form>
  );
};

const Input = ({ name, control, label, ...props }) => {
  const { field, fieldState } = useController({ name, control });
  return (
    <div className="mb-2">
      <p className="font-bold">{label}</p>
      <div
        className={`rounded-md border-[2px] px-3 py-3 text-black w-full flex items-center ${
          fieldState.error && "border-red-400"
        } `}
      >
        <input className=" outline-none flex-1" {...field} {...props} />
        {fieldState.error && (
          <i className="bx bx-error-circle text-red-400"></i>
        )}
      </div>
      {fieldState.error?.type !== "required" && (
        <p className="text-red-400 text-sm">{fieldState.error?.message}</p>
      )}
    </div>
  );
};
export default ForgotPasswordForm;

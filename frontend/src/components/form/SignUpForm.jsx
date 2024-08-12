import { useController, useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as Yup from "yup";
import { useState } from "react";
import { getOtp, signUp } from "../../api/AuthService";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { showSnackbar } from "../../redux/snackbarSlice";

const schema = Yup.object({
  username: Yup.string()
    .required("Vui lòng điền username")
    .min(5, "Username tối thiếu 5 kí tự")
    .max(50, "Tối đa 50 kí tự"),
  password: Yup.string()
    .required("Vui lòng điền mật khẩu")
    .min(6, "Mật khẩu tối thiểu 6 kí tự"),
  confirm: Yup.string()
    .required()
    .oneOf([Yup.ref("password")], "Xác nhận mật khẩu không đúng"),
  name: Yup.string()
    .required("Vui lòng điền tên hiển thị")
    .max(50, "Tối đã 50 kí tự"),
  email: Yup.string()
    .email("Không đúng định dạng email")
    .required("Vui lòng nhập email"),
  otp: Yup.string().required("Vui lòng điền mã OTP"),
});

const SignUpForm = () => {
  const navigate = useNavigate();
  const { handleSubmit, getValues, control, trigger } = useForm({
    defaultValues: {
      username: "",
      password: "",
      confirm: "",
      name: "",
      email: "",
      otp: "",
    },
    mode: "all",
    resolver: yupResolver(schema),
  });
  const [otpNotify, setOtpNotify] = useState("Gửi mã OTP đến email!");
  const dispatch = useDispatch();

  const handleSendOtp = async (e) => {
    const isValid = await trigger("email", { shouldFocus: true });
    if (isValid) {
      setOtpNotify("Đang gửi...");
      try {
        await getOtp(getValues("email"));
      } catch (e) {
        console.log(e);
        setOtpNotify(`Gửi OTP thất bại. Gửi lại?`);
      }
      setOtpNotify(`Đã gửi đến ${getValues("email")}. Gửi lại?`);
    }
  };
  const submit = async (formValues) => {
    const { username, password, email, name, otp } = formValues;
    const res = await signUp(username, password, email, name, otp);
    if (res.isSuccess) {
      dispatch(
        showSnackbar({ message: "Đăng ký thành công", type: "success" })
      );
      navigate("/auth/signin");
    } else {
      let message = "";
      switch (res.message) {
        case "USED USERNAME":
          message = "Tên tài khoản đã được sử dụng";
          break;
        case "USED EMAIL":
          message = "Email đã được sử dụng";
          break;
        case "INVALID PASSWORD":
          message = "Mật khẩu quá ngắn";
          break;
        case "INVALID OTP":
          message = "Mã OTP không hợp lệ";
          break;
        default:
          message = "Đăng ký thất bại";
      }
      dispatch(showSnackbar({ message, type: "error" }));
    }
  };

  return (
    <form
      className="p-5 text-black flex flex-col"
      method="POST"
      onSubmit={handleSubmit(submit)}
    >
      <h1 className="text-secondary text-center text-5xl font-bold mb-8">
        Đăng ký
      </h1>
      <Input
        control={control}
        name="username"
        label="Tên tài khoản"
        placeholder="Nhập tên tài khoản"
      ></Input>
      <Input
        control={control}
        name="password"
        label="Mật khẩu"
        placeholder="Nhập mật khẩu"
        type="password"
      ></Input>
      <Input
        control={control}
        name="confirm"
        label="Xác nhận lại"
        placeholder="Xác nhận lại mật khẩu"
        type="password"
      ></Input>
      <Input
        control={control}
        name="name"
        label="Tên hiển thị"
        placeholder="Nhập tên hiển thị"
      ></Input>
      <Input
        control={control}
        name="email"
        label="Địa chỉ email"
        placeholder="Nhập email"
      ></Input>
      <Input
        control={control}
        name="otp"
        label="Mã OTP"
        placeholder="Nhập mã OTP"
      ></Input>
      <button
        type="button"
        className="hover:bg-gray-200 bg-gray-100 rounded-md outline-none px-3 py-2 flex items-center justify-center gap-3"
        onClick={handleSendOtp}
      >
        {otpNotify}
      </button>

      <button
        type="submit"
        className="bg-secondary text-white font-bold text-xl p-2 rounded-md my-4"
        onClick={handleSubmit}
      >
        Đăng ký
      </button>
      <p className="text-center">
        Đã có tài khoản?
        <Link
          className="text-primary font-bold ms-1 cursor-pointer"
          to="/auth/signin"
        >
          Đăng nhập
        </Link>
      </p>
    </form>
  );
};

const Input = ({ name, control, label, ...props }) => {
  const { field, fieldState } = useController({ name, control });
  return (
    <div className="mb-4">
      <p className="font-bold text-start mb-1">{label}</p>
      <div
        className={`rounded-md border-[2px] px-3 py-3 text-black w-full flex items-center ${
          fieldState.error && "border-red-400"
        } `}
      >
        <input
          className=" outline-none flex-1"
          placeholder="Ex: example@gmail.com"
          type="text"
          {...field}
          {...props}
        />
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

export default SignUpForm;

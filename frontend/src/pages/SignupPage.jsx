import { useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getOtp, signUp } from "../api/AuthService";
import SignUpForm from "../components/form/SignUpForm";

const SignupPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [otp, setOtp] = useState("");
  const [otpNotify, setOtpNotify] = useState("Chọn gửi mã OTP đến email!");
  const [error, setError] = useState("");
  const otpBtnRef = useRef(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (password !== confirm) {
      setError("Xác nhận lại mật khẩu!");
      return;
    } else if (password.length < 6) {
      setError("Mật khẩu có ít nhất 6 kí tự");
      return;
    }
    try {
      const res = await signUp(username, password, email, name, otp);
      navigate("/auth/signin");
    } catch (e) {
      setError(e.response.data.message);
    }
  };
  const handleSendOtp = async (e) => {
    e.preventDefault();
    if (email.length == 0) {
      setError("Vui lòng điền email");
      return;
    }
    setOtpNotify("Đang gửi...");
    try {
      const res = await getOtp(email);
    } catch (e) {
      console.log(e);
      setOtpNotify(`Gửi OTP thất bại. Gửi lại?`);
    }
    setOtpNotify(`Đã gửi đến ${email}. Gửi lại?`);
  };

  return (
    <div className="w-screen h-screen grid place-items-center">
      <div className="w-8/12 h-[500px] bg-white m-auto rounded-md flex">
        <div className="w-1/2 h-full overflow-auto custom-scroll">
          <SignUpForm></SignUpForm>
        </div>
        <div className="w-1/2 h-full">
          <img src="/logo.png" alt="" className="w-full h-full object-cover" />
        </div>
      </div>
    </div>
  );
};

export default SignupPage;

import { useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getOtp, signUp } from "../api/AuthService";

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
        <form className="w-1/2 h-full p-5 text-black flex flex-col overflow-auto custom-scroll">
          <h1 className="text-secondary text-center text-5xl font-bold mb-3">
            Sign up
          </h1>
          <p className="font-bold text-start mb-1">Username</p>
          <input
            className="rounded-md border-[2px] outline-none px-2 py-3 text-black mb-2"
            placeholder="Ex: abc@"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <p className="font-bold text-start mb-1">Password</p>
          <input
            className="rounded-md border-[2px] outline-none px-2 py-3 text-black mb-2"
            placeholder="Ex: 123"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <p className="font-bold text-start mb-1">Confirm</p>
          <input
            className="rounded-md border-[2px] outline-none px-2 py-3 text-black mb-2"
            placeholder="Ex: 123"
            type="password"
            value={confirm}
            onChange={(e) => setConfirm(e.target.value)}
            required
          />
          <p className="font-bold text-start mb-1">Name</p>
          <input
            className="rounded-md border-[2px] outline-none px-2 py-3 text-black mb-2"
            placeholder="Ex: Nguyen Van A"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
          <p className="font-bold text-start mb-1">Email</p>
          <input
            className="rounded-md border-[2px] outline-none px-2 py-3 text-black mb-2"
            placeholder="Ex: example@gmail.com"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <p className="font-bold text-start mb-1">OTP</p>
          <input
            className="rounded-md border-[2px] outline-none px-2 py-3 text-black mb-2"
            placeholder="Ex: 12345"
            type="text"
            value={otp}
            onChange={(e) => setOtp(e.target.value)}
          />

          <button
            className="hover:bg-gray-200 bg-gray-100 rounded-md outline-none px-3 py-2 flex items-center justify-center gap-3"
            onClick={handleSendOtp}
          >
            {otpNotify}
          </button>

          <p className="text-red-400 text-sm">{error}</p>

          <button
            className="bg-secondary text-white font-bold text-xl p-2 rounded-md my-4"
            onClick={handleSubmit}
          >
            Sign up
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
        <div className="w-1/2 h-full">
          <img src="/logo.png" alt="" className="w-full h-full object-cover" />
        </div>
      </div>
    </div>
  );
};

export default SignupPage;

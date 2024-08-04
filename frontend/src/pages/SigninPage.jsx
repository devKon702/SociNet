import { useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { signIn } from "../api/AuthService";
import { authSelector } from "../redux/selectors";
import { setError, signinWithGoogleThunk } from "../redux/authSlice";
import { GoogleLogin } from "@react-oauth/google";
import { jwtDecode } from "jwt-decode";

const SigninPage = () => {
  const [searchParams] = useSearchParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const auth = useSelector(authSelector);
  const usernameInput = useRef(null);
  const passwordInput = useRef(null);

  const handleSignIn = async () => {
    const username = usernameInput.current.value;
    const password = passwordInput.current.value;
    if (!username) {
      dispatch(setError("Username must not be blank"));
      return;
    }
    await signIn(
      {
        username,
        password,
      },
      dispatch,
      navigate
    );
  };

  const handleSignInWithGoogle = (credentialResponse) => {
    const { email, name, picture, sub } = jwtDecode(
      credentialResponse.credential
    );
    dispatch(
      signinWithGoogleThunk({
        email,
        googleId: sub,
        name,
        avatarUrl: picture,
      })
    );
  };

  useEffect(() => {
    usernameInput.current.value = searchParams.get("username") || "";
  }, [searchParams]);

  useEffect(() => {
    if (auth.isAuthenticated) {
      if (auth.user.roles.includes("USER")) navigate("/");
      if (auth.user.roles.includes("ADMIN")) navigate("/admin");
    }
  }, [auth.isAuthenticated]);

  return (
    <div className="w-screen h-screen grid place-items-center">
      <div className="w-8/12 h-[500px] bg-white rounded-md flex">
        <div className="w-1/2 h-full">
          <img src="/logo.png" alt="" className="w-full h-full object-cover" />
        </div>
        {/* Form */}
        <div className="w-1/2 h-full p-5 text-black ">
          <h1 className="text-primary text-center text-5xl font-bold mb-3">
            Sign in
          </h1>
          <div className="w-full flex flex-col">
            <p className="font-bold text-start mb-1">Username</p>
            <input
              ref={usernameInput}
              className="rounded-md border-[2px] outline-none px-2 py-3 text-black mb-2"
              placeholder="Ex: abc@"
            />
            <p className="font-bold text-start mb-1">Password</p>
            <input
              ref={passwordInput}
              className="rounded-md border-[2px] outline-none px-2 py-3 text-black mb-2"
              placeholder="Ex: 123"
              type="password"
            />
            {auth.error ? (
              <p className="text-red-500">Đăng nhập thất bại</p>
            ) : null}
          </div>
          <Link
            to="/auth/forgot-password"
            className="inline-block text-gray-400 italic hover:underline cursor-pointer float-end"
          >
            Quên mật khẩu?
          </Link>
          <button
            className="inline-block w-full bg-primary text-white text-center font-bold text-xl p-2 rounded-md my-4"
            onClick={handleSignIn}
          >
            Sign in
          </button>
          <p className="text-gray-500 text-center">--------- Hoặc ---------</p>
          {/* <button
            className="flex items-center justify-center gap-4 w-full text-white bg-blue-500 text-center font-bold text-xl p-2 rounded-md my-4 shadow-2xl"
            onClick={handleSignInWithGoogle}
          >
            <i className="bx bxl-google text-2xl"></i>
            Đăng nhập với Google
          </button> */}
          <div className="flex justify-center my-3">
            <GoogleLogin
              onSuccess={handleSignInWithGoogle}
              onError={() => {
                console.log("Login Failed");
              }}
              text="Đăng nhập với Google"
            />
          </div>
          <p className="text-center">
            Chưa có tài khoản?
            <Link
              to="/auth/signup"
              className="text-[#65EEB7] font-bold ms-1 cursor-pointer"
            >
              Đăng ký
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default SigninPage;

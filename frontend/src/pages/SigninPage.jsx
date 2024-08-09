import { useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { signIn } from "../api/AuthService";
import { authSelector } from "../redux/selectors";
import {
  setError,
  signInThunk,
  signinWithGoogleThunk,
} from "../redux/authSlice";
import { GoogleLogin } from "@react-oauth/google";
import { jwtDecode } from "jwt-decode";
import SignInForm from "../components/form/SignInForm";

const SigninPage = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const auth = useSelector(authSelector);

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

  // useEffect(() => {
  //   if (auth.isAuthenticated) {
  //     if (auth.userrole.includes("USER")) navigate("/");
  //     if (auth.user.roles.includes("ADMIN")) navigate("/admin");
  //   }
  // }, [auth.isAuthenticated]);

  return (
    <div className="w-screen h-screen grid place-items-center">
      <div className="w-8/12 h-[500px] bg-white rounded-md flex">
        <div className="w-1/2 h-full">
          <img src="/logo.png" alt="" className="w-full h-full object-cover" />
        </div>
        {/* Form */}
        <div className="w-1/2 h-full p-5 text-black custom-scroll overflow-auto">
          <SignInForm></SignInForm>
        </div>
      </div>
    </div>
  );
};

export default SigninPage;

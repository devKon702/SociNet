import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as Yup from "yup";
import { useDispatch } from "react-redux";
import { signInThunk, signinWithGoogleThunk } from "../../redux/authSlice";
import { Link, useSearchParams } from "react-router-dom";
import { useEffect } from "react";
import { GoogleLogin } from "@react-oauth/google";
import { jwtDecode } from "jwt-decode";

const schema = Yup.object({
  username: Yup.string().required("Vui lòng điền tên tài khoản"),
  password: Yup.string().required("Vui lòng điền mật khẩu"),
});

const SignInForm = () => {
  const [searchParams] = useSearchParams();
  const dispatch = useDispatch();
  const {
    register,
    handleSubmit,
    formState: { errors },
    getValues,
    setValue,
  } = useForm({
    defaultValues: {
      username: "",
      password: "",
    },
    resolver: yupResolver(schema),
    mode: "onSubmit",
  });

  useEffect(() => {
    setValue("username", searchParams.get("username") || "");
  }, [searchParams]);

  const submit = () => {
    const { username, password } = getValues();
    dispatch(signInThunk({ username, password }));
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

  return (
    <form onSubmit={handleSubmit(submit)}>
      <div className="w-full flex flex-col">
        <h1 className="text-primary text-center text-5xl font-bold mb-10">
          Đăng nhập
        </h1>
        <div className="rounded-md border-[2px] outline-none p-3 text-gray-800 mb-2 flex items-center gap-3 w-full">
          <i className="bx bx-user text-xl"></i>
          <input
            autoFocus={true}
            className="outline-none flex-1"
            placeholder="Tên tài khoản"
            {...register("username")}
          />
        </div>
        {errors?.username && (
          <p className="text-red-400 text-sm mb-4">{errors.username.message}</p>
        )}
        <div className="rounded-md border-[2px] outline-none p-3 text-gray-800 mb-2 flex items-center gap-3">
          <i className="bx bx-lock text-xl"></i>
          <input
            type="password"
            className="outline-none flex-1"
            placeholder="Mật khẩu"
            {...register("password")}
          />
        </div>
        {errors?.password && (
          <p className="text-red-400 text-sm">{errors.password.message}</p>
        )}
      </div>
      <Link
        to="/auth/forgot-password"
        className="inline-block text-gray-400 italic hover:underline cursor-pointer float-end"
      >
        Quên mật khẩu?
      </Link>
      <button
        className="inline-block w-full bg-primary text-white text-center font-bold text-xl p-2 rounded-md my-4"
        type="submit"
      >
        Đăng nhập
      </button>
      <p className="text-gray-500 text-center">--------- Hoặc ---------</p>
      <div className="flex justify-center my-3 rounded-full overflow-hidden">
        <GoogleLogin
          onSuccess={handleSignInWithGoogle}
          onError={() => {
            console.log("Login Failed");
          }}
          text="Đăng nhập với Google"
          type="standard"
          theme="outline"
        />
      </div>
      <p className="text-center">
        Chưa có tài khoản?
        <Link
          to="/auth/signup"
          className="text-secondary font-bold ms-1 cursor-pointer"
        >
          Đăng ký
        </Link>
      </p>
    </form>
  );
};

export default SignInForm;

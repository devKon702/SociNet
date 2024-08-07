import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as Yup from "yup";

const schema = Yup.object({
  username: Yup.string().required("Vui lòng điền username"),
  password: Yup.string().required("Vui lòng điền mật khẩu"),
});

const SignInForm = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: {
      username: "",
      password: "",
    },
    resolver: yupResolver(schema),
    mode: "all",
  });

  const submit = () => {};

  return (
    <form onSubmit={handleSubmit(submit)}>
      <div className="w-full flex flex-col">
        <p className="font-bold text-start mb-1">Username</p>
        <input
          className="rounded-md border-[2px] outline-none px-2 py-3 text-black mb-2"
          placeholder="Ex: abc@"
          {...register("username")}
        />
        {errors?.username && (
          <p className="text-red-400 text-sm">{errors.username.message}</p>
        )}
        <p className="font-bold text-start mb-1">Password</p>
        <input
          className="rounded-md border-[2px] outline-none px-2 py-3 text-black mb-2"
          placeholder="Ex: 123"
          type="password"
          {...register("password")}
        />
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
        Sign in
      </button>
    </form>
  );
};

export default SignInForm;

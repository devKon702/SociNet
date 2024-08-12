import SignInForm from "../components/form/SignInForm";

const SigninPage = () => {
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

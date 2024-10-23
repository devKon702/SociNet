import SignInForm from "../components/form/SignInForm";

const SigninPage = () => {
  return (
    <div className="w-screen h-screen grid place-items-center">
      <div className="w-full sm:w-8/12 h-full sm:h-[500px] bg-white rounded-xl flex items-center overflow-hidden">
        <div className="hidden w-1/2 h-full md:block">
          <img src="/logo.png" alt="" className="w-full h-full object-cover" />
        </div>
        {/* Form */}
        <div className="w-full md:w-1/2 h-full p-5 text-black custom-scroll overflow-x-hidden overflow-y-auto grid place-items-center">
          <SignInForm></SignInForm>
        </div>
      </div>
    </div>
  );
};

export default SigninPage;

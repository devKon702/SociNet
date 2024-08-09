import { useRouteError } from "react-router-dom";

const ErrorPage = ({ error }) => {
  console.log({ error });
  return (
    <div className="w-screen h-screen flex flex-col items-center justify-center gap-5">
      <p className="font-bold text-7xl">Oops!</p>
      <p className="opacity-50 text-center">
        Something went wrong because of us. <br /> Sorry about that...
      </p>
      <button
        className="bg-primary px-4 py-2 rounded-sm"
        onClick={() => location.replace("/")}
      >
        BACK
      </button>
    </div>
  );
};

export default ErrorPage;

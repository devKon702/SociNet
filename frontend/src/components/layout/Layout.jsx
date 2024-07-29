import { Outlet } from "react-router-dom";
import Header from "./Header";

const Layout = () => {
  return (
    <div className="container">
      <div className="h-full bg-white rounded-md mx-auto overflow-hidden flex flex-col">
        <Header></Header>
        <div className="custom-scroll flex-1 overflow-y-auto overflow-x-hidden">
          <Outlet></Outlet>
        </div>
      </div>
    </div>
  );
};

export default Layout;

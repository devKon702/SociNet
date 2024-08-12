import { Outlet } from "react-router-dom";
import Header from "./Header";
import { useDispatch } from "react-redux";
import { useEffect } from "react";
import { clearConversation } from "../../redux/realtimeSlice";

const Layout = () => {
  const dispatch = useDispatch();
  useEffect(() => {
    dispatch(clearConversation());
  }, []);

  return (
    <div className="container">
      <div className="h-full bg-white rounded-md mx-auto overflow-hidden flex flex-col gap-2">
        <Header></Header>
        <div className="custom-scroll flex-1 overflow-y-auto overflow-x-hidden">
          <Outlet></Outlet>
        </div>
      </div>
    </div>
  );
};

export default Layout;

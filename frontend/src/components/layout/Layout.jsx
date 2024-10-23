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
    <div className="flex flex-col h-screen overflow-hidden">
      <Header></Header>
      <div className="md:px-10 pt-4 pb-2 flex-1 overflow-hidden bg-lightGray">
        <Outlet></Outlet>
      </div>
    </div>
  );
};

export default Layout;

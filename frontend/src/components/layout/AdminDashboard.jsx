import { NavLink } from "react-router-dom";

const AdminDashboard = () => {
  return (
    <div className="flex flex-col w-3/12 h-full bg-slate-700 text-white font-bold py-4 border-t-2 border-t-gray-600">
      <NavLink
        to={"/admin/home"}
        className={({ isActive }) =>
          `${isActive ? "bg-primary" : "hover:bg-gray-600"} py-4 px-6`
        }
      >
        <i className="bx bxs-home-alt-2 mr-2"></i>
        Trang chủ
      </NavLink>
      <NavLink
        to={"/admin/posts"}
        className={({ isActive }) =>
          `${isActive ? "bg-primary" : "hover:bg-gray-600"} py-4 px-6`
        }
      >
        <i className="bx bxs-card mr-2"></i>
        Quản lý bài viết
      </NavLink>
      <NavLink
        to={"/admin/users"}
        className={({ isActive }) =>
          `${isActive ? "bg-primary" : "hover:bg-gray-600"} py-4 px-6`
        }
      >
        <i className="bx bxs-user-account mr-2"></i>
        Quản lý người dùng
      </NavLink>
    </div>
  );
};

export default AdminDashboard;

import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { userInfoSelector } from "../../redux/selectors";
import { signOut } from "../../api/AuthService";
import { ClickAwayListener } from "@mui/base/ClickAwayListener";

const Header = () => {
  const [isShow, setIsShow] = useState(false);
  const [showNotify, setShowNotify] = useState(false);
  const user = useSelector(userInfoSelector);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  return (
    <div className="w-full h-[50px] flex justify-between items-center bg-slate-700 py-3 px-10">
      <Link
        className="h-full w-fit flex justify-around cursor-pointer gap-1"
        to="/home"
      >
        <div className="h-full">
          <img
            src="/logo-withoutbg.png"
            alt=""
            className="w-full h-full object-contain"
          />
        </div>
        <div className="h-full">
          <img
            src="/logo-name-withoutbg.png"
            alt=""
            className="w-full h-full object-contain"
          />
        </div>
      </Link>
      <div className="flex gap-4 items-center">
        <ClickAwayListener onClickAway={() => setShowNotify(false)}>
          <div
            className="relative cursor-pointer"
            onClick={() => setShowNotify(true)}
          >
            <i className="bx bxs-bell text-2xl"></i>
            <div className="rounded-full size-4 bg-red-500 absolute top-0 -right-1.5 text-xs flex items-center justify-center">
              2
            </div>
            {showNotify && (
              <div className="absolute right-0 bg-gray-800 w-[350px] rounded-md p-2 flex flex-col gap-2 z-50">
                <div className="hover:bg-gray-700 rounded-md p-2 cursor-default">
                  <div className="flex gap-2 items-center">
                    <div className="rounded-full size-14 overflow-hidden">
                      <img
                        src="/dev1.png"
                        alt=""
                        className="w-full h-full object-cover"
                      />
                    </div>
                    <div className="flex-1">
                      <p>
                        <Link
                          to="/user/2"
                          className="hover:underline font-bold"
                        >
                          Nguyễn Nhật Kha
                        </Link>{" "}
                        vừa gửi lời mời kết bạn
                      </p>
                      <p className="text-sm text-gray-400">1 giờ trước</p>
                    </div>
                  </div>
                  <div className="flex justify-end gap-4 mt-2">
                    <button className="p-2 px-3 rounded-md border border-red-500 text-red-500">
                      Từ chối
                    </button>
                    <button className="p-2 px-3 rounded-md bg-third">
                      Chấp nhận
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </ClickAwayListener>
        <div
          className="flex gap-4 items-center cursor-pointer popup-container"
          onClick={() => setIsShow(!isShow)}
        >
          <span className="font-bold">{user.name}</span>
          <div className="size-8 rounded-full overflow-hidden">
            <img
              src={user.avatarUrl ? user.avatarUrl : "/unknown-avatar.png"}
              alt=""
              className="w-full h-full object-cover"
            />
          </div>

          <div className="popup top-full right-0 rounded-lg bg-gray-700 p-3 w-[200px] shadow-md z-50">
            <Link
              to="/account"
              className="hover:bg-gray-600 rounded-md p-3 flex items-center gap-2"
            >
              <i className="bx bxs-id-card text-xl"></i>
              Tài khoản
            </Link>
            <button
              className="hover:bg-gray-600 rounded-md p-3 flex items-center gap-2 w-full"
              onClick={() => signOut(dispatch, navigate)}
            >
              <i className="bx bx-log-out-circle text-xl"></i>
              Đăng xuất
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Header;

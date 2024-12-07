import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { realtimeSelector, userInfoSelector } from "../../redux/selectors";
import { signOut } from "../../api/AuthService";
import { ClickAwayListener } from "@mui/base/ClickAwayListener";
import InvitationItem from "../notify/InvitationItem";
import { setNewInvitationNumber } from "../../redux/realtimeSlice";
import { useDebounce, useWindowSize } from "@uidotdev/usehooks";
import { getUsersByName } from "../../api/UserService";
import { Collapse } from "@mui/material";

const Header = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector(userInfoSelector);
  const { invitations, newInvitationNumber } = useSelector(realtimeSelector);
  const [showNotify, setShowNotify] = useState(false);
  const [isSearching, setSearching] = useState(false);
  const [searchValue, setSearchValue] = useState("");
  const searchValueDebouced = useDebounce(searchValue, 500);
  const [searchedUserList, setSearchedUserList] = useState([]);
  const windowSize = useWindowSize();

  useEffect(() => {
    if (searchValueDebouced) {
      getUsersByName(searchValueDebouced).then((res) => {
        setSearchedUserList(res.data);
      });
    } else {
      setSearchedUserList([]);
    }
  }, [searchValueDebouced]);

  return (
    <div className="w-full h-[60px] flex justify-between items-center bg-slate-700 py-3 px-10 rounded-b-xl">
      <Link
        className={`h-full w-fit flex justify-around cursor-pointer gap-1 ${
          (isSearching || searchValue != "") && windowSize.width <= 640
            ? "w-0"
            : ""
        }`}
        to="/home"
      >
        <div className="h-full hidden sm:inline-block">
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
      <ClickAwayListener
        onClickAway={() => {
          setSearching(false);
        }}
      >
        <div
          className={`w-fit min-w-10 h-10 rounded-3xl bg-slate-500 flex items-center justify-center gap-2 relative`}
        >
          <div
            className={`p-3 overflow-hidden w-10 h-10 ${
              isSearching || searchValue != "" ? "w-[270px]" : ""
            } bg-slate-500 shadow-[2px_2px_20px_rgba(0,0,0,0.08)] rounded-full flex group items-center hover:duration-300 duration-300 cursor-pointer`}
            onClick={() => setSearching(true)}
          >
            <div className="flex items-center justify-center fill-white relative">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                id="Isolation_Mode"
                data-name="Isolation Mode"
                viewBox="0 0 24 24"
                width="16"
                height="16"
              >
                <path d="M18.9,16.776A10.539,10.539,0,1,0,16.776,18.9l5.1,5.1L24,21.88ZM10.5,18A7.5,7.5,0,1,1,18,10.5,7.507,7.507,0,0,1,10.5,18Z"></path>
              </svg>
            </div>
            <input
              type="text"
              className="outline-none bg-transparent w-full text-white font-normal px-4"
              placeholder="Tìm kiếm"
              onChange={(e) => setSearchValue(e.target.value)}
            />
            {isSearching && searchValue != "" && (
              <div className="absolute top-full left-0 rigth-0 bg-gray-700 rounded-md shadow-lg z-30 px-2 w-full">
                <div className="custom-scroll p-4 max-h-[200px] overflow-auto flex flex-col gap-2 w-full">
                  {searchedUserList.length == 0 ? (
                    <div className="opacity-50">Hiện không có thông tin</div>
                  ) : (
                    searchedUserList.map((item, index) => (
                      <Link
                        to={`/user/${item.id}`}
                        className="w-full rounded-md hover:bg-gray-600 flex gap-2 p-2 items-center"
                        key={index}
                        onClick={() => {
                          setSearchValue("");
                          setSearching(false);
                        }}
                      >
                        <img
                          src={item.avatarUrl || "/unknown-avatar.png"}
                          alt=""
                          className="rounded-full size-8"
                        />
                        <p>{item.name}</p>
                      </Link>
                    ))
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      </ClickAwayListener>
      <div
        className={`flex gap-4 items-center ${
          (isSearching || searchValue != "") && windowSize.width <= 400
            ? "w-0 overflow-hidden"
            : ""
        }`}
      >
        <ClickAwayListener onClickAway={() => setShowNotify(false)}>
          <div
            className="relative cursor-pointer"
            onClick={() => {
              setShowNotify(!showNotify);
              dispatch(setNewInvitationNumber(0));
            }}
          >
            <i className="bx bxs-bell text-2xl"></i>
            {newInvitationNumber !== 0 && (
              <div className="rounded-full size-4 bg-red-500 absolute top-0 -right-1.5 text-xs flex items-center justify-center">
                {newInvitationNumber}
              </div>
            )}
            {showNotify && (
              <div className="absolute right-0 bg-gray-700 w-[250px] sm:w-[350px] rounded-md p-2 flex flex-col gap-2 z-50">
                {invitations.length != 0 ? (
                  invitations.map((invite, index) => (
                    <InvitationItem
                      key={index}
                      invitation={invite}
                    ></InvitationItem>
                  ))
                ) : (
                  <p className="opacity-50 text-xl py-8 text-center">
                    Hiện chưa có thông báo nào
                  </p>
                )}
              </div>
            )}
          </div>
        </ClickAwayListener>
        <div className="flex gap-3 items-center cursor-pointer popup-container">
          <span className="font-bold hidden md:inline-block">{user.name}</span>
          <div className="size-8 rounded-full overflow-hidden">
            <img
              src={user.avatarUrl ? user.avatarUrl : "/unknown-avatar.png"}
              alt=""
              className="w-full h-full object-cover"
            />
          </div>

          <div className="popup top-full right-0 rounded-lg bg-gray-700 p-3 w-[200px] shadow-md z-50">
            <Link
              to={`/user/${user.id}`}
              className="hover:bg-gray-600 rounded-md p-3 flex items-center gap-2"
            >
              <i className="bx bxs-home-circle text-xl"></i>
              Trang cá nhân
            </Link>
            <Link
              to="/account"
              className="hover:bg-gray-600 rounded-md p-3 flex items-center gap-2"
            >
              <i className="bx bxs-id-card text-xl"></i>
              Tài khoản
            </Link>
            <button
              className="hover:bg-gray-600 rounded-md p-3 flex items-center gap-2 w-full"
              onClick={() => {
                const check = confirm("Bạn chắc chắc muốn đăng xuất");
                if (check) signOut(dispatch, navigate);
              }}
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

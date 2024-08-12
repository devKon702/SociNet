import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { realtimeSelector, userInfoSelector } from "../../redux/selectors";
import { signOut } from "../../api/AuthService";
import { ClickAwayListener } from "@mui/base/ClickAwayListener";
import InvitationItem from "../notify/InvitationItem";
import { setNewInvitationNumber } from "../../redux/realtimeSlice";
import { useDebounce } from "@uidotdev/usehooks";
import { getUsersByName } from "../../api/UserService";

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
      <ClickAwayListener
        onClickAway={() => {
          setSearching(false);
        }}
      >
        <div className="rounded-2xl py-2 px-4 w-1/4 bg-slate-500 flex items-center gap-2 relative">
          <i className="bx bx-search"></i>
          <input
            value={searchValue}
            readOnly={true}
            type="text"
            placeholder="Tìm kiếm người dùng"
            className="bg-transparent outline-none flex-1"
            onFocus={() => setSearching(true)}
          />
          {isSearching && (
            <div className="absolute top-0 -left-2 -right-2 bg-gray-700 rounded-md shadow-lg z-30 px-2">
              <div className="rounded-2xl py-2 px-4 bg-slate-500 flex items-center gap-2">
                <i className="bx bx-search"></i>
                <input
                  value={searchValue}
                  onChange={(e) => setSearchValue(e.target.value)}
                  type="text"
                  placeholder="Tìm kiếm người dùng"
                  className="bg-transparent outline-none flex-1"
                  autoFocus={true}
                />
              </div>
              <div className="custom-scroll p-4 max-h-[200px] overflow-auto flex flex-col gap-2">
                {searchedUserList.length == 0 ? (
                  <p className="opacity-50">Hiện không có thông tin</p>
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
      </ClickAwayListener>
      <div className="flex gap-4 items-center">
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
              <div className="absolute right-0 bg-gray-700 w-[350px] rounded-md p-2 flex flex-col gap-2 z-50">
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
        <div className="flex gap-4 items-center cursor-pointer popup-container">
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

import React, { useEffect, useRef, useState } from "react";
import { Link, NavLink, Outlet, useParams } from "react-router-dom";
import CreatePostDialog from "../components/dialog/CreatePostDialog";
import UserInfoDIalog from "../components/dialog/UserInfoDIalog";
import { useDispatch, useSelector } from "react-redux";
import { userInfoSelector } from "../redux/selectors";
import { getUserInfo } from "../api/UserService";
import {
  createInvitationThunk,
  getPersonalInfo,
  getPersonalPost,
  preparePersonalInfoThunk,
  resetPersonalState,
} from "../redux/personalSlice";
import { setAction } from "../redux/postSlice";

const PersonalPage = () => {
  const { id } = useParams();
  const dispatch = useDispatch();
  const [isShowCreatePostModal, setShowCreatePostModal] = useState(false);
  const [isShowUpdateInfoModal, setShowUpdateInfoModal] = useState(false);
  const { user, friendStatus } = useSelector((state) => state.personal);
  const currentUser = useSelector((state) => state.auth?.user?.user);
  useEffect(() => {
    dispatch(resetPersonalState());
    dispatch(preparePersonalInfoThunk(id));
    return () => {
      dispatch(resetPersonalState());
    };
  }, [id]);

  const handleInvite = () => {
    dispatch(createInvitationThunk(id));
  };

  if (!user) return <></>;
  return (
    <div className="h-full overflow-y-scroll custom-scroll">
      {isShowCreatePostModal ? (
        <CreatePostDialog
          handleClose={() => {
            dispatch(setAction({ create: "", share: "" }));
            setShowCreatePostModal(false);
          }}
        ></CreatePostDialog>
      ) : null}

      {isShowUpdateInfoModal ? (
        <UserInfoDIalog
          user={user}
          handleClose={() => setShowUpdateInfoModal(false)}
        ></UserInfoDIalog>
      ) : null}
      <section className="bg-white w-full rounded-lg p-4 text-gray-800">
        <div className="flex flex-col items-center sm:flex-row sm:items-start gap-5 mb-4">
          <div className="rounded-full size-40 overflow-hidden">
            <img
              src={user.avatarUrl || "/unknown-avatar.png"}
              alt=""
              className="object-cover w-full h-full"
            />
          </div>
          <div>
            <p className="text-4xl font-bold mb-4">{user?.name}</p>
            <div className="grid grid-rows-3 grid-cols-1">
              <InforItem
                icon="bx bxs-user"
                label="Giới tính:"
                info={
                  user?.isMale ? (
                    <span>
                      Nam<i className="bx bx-male-sign ml-1 text-blue-500"></i>
                    </span>
                  ) : (
                    <span>
                      Nữ<i className="bx bx-female-sign ml-1 text-pink-500"></i>
                    </span>
                  )
                }
              />
              <InforItem
                icon="bx bxs-school"
                label="Đã học tại:"
                info={user?.school}
              />
              <InforItem
                icon="bx bxs-phone"
                label="Số điện thoại:"
                info={user?.phone}
              />
              <InforItem
                icon="bx bxs-map"
                label="Đến từ:"
                info={user?.address}
              />
            </div>
          </div>
          {user.id == currentUser.id ? (
            <div className="sm:ml-auto flex flex-col gap-3 items-stretch w-full sm:w-fit">
              <button
                className="bg-gray-200 p-3 rounded-md flex items-center justify-center gap-2"
                onClick={() => setShowUpdateInfoModal(true)}
              >
                <i className="bx bxs-edit-alt text-xl"></i>Chỉnh sửa
              </button>
              <button
                className="bg-secondary text-white p-3 rounded-md flex items-center justify-center gap-2"
                onClick={() => setShowCreatePostModal(true)}
              >
                <i className="bx bxs-duplicate text-xl"></i>Tạo bài đăng
              </button>
            </div>
          ) : (
            <div className="sm:ml-auto flex flex-col gap-3 items-stretch w-full sm:w-fit">
              {friendStatus === "FRIEND" && (
                <div className="bg-gray-200 p-3 rounded-md flex items-center justify-center gap-2 font-bold">
                  <i className="bx bxs-user-check text-xl"></i>Bạn bè
                </div>
              )}
              {friendStatus === "NO" && (
                <button
                  className="bg-blue-400 text-white font-bold p-3 rounded-md flex items-center justify-center gap-2"
                  onClick={handleInvite}
                >
                  <i className="bx bxs-user-plus text-xl"></i>Mời kết bạn
                </button>
              )}
              {friendStatus === "INVITED" && (
                <div className="bg-gray-200 font-bold p-3 rounded-md flex items-center justify-center gap-2">
                  <i className="bx bxs-user-check text-xl"></i>Đã gửi lời mời
                </div>
              )}
              <Link
                className="bg-secondary text-white font-bold p-3 rounded-md flex items-center justify-center gap-2"
                to={`/conversation/${id}`}
              >
                <i className="bx bxs-message-rounded-dots"></i>Nhắn tin
              </Link>
            </div>
          )}
        </div>
        <div className="divider"></div>
        <div className="py-3 text-center">
          <NavLink
            to={"posts"}
            className={({ isActive }) =>
              isActive
                ? "text-primary text-xl px-4 py-3 border-b-primary border-b-[4px]"
                : "hover:bg-gray-200 rounded-md px-4 py-3 text-xl"
            }
          >
            Bài viết
          </NavLink>
          <NavLink
            to={"friends"}
            className={({ isActive }) =>
              isActive
                ? "text-primary text-xl px-4 py-3 border-b-primary border-b-[4px]"
                : "hover:bg-gray-100 rounded-md px-4 py-3 text-xl"
            }
          >
            Bạn bè
          </NavLink>
        </div>
      </section>

      <section className="md:px-32 lg:px-64 bg-white mt-4 rounded-lg">
        <Outlet></Outlet>
      </section>
    </div>
  );
};

const InforItem = ({ label, info, icon }) => {
  return (
    <div className="flex items-center gap-2">
      <i className={icon}></i>
      <span className="font-bold">{label}</span>
      <span>{info}</span>
    </div>
  );
};

export default PersonalPage;

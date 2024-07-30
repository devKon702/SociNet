import React, { useEffect, useRef, useState } from "react";
import { NavLink, Outlet, useParams } from "react-router-dom";
import CreatePostDialog from "../components/dialog/CreatePostDialog";
import UserInfoDIalog from "../components/dialog/UserInfoDIalog";
import { useDispatch, useSelector } from "react-redux";
import { userInfoSelector } from "../redux/selectors";
import { getUserInfo } from "../api/UserService";
import { getPersonalInfo, getPersonalPost } from "../redux/personalSlice";

const PersonalPage = () => {
  const { id } = useParams();
  const dispatch = useDispatch();
  const [isShowCreatePostModal, setShowCreatePostModal] = useState(false);
  const [isShowUpdateInfoModal, setShowUpdateInfoModal] = useState(false);
  const user = useSelector((state) => state.personal.user);
  const currentUser = useSelector((state) => state.auth?.user?.user);
  useEffect(() => {
    dispatch(getPersonalInfo(id));
    dispatch(getPersonalPost(id));
  }, [dispatch, id]);

  if (!user) return <></>;
  return (
    <div className="px-4 py-3 bg-gray-300">
      <div className="flex h-full bg-slate-200">
        {isShowCreatePostModal ? (
          <CreatePostDialog
            handleClose={() => setShowCreatePostModal(false)}
          ></CreatePostDialog>
        ) : null}

        {isShowUpdateInfoModal ? (
          <UserInfoDIalog
            user={user}
            handleClose={() => setShowUpdateInfoModal(false)}
          ></UserInfoDIalog>
        ) : null}
      </div>
      <section className="bg-white w-full rounded-lg p-4 text-gray-800">
        <div className="flex items-start gap-5 mb-4">
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
                info={user?.isMale}
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
          {user.id === currentUser.id ? (
            <div className="ml-auto flex flex-col gap-3">
              <button
                className="bg-gray-200 p-3 rounded-md flex items-center gap-2"
                onClick={() => setShowUpdateInfoModal(true)}
              >
                <i className="bx bxs-edit-alt text-xl"></i>Chỉnh sửa
              </button>
              <button
                className="bg-secondary p-3 rounded-md flex items-center gap-2"
                onClick={() => setShowCreatePostModal(true)}
              >
                <i className="bx bxs-duplicate text-xl"></i>Tạo bài đăng
              </button>
            </div>
          ) : (
            <div className="ml-auto flex flex-col gap-3">
              <button className="bg-gray-200 p-3 rounded-md flex items-center gap-2">
                <i className="bx bxs-user-check text-xl"></i>Bạn bè
              </button>
              <button className="bg-blue-400 text-white font-bold p-3 rounded-md flex items-center gap-2">
                <i className="bx bxs-user-plus text-xl"></i>Mời kết bạn
              </button>
              <button className="bg-secondary text-white font-bold p-3 rounded-md flex items-center gap-2">
                <i className="bx bxs-message-rounded-dots"></i>Nhắn tin
              </button>
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

      <section className="px-32 bg-white mt-4 rounded-lg">
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
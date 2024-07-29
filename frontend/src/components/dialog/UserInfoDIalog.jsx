import React, { useState } from "react";
import Dialog from "./Dialog";
import { updateUserInfo } from "../../api/UserService";
import { useDispatch } from "react-redux";
import { getPersonalInfo, getPersonalPost } from "../../redux/personalSlice";

const UserInfoDIalog = ({ user, handleClose }) => {
  const [name, setName] = useState(user.name);
  const [school, setSchool] = useState(user.school);
  const [phone, setPhone] = useState(user.phone);
  const [file, setFile] = useState(null);
  const [avatarSrc, setAvatarSrc] = useState(user.avatarUrl);
  const dispatch = useDispatch();

  function handleFileChange(e) {
    const file = e.target.files[0];
    setFile(file);
    // const fileSize = (file.size / 1024 / 1024).toFixed(2);
    if (file.type.startsWith("image/")) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setAvatarSrc(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  }

  function handleUpdate() {
    updateUserInfo(name, phone, school, null, null, file).then((res) => {
      dispatch(getPersonalInfo(user.id));
      dispatch(getPersonalPost(user.id));
    });
    handleClose();
  }

  return (
    <Dialog title="Thông tin cá nhân" handleClose={handleClose}>
      <div className="w-[600px] flex-1 p-3 overflow-auto custom-scroll">
        <p className="font-bold text-2xl">Avatar</p>
        <div className="rounded-full overflow-hidden w-[200px] h-[200px] mx-auto relative">
          <img
            src={avatarSrc || "/unknown-avatar.png"}
            alt=""
            className="w-full h-full object-cover"
          />
          <label
            htmlFor="input-avatar"
            className="bg-black opacity-0 absolute inset-0 flex items-center justify-center cursor-pointer hover:opacity-80"
          >
            <i className="bx bxs-pencil text-white text-3xl"></i>
          </label>
          <input
            type="file"
            id="input-avatar"
            hidden={true}
            onChange={handleFileChange}
          />
        </div>
        <p className="font-bold text-2xl">Tên hiển thị</p>
        <input
          type="text"
          className="bg-slate-200 p-2 outline-none w-full mb-4"
          value={name || ""}
          onChange={(e) => setName(e.target.value)}
        />
        {/* <p className="font-bold text-2xl">Ngày sinh</p>
        <input
          type="text"
          className="bg-slate-200 p-2 outline-none w-full mb-4"
          placeholder="dd/mm/yyyy"
        /> */}
        <p className="font-bold text-2xl">Trường</p>
        <input
          type="text"
          className="bg-slate-200 p-2 outline-none w-full mb-4"
          placeholder="Trường THPT A"
          value={school || ""}
          onChange={(e) => setSchool(e.target.value)}
        />
        <p className="font-bold text-2xl">Số điện thoại</p>
        <input
          type="text"
          className="bg-slate-200 p-2 outline-none w-full mb-4"
          placeholder="0123456xxx"
          value={phone || ""}
          onChange={(e) => setPhone(e.target.value)}
        />
      </div>
      <button
        className="w-full py-2 bg-secondary text-white font-bold rounded-md"
        onClick={handleUpdate}
      >
        Cập nhật
      </button>
    </Dialog>
  );
};

export default UserInfoDIalog;

import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as Yup from "yup";
import { updateUserInfo } from "../../api/UserService";
import { useDispatch, useSelector } from "react-redux";
import { getPersonalInfo, getPersonalPost } from "../../redux/personalSlice";
import { authSelector } from "../../redux/selectors";
import { setUserInfo } from "../../redux/authSlice";

const schema = Yup.object({
  name: Yup.string().max(50, "Tên hiển thị không quá 50 kí tự"),
  phone: Yup.string(),
  school: Yup.string(),
  address: Yup.string(),
  genre: Yup.string().oneOf(["male", "female"]),
  file: Yup.mixed().test("fileSize", "File must be < 3MB", (value) => {
    if (!value) return true;
    const fileSize = value[0]?.size;
    return !fileSize || fileSize <= 3 * 1024 * 1024;
  }),
});

const UpdateInfoForm = ({ user, handleClose }) => {
  const {
    register,
    formState: { errors },
    getValues,
    watch,
  } = useForm({
    mode: "onChange",
    defaultValues: {
      name: user.name,
      phone: user.phone,
      school: user.school,
      address: user.address,
      genre: user.isMale ? "male" : "female",
      file: null,
    },
    resolver: yupResolver(schema),
  });
  const dispatch = useDispatch();
  const auth = useSelector(authSelector);
  const [avatarSrc, setAvatarSrc] = useState(user.avatarUrl);
  const fileWatch = watch("file");
  function handleUpdate() {
    const { name, phone, school, address, genre, file } = getValues();
    updateUserInfo(
      name,
      phone,
      school,
      address,
      genre == "male",
      file && file[0]
    ).then((res) => {
      console.log(res);

      if (res.isSuccess) {
        dispatch(getPersonalInfo(user.id));
        dispatch(getPersonalPost(user.id));
        if (user.id === auth.user.user.id) {
          dispatch(setUserInfo(res.data));
        }

        handleClose();
      } else {
        console.log(res);
      }
    });
  }

  useEffect(() => {
    console.log(fileWatch);

    if (!fileWatch || !fileWatch.length) {
      setAvatarSrc(user.avatarUrl);
      return;
    }
    const reader = new FileReader();
    reader.onload = (e) => {
      setAvatarSrc(e.target.result);
    };
    reader.readAsDataURL(fileWatch[0]);
  }, [fileWatch]);

  return (
    <form className="h-full overflow-auto custom-scroll">
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
            className={`bg-black absolute inset-0 flex items-center justify-center cursor-pointer ${
              errors.file ? "opacity-80" : "opacity-0 hover:opacity-80"
            }`}
          >
            {errors.file ? (
              <span className="text-red-400">Quá 3MB</span>
            ) : (
              <i className="bx bxs-pencil text-white text-3xl"></i>
            )}
          </label>
          <input
            type="file"
            id="input-avatar"
            hidden={true}
            {...register("file")}
            // onChange={handleFileChange}
            accept="image/*"
          />
        </div>
        <p className="font-bold text-2xl">Tên hiển thị</p>
        <input
          type="text"
          className="bg-slate-200 p-2 outline-none w-full mb-4"
          {...register("name")}
        />
        <p className="font-bold text-2xl">Trường</p>
        <input
          type="text"
          className="bg-slate-200 p-2 outline-none w-full mb-4"
          {...register("school")}
        />
        <p className="font-bold text-2xl">Số điện thoại</p>
        <input
          type="text"
          className="bg-slate-200 p-2 outline-none w-full mb-4"
          {...register("phone")}
        />
        <p className="font-bold text-2xl">Địa chỉ</p>
        <input
          type="text"
          className="bg-slate-200 p-2 outline-none w-full mb-4"
          {...register("address")}
        />
        <p className="font-bold text-2xl">Giới tính</p>
        <select
          className="bg-slate-200 p-2 outline-none w-full mb-4"
          {...register("genre")}
        >
          <option value="male">Nam</option>
          <option value="female">Nữ</option>
        </select>
      </div>
      <button
        type="button"
        className="w-full py-2 bg-secondary text-white font-bold rounded-md"
        onClick={handleUpdate}
      >
        Cập nhật
      </button>
    </form>
  );
};

export default UpdateInfoForm;

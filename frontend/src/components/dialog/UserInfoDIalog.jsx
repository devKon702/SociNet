import React, { useState } from "react";
import Dialog from "./Dialog";
import { updateUserInfo } from "../../api/UserService";
import { useDispatch } from "react-redux";
import { getPersonalInfo, getPersonalPost } from "../../redux/personalSlice";
import UpdateInfoForm from "../form/UpdateInfoForm";

const UserInfoDIalog = ({ user, handleClose }) => {
  return (
    <Dialog title="Thông tin giới thiệu" handleClose={handleClose}>
      <UpdateInfoForm user={user} handleClose={handleClose}></UpdateInfoForm>
    </Dialog>
  );
};

export default UserInfoDIalog;

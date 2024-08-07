import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  getPostListThunk,
  manageAccountThunk,
  setCurrentAccount,
} from "../../redux/adminSlice";
import { adminSelector } from "../../redux/selectors";

const AdminUserItem = ({ account }) => {
  const dispatch = useDispatch();
  const {
    account: { currentAccount },
  } = useSelector(adminSelector);
  const handleClick = (e) => {
    dispatch(setCurrentAccount(account));
    dispatch(getPostListThunk({ userId: account.user.id }));
  };
  return (
    <div
      className={`rounded-md flex justify-between p-4 items-center cursor-pointer ${
        currentAccount?.username == account.username
          ? "bg-primary text-white"
          : "bg-white hover:bg-gray-50"
      }`}
      onClick={handleClick}
    >
      <div className="flex gap-3 items-center">
        <img
          src={account.user.avatarUrl || "/unknown-avatar.png"}
          alt=""
          className="rounded-full size-7"
        />
        <p className="font-bold">{account.user.name}</p>
      </div>
      {account.isActive ? (
        <i
          className="bx bxs-lock-open-alt text-secondary text-xl rounded-full hover:bg-gray-100 size-8 grid place-items-center"
          onClick={(e) => {
            e.stopPropagation();
            dispatch(
              manageAccountThunk({
                username: account.username,
                isActive: false,
              })
            );
          }}
        ></i>
      ) : (
        <i
          className="bx bxs-lock-alt text-red-500 rounded-full hover:bg-gray-100 size-8 grid place-items-center"
          onClick={(e) => {
            e.stopPropagation();
            dispatch(
              manageAccountThunk({
                username: account.username,
                isActive: true,
              })
            );
          }}
        ></i>
      )}
    </div>
  );
};

export default AdminUserItem;

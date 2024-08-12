import React, { useState } from "react";
import { useDispatch } from "react-redux";
import { Link } from "react-router-dom";
import { dateFormated } from "../../helper";
import {
  responseInvitationThunk,
  setInvitationStatus,
} from "../../redux/realtimeSlice";

const InvitationItem = ({ invitation }) => {
  const dispatch = useDispatch();

  const handleReject = () => {
    dispatch(responseInvitationThunk({ invitation, isAccept: false }));
    dispatch(
      setInvitationStatus({ inviteId: invitation.id, status: "rejected" })
    );
  };

  const handleAccept = () => {
    dispatch(responseInvitationThunk({ invitation, isAccept: true }));
    dispatch(
      setInvitationStatus({ inviteId: invitation.id, status: "accepted" })
    );
  };

  return (
    <div className="hover:bg-gray-700 rounded-md p-2 cursor-default">
      <div className="flex gap-2 items-center">
        <div className="rounded-full size-14 overflow-hidden">
          <img
            src={invitation.user.avatarUrl || "/unknown-avatar.png"}
            alt=""
            className="w-full h-full object-cover"
          />
        </div>
        <div className="flex-1">
          <p>
            <Link
              to={`/user/${invitation.user.id}`}
              className="hover:underline font-bold"
            >
              {invitation.user.name}
            </Link>{" "}
            đã gửi lời mời kết bạn
          </p>
          <p className="text-sm text-gray-400">
            {dateFormated(invitation.createdAt)}
          </p>
        </div>
      </div>
      <div className="flex justify-end gap-4 mt-2">
        {invitation.status == "accepted" ? (
          <p className="text-sm text-gray-400">Đã chấp nhận lời mời</p>
        ) : invitation.status == "rejected" ? (
          <p className="text-sm text-gray-400">Đã từ chối lời mời</p>
        ) : (
          <>
            <button
              className="p-2 px-3 rounded-md border border-red-500 text-red-500"
              onClick={handleReject}
            >
              Từ chối
            </button>
            <button
              className="p-2 px-3 rounded-md bg-third"
              onClick={handleAccept}
            >
              Chấp nhận
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default InvitationItem;

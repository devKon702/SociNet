import React from "react";
import Dialog from "./Dialog";

const ConfirmDialog = ({ message, handleConfirm, handleClose }) => {
  return (
    <Dialog handleClose={handleClose} title="Xác nhận">
      <div>
        <p className="text-center">{message}</p>
        <div>
          <button className="border border-red-500 text-red-500 py-2 px-4">
            Hủy bỏ
          </button>
          <button className="bg-secondary text-white py-2 px-4">
            Xác nhận
          </button>
        </div>
      </div>
    </Dialog>
  );
};

export default ConfirmDialog;

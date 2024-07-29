import React from "react";
import { createPortal } from "react-dom";

const Dialog = ({ title, handleClose, ...props }) => {
  return (
    <>
      {createPortal(
        <div className="dialog fixed inset-0 flex justify-center items-center">
          <div
            className="wrapper opacity-70 bg-black absolute w-full h-full"
            onClick={handleClose}
          ></div>

          <div className="content w-fit h-5/6 rounded-2xl z-10 bg-white text-gray-700 flex flex-col relative">
            <button
              className="rounded-full absolute top-0 right-0 bg-slate-400 text-white p-3 size-10 translate-x-1/4 -translate-y-1/4 flex items-center justify-center"
              onClick={handleClose}
            >
              <i className="bx bx-x text-3xl"></i>
            </button>
            {/* Header */}
            <p className="font-bold text-3xl text-center py-2">{title}</p>
            <div className="divider"></div>
            {/* Content */}
            {props.children}
          </div>
        </div>,
        document.body
      )}
    </>
  );
};

export default Dialog;

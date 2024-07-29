import React from "react";

const AdminHeader = () => {
  return (
    <div className="w-full h-[50px] flex justify-between items-center bg-slate-700 py-3 px-10">
      <div className="h-full w-fit flex justify-around cursor-pointer gap-1">
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
      </div>
      <div className="flex gap-4 items-center">
        <div className="flex gap-4 items-center cursor-pointer relative">
          <span className="font-bold">Admin</span>
          <div className="size-8 rounded-full overflow-hidden">
            <img
              src="/dev1.png"
              alt=""
              className="w-full h-full object-cover"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminHeader;

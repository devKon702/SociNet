import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import useImageSelect from "../../hooks/useImageSelect";
import Dialog from "./Dialog";
import LoadingItem from "../common/LoadingItem";
import { updateRoomThunk } from "../../redux/realtimeSlice";
import { roomActivitySelector } from "../../redux/selectors";

const UpdateRoomDialog = ({ handleClose, room }) => {
  const dispatch = useDispatch();
  const { file, src, sizeError, handleFileChange } = useImageSelect({
    initialSrc: room.avatarUrl,
    maxSize: 5 * 1024 * 1024,
  });
  const [name, setName] = useState(room.name);
  const { fetchStatus } = useSelector(roomActivitySelector);

  function handleUpdate() {
    if (!name.trim() || sizeError) return;
    dispatch(updateRoomThunk({ roomId: room.id, name, file }));
  }

  return (
    <Dialog handleClose={handleClose} title="Thông tin nhóm">
      <>
        <div className="md:w-[550px]">
          <div className="mt-3">
            <input
              type="text"
              className="w-3/4 py-2 px-3 text-center rounded-lg outline-none border-[2px] mx-auto flex"
              placeholder="Tên nhóm"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div className="py-3">
            <label
              htmlFor="file-input"
              className="w-full hover:bg-gray-100 cursor-pointer p-3 flex items-center justify-center"
            >
              <i className="bx bxs-image-add text-2xl text-secondary"></i>
              Chọn ảnh đại diện
            </label>
            {src && (
              <div className="rounded-full overflow-hidden size-40 mx-auto flex mt-5 relative">
                <img src={src} alt="" className="size-full object-cover" />
                {sizeError && (
                  <div className="absolute inset-0 text-red-400 grid place-items-center bg-black bg-opacity-70">
                    Ảnh vượt 5MB
                  </div>
                )}
              </div>
            )}
            <input
              type="file"
              id="file-input"
              hidden={true}
              onChange={handleFileChange}
              accept="image/*"
            />
          </div>
        </div>
        <button
          className="w-11/12 mt-auto mx-auto mb-2 p-3 rounded-lg bg-green-400 text-white font-bold flex items-center justify-center outline-none"
          onClick={handleUpdate}
        >
          {fetchStatus === "UPDATING" ? (
            <LoadingItem></LoadingItem>
          ) : (
            "Cập nhật"
          )}
        </button>
      </>
    </Dialog>
  );
};

export default UpdateRoomDialog;

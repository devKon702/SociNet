import { useState } from "react";
import useImageSelect from "../../hooks/useImageSelect";
import Dialog from "./Dialog";
import { useDispatch } from "react-redux";
import { createRoomThunk } from "../../redux/realtimeSlice";
import LoadingItem from "../common/LoadingItem";

const CreateRoomDialog = ({ handleClose }) => {
  const dispatch = useDispatch();
  const { file, src, sizeError, handleFileChange } = useImageSelect({
    initialSrc: "",
    maxSize: 5 * 1024 * 1024,
  });
  const [name, setName] = useState("");
  const [loading, setLoading] = useState(false);

  function handleCreate() {
    setName(name.trim());
    if (name.length == 0 || !file) return;
    setLoading(true);
    dispatch(createRoomThunk({ name, file })).then(() => {
      setLoading(false);
      handleClose();
    });
  }

  return (
    <Dialog handleClose={handleClose} title="Tạo nhóm">
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
          className="w-11/12 mt-auto mx-auto mb-2 p-3 rounded-lg bg-green-400 text-white font-bold"
          onClick={handleCreate}
        >
          {loading ? <LoadingItem></LoadingItem> : "Tạo"}
        </button>
      </>
    </Dialog>
  );
};

export default CreateRoomDialog;

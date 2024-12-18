import { useOutletContext } from "react-router-dom";

const EmptyRoomPage = () => {
  const { toggleMenu } = useOutletContext();

  return (
    <div
      className="flex flex-col items-center justify-center h-full opacity-50 cursor-pointer"
      onClick={toggleMenu}
    >
      <i className="bx bx-search-alt text-[100px]"></i>
      <p className="text-2xl text-center">
        Chọn nhóm và bắt đầu cuộc trò chuyện
      </p>
    </div>
  );
};

export default EmptyRoomPage;

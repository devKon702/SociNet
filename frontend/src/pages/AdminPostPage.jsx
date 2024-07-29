const data = [
  {
    owner: "Nguyễn Nhật Kha",
    avatarUrl: "/dev1.png",
    caption: "Hôm nay đổi hình đại diện",
    imageUrl: "1098298.png",
    videoUrl: "",
    numberOfComment: 12,
    numberOfReaction: 31,
    isActive: true,
  },
  {
    owner: "Trịnh Đình Huy",
    avatarUrl: "/dev1.png",
    caption: "Sài gòn hôm nay có tin hot",
    imageUrl: "1098299.png",
    videoUrl: "",
    numberOfComment: 27,
    numberOfReaction: 40,
    isActive: true,
  },
  {
    owner: "Nguyễn Nhật Kha",
    avatarUrl: "/dev1.png",
    caption: "Mọi người đánh giá cái này như thế nào",
    imageUrl: "1082977.png",
    videoUrl: "",
    numberOfComment: 11,
    numberOfReaction: 5,
    isActive: false,
  },
];
const AdminPostPage = () => {
  return (
    <div className="text-gray-800">
      <input
        type="text"
        placeholder="Search..."
        className="bg-gray-200 py-2 px-3 rounded-lg w-full outline-none mb-6"
      />
      <table border="1" className="w-full">
        <thead>
          <tr className="text-left">
            <th className="px-2">Người đăng</th>
            <th>Caption</th>
            <th>Tệp</th>
            <th>Bình luận</th>
            <th>Tương tác</th>
            <th>Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          {data.map((row) => (
            <tr key={row.id} className="hover:bg-gray-200 border-t-[1px]">
              <td className="p-3 flex items-center">
                <div className="inline-block rounded-full size-8 overflow-hidden mr-2">
                  <img
                    src="/dev1.png"
                    alt=""
                    className="size-full object-cover"
                  />
                </div>
                {row.owner}
              </td>
              <td>{row.caption}</td>
              <td>{row.imageUrl}</td>
              <td>{row.numberOfComment}</td>
              <td>{row.numberOfReaction}</td>
              <td>
                {row.isActive ? (
                  <i className="bx bxs-lock-open text-secondary text-2xl cursor-pointer"></i>
                ) : (
                  <i className="bx bxs-lock text-red-400 text-2xl cursor-pointer"></i>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminPostPage;

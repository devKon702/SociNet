const data = [
  {
    id: 1,
    name: "John Doe",
    genre: "Male",
    birth: "Hồ Chí Minh",
    email: "john.doe@example.com",
    isActive: true,
  },
  {
    id: 2,
    name: "Jane Smith",
    genre: "Female",
    birth: "Hà Nội",
    email: "jane.smith@example.com",
    isActive: true,
  },
  {
    id: 3,
    name: "Sam Johnson",
    genre: "Male",
    birth: "Hà Tĩnh",
    email: "sam.johnson@example.com",
    isActive: false,
  },
  // Add more data as needed
];
const AdminUserPage = () => {
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
            <th className="px-2">Avatar</th>
            <th>Tên</th>
            <th>Giới tính</th>
            <th>Quê quán</th>
            <th>Email</th>
            <th>Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          {data.map((row) => (
            <tr key={row.id} className="hover:bg-gray-200 border-t-[1px]">
              <td className="p-2">
                <div className="inline-block rounded-full size-8 overflow-hidden">
                  <img
                    src="/dev1.png"
                    alt=""
                    className="size-full object-cover"
                  />
                </div>
              </td>
              <td>{row.name}</td>
              <td>{row.genre}</td>
              <td>{row.birth}</td>
              <td>{row.email}</td>
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

export default AdminUserPage;

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
    </div>
  );
};

export default AdminUserPage;

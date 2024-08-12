import ReactPaginate from "react-paginate";
import AdminPostItem from "../components/admin/AdminPostItem";
import AdminUserItem from "../components/admin/AdminUserItem";
import { useDispatch, useSelector } from "react-redux";
import { useEffect, useState } from "react";
import {
  clearPostList,
  getAccountListThunk,
  setCurrentAccount,
} from "../redux/adminSlice";
import { adminSelector } from "../redux/selectors";
import { useDebounce } from "@uidotdev/usehooks";

const AdminUserPage = () => {
  const dispatch = useDispatch();

  const [searchValue, setSearchValue] = useState("");
  const deboucedValue = useDebounce(searchValue, 500);

  const {
    account: { accountList, totalPage },
    post: { postList },
  } = useSelector(adminSelector);
  useEffect(() => {
    dispatch(getAccountListThunk({ name: deboucedValue }));
  }, [deboucedValue]);

  return (
    <>
      <div className="text-gray-800">
        <input
          value={searchValue}
          type="text"
          placeholder="Search..."
          className="bg-gray-200 py-2 px-3 rounded-lg w-full outline-none mb-6"
          onChange={(e) => setSearchValue(e.target.value)}
        />
        <div className="grid grid-cols-2 grid-rows-2 gap-3 min-h-36">
          {accountList.map((item, index) => (
            <AdminUserItem key={index} account={item}></AdminUserItem>
          ))}
        </div>
        <ReactPaginate
          breakLabel="..."
          nextLabel={
            <span className="size-8 bg-gray-300 grid place-items-center rounded-md">
              <i className="bx bx-chevron-right"></i>
            </span>
          }
          onPageChange={(e) => {
            dispatch(clearPostList());
            dispatch(setCurrentAccount(null));
            dispatch(getAccountListThunk({ name: "", page: e.selected }));
          }}
          pageRangeDisplayed={3}
          pageCount={totalPage}
          previousLabel={
            <span className="size-8 bg-gray-300 grid place-items-center rounded-md">
              <i className="bx bx-chevron-left"></i>
            </span>
          }
          containerClassName="flex gap-4 mx-auto items-center justify-center mt-4 mb-8"
          pageClassName="grid place-items-center hover:bg-gray-300 rounded-md"
          pageLinkClassName="px-3 py-2"
          activeClassName="rounded-md bg-primary hover:bg-primary text-white"
          initialPage={0}
        />
      </div>
      <div className="grid grid-cols-2 w-full gap-4">
        {postList.map((item, index) => (
          <AdminPostItem key={index} post={item}></AdminPostItem>
        ))}
      </div>
    </>
  );
};

export default AdminUserPage;

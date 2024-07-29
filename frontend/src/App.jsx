import { Navigate, Route, Routes, useNavigate } from "react-router-dom";
import SigninPage from "./pages/SigninPage";
import SignupPage from "./pages/SignupPage";
import HomePage from "./pages/HomePage";
import Layout from "./components/layout/Layout";
import PersonalPage from "./pages/PersonalPage";
import AccountPage from "./pages/AccountPage";
import AdminLayout from "./components/layout/AdminLayout";
import AdminHomePage from "./pages/AdminHomePage";
import AdminUserPage from "./pages/AdminUserPage";
import AdminPostPage from "./pages/AdminPostPage";
import { setupInterceptors } from "./axiosConfig";
import store from "./redux/store";
import { useDispatch, useSelector } from "react-redux";
import { authSelector } from "./redux/selectors";
import { useEffect } from "react";
import { refreshToken } from "./api/AuthService";
import PersonalPostList from "./components/post/PersonalPostList";
import FriendList from "./components/friend/FriendList";
import ConversationLayout from "./components/layout/ConversationLayout";
import ConversationPage from "./pages/ConversationPage";
import ForgotPasswordPage from "./pages/ForgotPasswordPage";

function App() {
  setupInterceptors(store);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const auth = useSelector(authSelector);
  useEffect(() => {
    refreshToken(dispatch, navigate);
  }, []);
  return (
    <Routes>
      <Route path="/auth/signin" element={<SigninPage></SigninPage>}></Route>
      <Route path="/auth/signup" element={<SignupPage></SignupPage>}></Route>
      <Route
        path="/auth/forgot-password"
        element={<ForgotPasswordPage></ForgotPasswordPage>}
      ></Route>
      <Route
        path="/"
        element={
          auth.isAuthenticated ? (
            <Layout></Layout>
          ) : // <Navigate to={"/auth/signin"} />
          null
        }
      >
        <Route path="" element={<Navigate to="/home" replace />}></Route>
        <Route path="home" element={<HomePage></HomePage>}></Route>
        <Route path="user/:id" element={<PersonalPage></PersonalPage>}>
          <Route path="" element={<Navigate to="posts" replace />}></Route>
          <Route
            path="posts"
            element={<PersonalPostList></PersonalPostList>}
          ></Route>
          <Route
            path="friends"
            element={
              <div className="grid grid-cols-3 py-4">
                <FriendList></FriendList>
              </div>
            }
          ></Route>
        </Route>
        <Route path="account" element={<AccountPage></AccountPage>}></Route>
      </Route>
      <Route path="/admin" element={<AdminLayout></AdminLayout>}>
        <Route path="" element={<Navigate to="home" replace />}></Route>
        <Route path="home" element={<AdminHomePage></AdminHomePage>}></Route>
        <Route path="posts" element={<AdminPostPage></AdminPostPage>}></Route>
        <Route path="users" element={<AdminUserPage></AdminUserPage>}></Route>
      </Route>

      <Route
        path="/conversation"
        element={<ConversationLayout></ConversationLayout>}
      >
        <Route
          path=""
          element={
            <div className="flex flex-col items-center justify-center h-full opacity-50">
              <i className="bx bx-search-alt text-[100px]"></i>
              <p className="text-2xl">
                Hãy tìm một người bạn và bắt đầu cuộc trò chuyện
              </p>
            </div>
          }
        ></Route>
        <Route
          path=":id"
          element={<ConversationPage></ConversationPage>}
        ></Route>
      </Route>
    </Routes>
  );
}

export default App;

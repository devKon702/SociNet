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
import { setupInterceptors } from "./axiosConfig";
import store from "./redux/store";
import { useDispatch, useSelector } from "react-redux";
import {
  authSelector,
  loadingSelector,
  snackbarSelector,
} from "./redux/selectors";
import { useEffect } from "react";
import { refreshToken } from "./api/AuthService";
import PersonalPostList from "./components/post/PersonalPostList";
import FriendList from "./components/friend/FriendList";
import ConversationLayout from "./components/layout/ConversationLayout";
import ConversationPage from "./pages/ConversationPage";
import ForgotPasswordPage from "./pages/ForgotPasswordPage";
import AuthenticatedPage from "./pages/AuthenticatedPage";
import UnauthPage from "./pages/UnauthPage";
import UserPage from "./pages/UserPage";
import AdminPage from "./pages/AdminPage";
import { Alert, Slide, Snackbar } from "@mui/material";
import { hideSnackbar } from "./redux/snackbarSlice";
import RoomPage from "./pages/RoomPage";
import { signin, signout } from "./redux/authSlice";
import { hideLoading, showLoading } from "./redux/loadingSlice";
import EmptyConversationPage from "./pages/EmptyConversationPage";
import { setNavigate } from "./navigator";

function SlideTransition(props) {
  return <Slide {...props} direction="up" />;
}

function App() {
  setupInterceptors(store);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { isLoading } = useSelector(authSelector);
  const { open, message, type } = useSelector(snackbarSelector);
  const { isLoading: isShowLoading } = useSelector(loadingSelector);

  const handleCloseSnackbar = (event, reason) => {
    if (reason === "clickaway") return;
    dispatch(hideSnackbar());
  };

  useEffect(() => {
    setNavigate(navigate);
    const token = localStorage.getItem("socinet");
    if (!token) dispatch(signout());
    else {
      dispatch(showLoading());
      refreshToken(localStorage.getItem("socinet"))
        .then((res) => {
          if (res.isSuccess) {
            const { accessToken, refreshToken, account } = res.data;
            dispatch(signin({ token: accessToken, user: account }));
            localStorage.setItem("socinet", refreshToken);
          } else {
            dispatch(signout());
          }
        })
        .catch((e) => {
          console.log(e);
          dispatch(signout());
        })
        .finally(() => {
          dispatch(hideLoading());
        });
    }
  }, []);

  if (isLoading) {
    return (
      <div className="fixed inset-0 grid place-items-center z-50">
        <div className="absolute w-48 h-28 rounded-2xl top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-black opacity-90 z-10"></div>
        <div className="absolute inset-0 bg-black opacity-20"></div>
        <svg viewBox="25 25 50 50" className="loading-container z-50">
          <circle cx="50" cy="50" r="20" className="loader"></circle>
        </svg>
      </div>
    );
  }

  return (
    <>
      <Routes>
        <Route path="/auth" element={<UnauthPage></UnauthPage>}>
          <Route path="signin" element={<SigninPage></SigninPage>}></Route>
          <Route path="signup" element={<SignupPage></SignupPage>}></Route>
          <Route
            path="forgot-password"
            element={<ForgotPasswordPage></ForgotPasswordPage>}
          ></Route>
        </Route>

        <Route path="" element={<AuthenticatedPage></AuthenticatedPage>}>
          <Route element={<UserPage></UserPage>}>
            <Route path="/" element={<Layout></Layout>}>
              <Route path="" element={<Navigate to="/home" replace />}></Route>
              <Route path="home" element={<HomePage></HomePage>}></Route>
              <Route path="user/:id" element={<PersonalPage></PersonalPage>}>
                <Route
                  path=""
                  element={<Navigate to="posts" replace />}
                ></Route>
                <Route
                  path="posts"
                  element={<PersonalPostList></PersonalPostList>}
                ></Route>
                <Route
                  path="friends"
                  element={<FriendList></FriendList>}
                ></Route>
              </Route>
              <Route
                path="account"
                element={<AccountPage></AccountPage>}
              ></Route>
            </Route>
            <Route
              path="/conversation"
              element={<ConversationLayout></ConversationLayout>}
            >
              <Route
                path=""
                element={<EmptyConversationPage></EmptyConversationPage>}
              ></Route>
              <Route
                path=":id"
                element={<ConversationPage></ConversationPage>}
              ></Route>
              <Route path="room/:id" element={<RoomPage></RoomPage>}></Route>
            </Route>
          </Route>

          <Route path="/admin" element={<AdminPage></AdminPage>}>
            <Route path="" element={<AdminLayout></AdminLayout>}>
              <Route path="" element={<Navigate to="manage" replace />}></Route>
              {/* <Route
                path="home"
                element={<AdminHomePage></AdminHomePage>}
              ></Route> */}
              <Route
                path="manage"
                element={<AdminUserPage></AdminUserPage>}
              ></Route>
              <Route
                path="account"
                element={<AccountPage></AccountPage>}
              ></Route>
            </Route>
          </Route>
        </Route>
      </Routes>
      <Snackbar
        open={open}
        autoHideDuration={1500}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ horizontal: "center", vertical: "bottom" }}
        TransitionComponent={SlideTransition}
        key={SlideTransition.name}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity={type}
          variant="standard"
          sx={{ width: "100%" }}
        >
          {message}
        </Alert>
      </Snackbar>
      {isShowLoading && (
        <div className="fixed inset-0 grid place-items-center z-50 fade-in">
          <div className="absolute w-48 h-28 rounded-2xl top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-black opacity-90 z-10"></div>
          <div className="absolute inset-0 bg-black opacity-20"></div>
          <svg viewBox="25 25 50 50" className="loading-container z-50">
            <circle cx="50" cy="50" r="20" className="loader"></circle>
          </svg>
        </div>
      )}
    </>
  );
}

export default App;

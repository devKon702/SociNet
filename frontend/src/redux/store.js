import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./authSlice";
import postReducer from "./postSlice";
import commentReducer from "./commentSlice";
import personalReducer from "./personalSlice";
import forgotPasswordReducer from "./forgotPasswordSlice";
import accountReducer from "./accountSlice";
import friendReducer from "./friendSlice";
import realtimeReducer from "./realtimeSlice";

const store = configureStore({
  reducer: {
    auth: authReducer,
    forgotPassword: forgotPasswordReducer,
    post: postReducer,
    comment: commentReducer,
    personal: personalReducer,
    account: accountReducer,
    friend: friendReducer,
    realtime: realtimeReducer,
  },
});

export default store;

import { configureStore, createAction } from "@reduxjs/toolkit";
import authReducer from "./authSlice";
import postReducer from "./postSlice";
import commentReducer from "./commentSlice";
import personalReducer from "./personalSlice";
import forgotPasswordReducer from "./forgotPasswordSlice";
import accountReducer from "./accountSlice";
import friendReducer from "./friendSlice";
import realtimeReducer from "./realtimeSlice";
import adminReducer from "./adminSlice";
import snackbarReducer from "./snackbarSlice";
import loadingReducer from "./loadingSlice";

export const resetDataStore = createAction("resetDateStore");

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
    admin: adminReducer,
    snackbar: snackbarReducer,
    loading: loadingReducer,
  },
});

export default store;

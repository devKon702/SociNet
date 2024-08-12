import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getOtp } from "../api/AuthService";
import { changeEmail, changePassword } from "../api/AccountService";
import store from "./store";
import { setAccountEmail, setUser } from "./authSlice";
import { showSnackbar } from "./snackbarSlice";

const accountSlice = createSlice({
  name: "account",
  initialState: {
    manageEmail: {
      newEmail: "",
      otp: "",
      progress: 1,
      errorMessage: "",
      otpPending: false,
      emailPending: false,
    },
    managePassword: {
      oldPassword: "",
      newPassword: "",
      confirmPassword: "",
      errorMessage: "",
    },
  },
  reducers: {
    setProgress: (state, action) => {
      state.manageEmail.progress = action.payload.progress;
    },
    setNewEmail: (state, action) => {
      state.manageEmail.newEmail = action.payload.newEmail;
    },
    setOtp: (state, action) => {
      state.manageEmail.otp = action.payload.otp;
    },
    clearEmailManage: (state) => {
      state.manageEmail = {
        newEmail: "",
        otp: "",
        progress: 1,
        errorMessage: "",
        otpPending: false,
        emailPending: false,
      };
    },
    setOldPassword: (state, action) => {
      state.managePassword.oldPassword = action.payload.oldPassword;
    },
    setNewPassword: (state, action) => {
      state.managePassword.newPassword = action.payload.newPassword;
    },
    setConfirmPassword: (state, action) => {
      state.managePassword.confirmPassword = action.payload.confirmPassword;
    },
    setEmailError: (state, action) => {
      state.manageEmail.errorMessage = action.payload.errorMessage;
    },
    setPasswordError: (state, action) => {
      state.managePassword.errorMessage = action.payload.errorMessage;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(sendOtpThunk.pending, (state) => {
        state.manageEmail.otpPending = true;
      })
      .addCase(sendOtpThunk.fulfilled, (state) => {
        state.manageEmail.otpPending = false;
      })
      .addCase(changeEmailThunk.pending, (state) => {
        state.manageEmail.emailPending = true;
      })
      .addCase(changeEmailThunk.fulfilled, (state, action) => {
        state.manageEmail.emailPending = false;
        if (!action.payload.isSuccess) {
          switch (action.payload.message) {
            case "USED EMAIL":
              state.manageEmail.errorMessage = "Email đã được sử dụng";
              break;
            case "INVALID OTP":
              state.manageEmail.errorMessage = "OTP không hợp lệ";
              break;
            default:
              break;
          }
        } else {
          state.manageEmail.progress = 3;
        }
      })
      .addCase(changePasswordThunk.fulfilled, (state, action) => {
        if (!action.payload.isSuccess) {
          switch (action.payload.message) {
            case "Password is incorrect":
              state.managePassword.errorMessage = "Mật khẩu không đúng";
              break;
          }
        } else {
          state.managePassword = {
            oldPassword: "",
            newPassword: "",
            confirmPassword: "",
            errorMessage: "",
          };
        }
      });
  },
});

export const sendOtpThunk = createAsyncThunk(
  "account/sendOtpThunk",
  async (email) => {
    const res = await getOtp(email);
    return res;
  }
);

export const changeEmailThunk = createAsyncThunk(
  "account/changeEmailThunk",
  async ({ newEmail, otp }, { dispatch }) => {
    const res = await changeEmail(newEmail, otp);
    if (res.isSuccess) {
      dispatch(
        showSnackbar({ message: "Cập nhật email thành công", type: "success" })
      );
      dispatch(setAccountEmail(newEmail));
    }
    return res;
  }
);

export const changePasswordThunk = createAsyncThunk(
  "account/changePasswordThunk",
  async ({ oldPassword, newPassword }, { dispatch }) => {
    const res = await changePassword(oldPassword, newPassword);
    if (res.isSuccess) {
      dispatch(
        showSnackbar({
          message: "Cập nhật mật khẩu thành công",
          type: "success",
        })
      );
    }
    return res;
  }
);

export const {
  setNewEmail,
  setOtp,
  setProgress,
  clearEmailManage,
  setOldPassword,
  setNewPassword,
  setConfirmPassword,
  setEmailError,
  setPasswordError,
} = accountSlice.actions;

export default accountSlice.reducer;

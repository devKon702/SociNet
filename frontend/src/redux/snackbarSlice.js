import { createSlice } from "@reduxjs/toolkit";

const snackbarSlice = createSlice({
  name: "snackbar",
  initialState: {
    open: false,
    message: "",
    type: "info", // success, error, info, warning
  },
  reducers: {
    showSnackbar: (state, action) => {
      state.message = action.payload.message;
      state.type = action.payload.type || "info";
      state.open = true;
    },
    hideSnackbar: (state) => {
      state.open = false;
    },
  },
});

export const { showSnackbar, hideSnackbar } = snackbarSlice.actions;
export default snackbarSlice.reducer;

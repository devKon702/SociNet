import { createSlice } from "@reduxjs/toolkit";
import { resetDataStore } from "./store";

const initialState = {
  friendList: [],
  suggestionList: [], // list of User
};

const friendSlice = createSlice({
  name: "friend",
  initialState,
  reducers: {
    setSuggestionList: (state, action) => {
      state.suggestionList = action.payload;
    },
  },
  extraReducers: (builder) =>
    builder.addCase(resetDataStore, () => initialState),
});

export const { setSuggestionList } = friendSlice.actions;
export default friendSlice.reducer;

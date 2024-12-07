import { createSlice } from "@reduxjs/toolkit";

const friendSlice = createSlice({
  name: "friend",
  initialState: {
    friendList: [],
    suggestionList: [], // list of User
  },
  reducers: {
    setSuggestionList: (state, action) => {
      state.suggestionList = action.payload;
    },
  },
});

export const { setSuggestionList } = friendSlice.actions;
export default friendSlice.reducer;

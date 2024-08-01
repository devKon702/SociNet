import axios from "axios";

const AccountService = {
  changeEmail: async (email, otp) =>
    axios
      .put(`api/v1/account/change-email?newEmail=${email}&otp=${otp}`)
      .then((res) => res.data)
      .catch((e) => e.response.data),

  changePassword: async (oldPassword, newPassword) =>
    axios
      .put(
        "api/v1/account/change-password",
        { oldPassword, newPassword },
        { headers: { "Content-Type": "multipart/form-data" } }
      )
      .then((res) => res.data)
      .catch((e) => e.response.data),
};

export const { changeEmail, changePassword } = AccountService;
export default AccountService;

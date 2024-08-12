import React, { useState } from "react";
import Dialog from "./Dialog";
import { useDispatch, useSelector } from "react-redux";
import { forgotPasswordSelector } from "../../redux/selectors";
import { forgotPassword, getOtp } from "../../api/AuthService";
import { useNavigate } from "react-router-dom";
import ForgotPasswordForm from "../form/ForgotPasswordForm";

const ForgotPasswordDialog = ({ handleClose }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { email } = useSelector(forgotPasswordSelector);
  const [isSending, setSending] = useState(false);
  const [isSubmitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [formValues, setFormValues] = useState({
    newPassword: "",
    confirm: "",
    otp: "",
  });

  const handleResendOtp = async () => {
    setSending(true);
    await getOtp(email);
    setSending(false);
  };

  const handleSubmit = () => {
    if (formValues.confirm !== formValues.newPassword) {
      setError("Xác nhận sai mật khẩu");
      return;
    }
    setSubmitting(true);
    forgotPassword(email, formValues.newPassword, formValues.otp)
      .then((res) => {
        console.log(res);
        navigate("/auth/signin?username=" + res.data.username);
      })
      .catch((e) => {
        setSubmitting(false);
        setError(e.message);
      });
  };

  return (
    <Dialog handleClose={handleClose} title={"Quên mật khẩu"}>
      <div className="overflow-y-auto overflow-x-hidden custom-scroll">
        <ForgotPasswordForm></ForgotPasswordForm>
      </div>
    </Dialog>
  );
};

export default ForgotPasswordDialog;

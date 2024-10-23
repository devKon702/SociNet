import { useState } from "react";

const useFetch = ({
  handleFetch = () => {},
  params = [],
  handleSuccess = () => {},
  handleFail = () => {},
} = {}) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [response, setResponse] = useState(null);
  function callFetch() {
    setLoading(true);
    handleFetch(...params)
      .then((res) => {
        setResponse(res);
        setLoading(false);
        handleSuccess(res);
      })
      .catch((e) => {
        console.log(e);
        setError(e);
        setLoading(false);
        handleFail(e);
      });
  }
  return { callFetch, loading, response, error };
};

export default useFetch;

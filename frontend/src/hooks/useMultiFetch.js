import { useState } from "react";

const useMultiFetch = ({
  handleFetchList = [],
  paramsList = [],
  handleSuccess = () => {},
  handleFail = () => {},
}) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [response, setResponse] = useState(null);
  function callFetch() {
    setLoading(true);
    Promise.all(
      handleFetchList.map((item, index) => item(...paramsList[index]))
    )
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

export default useMultiFetch;

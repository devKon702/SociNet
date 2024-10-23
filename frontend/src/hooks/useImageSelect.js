import { useEffect, useState } from "react";

const useImageSelect = ({ initialSrc, maxSize }) => {
  const [src, setSrc] = useState(initialSrc);
  const [size, setSize] = useState(0);
  const [file, setFile] = useState();
  const [sizeError, setSizeError] = useState(false);
  //   const inputRef = useRef(null);

  function handleFileChange(e) {
    const fileData = e.target.files[0];
    const fileSize = (fileData.size / 1024 / 1024).toFixed(2);
    if (fileData.type.startsWith("image/")) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setSizeError(fileData.size > maxSize);
        setSrc(e.target.result);
        setSize(fileSize);
        setFile(fileData);
      };
      reader.readAsDataURL(fileData);
    }
  }

  useEffect(() => {
    if (!src) setFile(null);
  }, [src]);

  return { file, src, size, sizeError, handleFileChange, setSrc };
};

export default useImageSelect;

const LoadingItem = ({
  borderWidthClass = "border-4",
  borderColorClass = "border-white",
  sizeClass = "size-6",
} = {}) => {
  return (
    <div
      className={`${sizeClass} ${borderWidthClass} ${borderColorClass} rounded-full animate-spin border-r-transparent inline-block`}
    ></div>
  );
};

export default LoadingItem;

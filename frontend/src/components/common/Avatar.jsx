const Avatar = ({ size, src, ...props } = { size: "size-8", src }) => {
  return (
    <div className={`rounded-full overflow-hidden ${size}`} {...props}>
      <img src={src} alt="" className="object-cover size-full" />
    </div>
  );
};

export default Avatar;

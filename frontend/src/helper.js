import { format, isToday, isThisWeek } from "date-fns";

export const dateFormated = (dateString) => {
  const inputDate = new Date(dateString);
  const currentDate = new Date();

  const timeDifference = currentDate - inputDate;

  const minutesDifference = Math.floor(timeDifference / (1000 * 60));
  const hoursDifference = Math.floor(timeDifference / (1000 * 60 * 60));
  const daysDifference = Math.floor(timeDifference / (1000 * 60 * 60 * 24));
  const weeksDifference = Math.floor(
    timeDifference / (1000 * 60 * 60 * 24 * 7)
  );

  if (minutesDifference < 1) {
    return "vài giây trước";
  } else if (minutesDifference < 60) {
    return `${minutesDifference} phút trước`;
  } else if (hoursDifference < 24) {
    return `${hoursDifference} giờ trước`;
  } else if (daysDifference < 7) {
    return `${daysDifference} ngày trước`;
  } else {
    return inputDate.toLocaleDateString(); // Format as day/month/year
  }
};

const daysMap = {
  Monday: "T2",
  Tuesday: "T3",
  Wednesday: "T4",
  Thursday: "T5",
  Friday: "T6",
  Saturday: "T7",
  Sunday: "CN",
};

export const dateDetailFormated = (date) => {
  // Kiểm tra nếu ngày là hôm nay
  if (isToday(date)) {
    return format(date, "HH:mm");
  }

  // Kiểm tra nếu ngày nằm trong tuần này
  if (isThisWeek(date, { weekStartsOn: 1 })) {
    // weekStartsOn: 1 cho biết tuần bắt đầu từ Thứ 2
    const dayName = format(date, "EEEE");
    return `${daysMap[dayName]} ${format(date, "HH:mm")}`;
  }

  // Nếu không phải hôm nay và không nằm trong tuần này
  return format(date, "dd/MM/yyyy HH:mm");
};

export const isImage = (url) => {
  const typeList = [
    ".apng",
    ".png",
    ".jpg",
    ".jpeg",
    ".jfif",
    ".pjpeg",
    ".pjp",
    ".svg",
    ".webp",
    ".avif",
    ".gif",
  ];
  return typeList.some((item) => url.includes(item));
};

export const isVideo = (url) => {
  const typeList = [".mp4", ".webm", ".avi", ".mov", ".wmv"];
  return typeList.some((item) => url.includes(item));
};

export const removeAllListenersExcept = (socket, eventNameToKeep) => {
  // Lưu lại tất cả các listeners của event
  const listeners = socket.listeners(eventNameToKeep);

  // Xóa tất cả event listeners
  socket.removeAllListeners();

  // Gán lại các listeners của event
  listeners.forEach((listener) => {
    socket.on(eventNameToKeep, listener);
  });
};

export const getDeviceType = (userAgent) => {
  if (/(tablet|ipad|playbook|silk)|(android(?!.*mobi))/i.test(userAgent)) {
    return "Tablet";
  }
  if (
    /Mobile|iP(hone|od)|Android|okhttp|BlackBerry|IEMobile|Kindle|Silk-Accelerated|(hpw|web)OS|Opera M(obi|ini)/.test(
      userAgent
    )
  ) {
    return "Mobile";
  }
  return "PC";
};

export const getIpInformation = async (ip) => {
  let url = "";
  if (ip) url = "http://ip-api.com/json/" + ip;
  else url = "http://ip-api.com/json/?fields=61439";

  const ipResult = await fetch(url)
    .then((res) => res.json())
    .catch((error) => console.log(error));
  return ipResult;
};

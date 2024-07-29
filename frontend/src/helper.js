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
    return "just now";
  } else if (minutesDifference < 60) {
    return `${minutesDifference} minutes ago`;
  } else if (hoursDifference < 24) {
    return `${hoursDifference} hours ago`;
  } else if (daysDifference < 7) {
    return `${daysDifference} days ago`;
  } else {
    return inputDate.toLocaleDateString(); // Format as day/month/year
  }
};

// navigateService.js
let navigate = null;

export const setNavigate = (navigateFn) => {
  navigate = navigateFn;
};

export const navigateTo = (path) => {
  if (navigate) {
    navigate(path);
  } else {
    console.error("Navigate function is not set yet.");
  }
};

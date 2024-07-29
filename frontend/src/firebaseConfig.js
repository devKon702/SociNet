import { initializeApp } from "firebase/app";
import { getStorage } from "firebase/storage";

const firebaseConfig = {
  apiKey: "AIzaSyBqNh0CevFZhI-0Edm_fH9wkCvreqWbjiA",
  authDomain: "socinet-6cfdd.firebaseapp.com",
  projectId: "socinet-6cfdd",
  storageBucket: "socinet-6cfdd.appspot.com",
  messagingSenderId: "411033673476",
  appId: "1:411033673476:web:2dba0c37656373fabe3a51",
  measurementId: "G-WJZHGCWZXS",
};

const app = initializeApp(firebaseConfig);
const storage = getStorage(app);

export { storage };

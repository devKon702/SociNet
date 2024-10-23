const mysql = require("mysql2");

const HOST = "localhost";
const USER = "root";
const PASS = "123456";
const DATABASE = "socialnet";
const connection = mysql.createConnection({
  host: HOST,
  user: USER,
  password: PASS,
  database: DATABASE,
  port: 3306,
});
connection.connect((err) => {
  if (err) {
    console.error("Error connecting to MySQL:", err.stack);
    return;
  }
  console.log("Connected to MySQL as id ", connection.threadId);
});

module.exports = { connection };

const mysql = require("mysql2");
require("dotenv").config();
const HOST = process.env.DB_HOST;
const PORT = process.env.DB_PORT;
const USER = process.env.DB_USER;
const PASS = process.env.DB_PASS;
const DATABASE = process.env.DB_DATABASE;
const connection = mysql.createConnection({
  host: HOST,
  user: USER,
  password: PASS,
  database: DATABASE,
  port: PORT,
});
connection.connect((err) => {
  if (err) {
    console.error("Error connecting to MySQL:", err.stack);
    return;
  }
  console.log("Connected to MySQL as id ", connection.threadId);
});

module.exports = { connection };

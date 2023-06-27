module.exports = [
  {
    context: ["/api/**"],
    // context: ["/**"],
    target: "http://localhost:8080",
    secure: false,
  },
];

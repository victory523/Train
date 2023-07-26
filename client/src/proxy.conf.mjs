export default {
  "/api": {
    target: "http://localhost:8080",
    secure: false,
    changeOrigin: true,
  },
  "/db/last-backup-time": {
    bypass: function (req, res) {
      res.send("2 hours ago");
    },
  },
};

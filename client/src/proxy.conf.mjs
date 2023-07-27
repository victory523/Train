export default {
  "/api": {
    target: "http://localhost:8080",
    secure: false
  },
  "/db/last-backup-time": {
    bypass: function (req, res) {
      // res.send(new Date(Date.now() - 25 * 60 * 60 * 1000));
      res.send(new Date(Date.now() - 5 * 60 * 1000));
    },
  },
};

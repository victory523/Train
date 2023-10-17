const { getSearchParams } = require("./request");

let activity1Date = new Date();
let activity2Date = new Date();

function getActivities(request, response) {
  const authorization = request.headers.authorization;
  if (authorization !== "Bearer test-access-token") {
    console.log("access denied");
    response.writeHead(401);
    response.end();
    return;
  }

  const searchParams = getSearchParams(request);
  const after = parseInt(searchParams.get("after"));
  const before = parseInt(searchParams.get("before"));
  activity1Date = new Date(1000 * (after + (before - after) / 2));
  activity1Date.setUTCHours(12, 35);
  activity2Date = new Date(1000 * (after + (before - after) / 2));
  activity2Date.setUTCHours(12, 45);

  response.writeHead(200, {
    "Content-Type": "application/json",
  });
  return response.end(
    JSON.stringify([
      {
        resource_state: 2,
        athlete: {
          id: 134815,
          resource_state: 1,
        },
        name: "Happy Friday",
        distance: 24931.4,
        moving_time: 4500,
        elapsed_time: 4500,
        total_elevation_gain: 0,
        type: "Ride",
        sport_type: "MountainBikeRide",
        workout_type: null,
        id: 1,
        external_id: "garmin_push_12345678987654321",
        upload_id: 1,
        start_date: "2018-05-02T12:15:09Z",
        start_date_local: "2018-05-02T05:15:09Z",
        timezone: "(GMT-08:00) America/Los_Angeles",
        utc_offset: -25200,
        start_latlng: null,
        end_latlng: null,
        location_city: null,
        location_state: null,
        location_country: "United States",
        achievement_count: 0,
        kudos_count: 3,
        comment_count: 1,
        athlete_count: 1,
        photo_count: 0,
        map: {
          id: "a12345678987654321",
          summary_polyline: null,
          resource_state: 2,
        },
        trainer: true,
        commute: false,
        manual: false,
        private: false,
        flagged: false,
        gear_id: "b12345678987654321",
        from_accepted_tag: false,
        average_speed: 5.54,
        max_speed: 11,
        average_cadence: 67.1,
        average_watts: 175.3,
        weighted_average_watts: 210,
        kilojoules: 788.7,
        device_watts: true,
        has_heartrate: true,
        average_heartrate: 140.3,
        max_heartrate: 178,
        max_watts: 406,
        pr_count: 0,
        total_photo_count: 1,
        has_kudoed: false,
        suffer_score: 82,
      },
      {
        resource_state: 2,
        athlete: {
          id: 167560,
          resource_state: 1,
        },
        name: "Bondcliff",
        distance: 23676.5,
        moving_time: 5400,
        elapsed_time: 5400,
        total_elevation_gain: 0,
        type: "Ride",
        sport_type: "MountainBikeRide",
        workout_type: null,
        id: 2,
        external_id: "garmin_push_12345678987654321",
        upload_id: 2,
        start_date: "2018-04-30T12:35:51Z",
        start_date_local: "2018-04-30T05:35:51Z",
        timezone: "(GMT-08:00) America/Los_Angeles",
        utc_offset: -25200,
        start_latlng: null,
        end_latlng: null,
        location_city: null,
        location_state: null,
        location_country: "United States",
        achievement_count: 0,
        kudos_count: 4,
        comment_count: 0,
        athlete_count: 1,
        photo_count: 0,
        map: {
          id: "a12345689",
          summary_polyline: null,
          resource_state: 2,
        },
        trainer: true,
        commute: false,
        manual: false,
        private: false,
        flagged: false,
        gear_id: "b12345678912343",
        from_accepted_tag: false,
        average_speed: 4.385,
        max_speed: 8.8,
        average_cadence: 69.8,
        average_watts: 200,
        weighted_average_watts: 214,
        kilojoules: 1080,
        device_watts: true,
        has_heartrate: true,
        average_heartrate: 152.4,
        max_heartrate: 183,
        max_watts: 403,
        pr_count: 0,
        total_photo_count: 1,
        has_kudoed: false,
        suffer_score: 162,
      },
    ])
  );
}

function getActivity(request, response) {
  const authorization = request.headers.authorization;
  if (authorization !== "Bearer test-access-token") {
    console.log("access denied");
    response.writeHead(401);
    response.end();
    return;
  }

  response.writeHead(200, {
    "Content-Type": "application/json",
  });
  return response.end(
    JSON.stringify({
      id: 12345678987654321,
      resource_state: 3,
      external_id: "garmin_push_12345678987654321",
      upload_id: 98765432123456789,
      athlete: {
        id: 134815,
        resource_state: 1,
      },
      name: "Happy Friday",
      distance: 28099,
      moving_time: 4207,
      elapsed_time: 4410,
      total_elevation_gain: 516,
      type: "Ride",
      sport_type: "MountainBikeRide",
      start_date: request.url.endsWith("1")
        ? activity1Date.toISOString()
        : activity2Date.toISOString(),
      start_date_local: request.url.endsWith("1")
        ? activity1Date.toISOString()
        : activity2Date.toISOString(),
      timezone: "(GMT-08:00) America/Los_Angeles",
      utc_offset: -28800,
      start_latlng: [37.83, -122.26],
      end_latlng: [37.83, -122.26],
      achievement_count: 0,
      kudos_count: 19,
      comment_count: 0,
      athlete_count: 1,
      photo_count: 0,
      trainer: false,
      commute: false,
      manual: false,
      private: false,
      flagged: false,
      gear_id: "b12345678987654321",
      from_accepted_tag: false,
      average_speed: 6.679,
      max_speed: 18.5,
      average_cadence: 78.5,
      average_temp: 4,
      average_watts: 185.5,
      weighted_average_watts: 230,
      kilojoules: 780.5,
      device_watts: true,
      has_heartrate: false,
      max_watts: 743,
      elev_high: 446.6,
      elev_low: 17.2,
      pr_count: 0,
      total_photo_count: 2,
      has_kudoed: false,
      workout_type: 10,
      suffer_score: null,
      description: "",
      calories: 870.2,
      gear: {
        id: "b12345678987654321",
        primary: true,
        name: "Tarmac",
        resource_state: 2,
        distance: 32547610,
      },
      partner_brand_tag: null,
      hide_from_home: false,
      device_name: "Garmin Edge 1030",
      embed_token: "18e4615989b47dd4ff3dc711b0aa4502e4b311a9",
      segment_leaderboard_opt_out: false,
      leaderboard_opt_out: false,
    })
  );
}

module.exports = {
  getActivities,
  getActivity,
};

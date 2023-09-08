CREATE TABLE IF NOT EXISTS oauth2_authorized_client (
  client_registration_id varchar(100) NOT NULL,
  principal_name varchar(200) NOT NULL,
  access_token_type varchar(100) NOT NULL,
  access_token_value bytea NOT NULL,
  access_token_issued_at timestamp NOT NULL,
  access_token_expires_at timestamp NOT NULL,
  access_token_scopes varchar(1000) DEFAULT NULL,
  refresh_token_value bytea DEFAULT NULL,
  refresh_token_issued_at timestamp DEFAULT NULL,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (client_registration_id, principal_name)
);

CREATE TABLE IF NOT EXISTS ride (
  created_at timestamp(6) NOT NULL,
  calories float4,
  distance float4,
  moving_time integer,
  name varchar(255),
  sport_type varchar(255),
  total_elevation_gain float4,
  weighted_average_watts float4,
  PRIMARY KEY (created_at)
);

CREATE TABLE IF NOT EXISTS fitness (
  created_at timestamp(6) NOT NULL,
  fatigue float4,
  fitness
  float4,
  form float4,
  PRIMARY KEY (created_at)
);

CREATE TABLE IF NOT EXISTS weight (
  created_at timestamp(6) NOT NULL,
  weight float4,
  fat_ratio float4,
  fat_mass_weight float4,
  PRIMARY KEY (created_at)
);

CREATE TABLE IF NOT EXISTS socialmusicdiscovery_tracks (
  smdid varchar(64) PRIMARY KEY,
  url blob not null
);

CREATE TABLE IF NOT EXISTS socialmusicdiscovery_tags (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  smdid varchar(64),
  name varchar(40),
  value varchar(100),
  sortvalue varchar(100)
);


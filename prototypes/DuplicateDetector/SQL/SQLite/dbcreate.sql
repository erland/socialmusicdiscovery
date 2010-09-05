CREATE TABLE IF NOT EXISTS duplicatedetector_tracks (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  url blob not null,
  smdid varchar(64),
  audiosize int(10)
);


CREATE TABLE IF NOT EXISTS duplicatedetector_tracks (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
  url text NOT NULL,
  smdid varchar(64),
  audiosize int(10),
  primary key (id)
) TYPE=InnoDB;

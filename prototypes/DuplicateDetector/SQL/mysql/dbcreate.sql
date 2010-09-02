CREATE TABLE IF NOT EXISTS duplicatedetector_tracks (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
  url text NOT NULL,
  smdid varchar(64),
  primary key (id)
) TYPE=InnoDB;

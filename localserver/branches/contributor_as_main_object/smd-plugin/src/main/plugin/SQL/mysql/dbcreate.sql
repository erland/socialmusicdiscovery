CREATE TABLE IF NOT EXISTS socialmusicdiscovery_tracks (
  url text NOT NULL,
  smdid varchar(64),
  primary key (smdid)
) TYPE=InnoDB;

CREATE TABLE IF NOT EXISTS socialmusicdiscovery_tags (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
  smdid varchar(64),
  name varchar(40),
  value varchar(100),
  sortvalue varchar(100),
  primary key (id)
) TYPE=InnoDB;

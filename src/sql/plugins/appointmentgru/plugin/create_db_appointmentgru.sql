
--
-- Structure for table appointmentgru_
--

DROP TABLE IF EXISTS appointmentgru_;
CREATE TABLE appointmentgru_ (
id_appointmentgru int NOT NULL,
guid varchar(50) NOT NULL default '',
cuid int NOT NULL default 0,
PRIMARY KEY (id_appointmentgru)
);

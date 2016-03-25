
--
-- Structure for table appointmentgru_
--

DROP TABLE IF EXISTS appointmentgru_;
CREATE TABLE appointmentgru_ (
id_appointmentgru int(6) NOT NULL,
guid varchar(50) NOT NULL default '',
cuid int(11) NOT NULL default '0',
PRIMARY KEY (id_appointmentgru)
);

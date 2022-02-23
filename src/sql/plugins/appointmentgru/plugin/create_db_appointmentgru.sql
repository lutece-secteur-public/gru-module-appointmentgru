
--
-- Structure for table appointmentgru_
--

DROP TABLE IF EXISTS appointmentgru_;
CREATE TABLE appointmentgru_ (
id_appointmentgru int NOT NULL,
guid varchar(50) DEFAULT '' NOT NULL,
cuid int DEFAULT 0 NOT NULL,
PRIMARY KEY (id_appointmentgru)
);

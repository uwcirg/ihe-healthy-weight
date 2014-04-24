-- Contains schema of the ihe2008 database
--
-- Modified from MySQL dump 9.11 of the phin2007 database
-- ------------------------------------------------------
-- Server version	4.1.3-beta-max-log

--
-- Table structure for table `metadata`
--

CREATE TABLE metadata (
  metadataId int(11) NOT NULL auto_increment,
  patientId varchar(255) default NULL,
  documentUri text,
  mimeType varchar(255) default NULL,
  formatCode varchar(255) default NULL,
  typeCode varchar(255) default NULL,
  creationTime datetime default NULL,
  uniqueId varchar(245) default NULL,
  languageCode varchar(255) default NULL,
  sourcePatientInfo text,
  beenParsed tinyint(1) default '0',
  PRIMARY KEY  (metadataId),
  UNIQUE KEY IX_patientId_uniqueId (patientId,uniqueId)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `metadataVisits`
--

CREATE TABLE metadataVisits (
  `index` int(10) unsigned NOT NULL auto_increment,
  encounter varchar(255) default NULL,
  `date` datetime default NULL,
  facility varchar(255) default NULL,
  zip varchar(255) default NULL,
  age varchar(255) default NULL,
  gender varchar(255) default NULL,
  cc varchar(255) default NULL,
  disposition varchar(127) default NULL,
  classification varchar(127) default NULL,
  PRIMARY KEY  (`index`),
  UNIQUE KEY IX_encounter (encounter)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


--
-- Table structure for table `simulatedVisits`
--

CREATE TABLE simulatedVisits (
  `index` int(10) unsigned NOT NULL auto_increment,
  encounter varchar(127) default NULL,
  `date` datetime default NULL,
  facility varchar(127) default NULL,
  zip varchar(127) default NULL,
  age varchar(127) default NULL,
  gender varchar(127) default NULL,
  cc text,
  disposition varchar(127) default NULL,
  classification varchar(127) default NULL,
  PRIMARY KEY  (`index`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


--
-- Table structure for table `xdsMsParse`
--

CREATE TABLE xdsMsParse (
  xdsMsParseId int(11) NOT NULL auto_increment,
  patientId varchar(255) default NULL,
  effectiveTime datetime default NULL,
  birthDate date default NULL,
  gender varchar(255) default NULL,
  nameGiven varchar(255) default NULL,
  nameFamily varchar(255) default NULL,
  addrStreet varchar(255) default NULL,
  addrCity varchar(255) default NULL,
  addrState varchar(255) default NULL,
  addrZip varchar(255) default NULL,
  documentUri text,
  PRIMARY KEY  (xdsMsParseId),
  UNIQUE KEY IX_documentUri (documentUri(255))
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


--
-- Table structure for table `xdsMsParseVisits`
--

CREATE TABLE xdsMsParseVisits (
  `index` int(10) unsigned NOT NULL auto_increment,
  encounter varchar(127) default NULL,
  `date` datetime default NULL,
  facility varchar(127) default NULL,
  zip varchar(127) default NULL,
  age varchar(127) default NULL,
  gender varchar(127) default NULL,
  cc text,
  disposition varchar(127) default NULL,
  classification varchar(127) default NULL,
  PRIMARY KEY  (`index`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



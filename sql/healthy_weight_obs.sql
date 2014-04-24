-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 03, 2014 at 12:21 PM
-- Server version: 5.5.35
-- PHP Version: 5.4.4-14+deb7u7

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `ihe2014`
--

-- --------------------------------------------------------

--
-- Table structure for table `healthy_weight_obs`
--

CREATE TABLE IF NOT EXISTS `healthy_weight_obs` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `patient_id` varchar(1024) DEFAULT NULL,
  `gender` enum('M','F','U') NOT NULL DEFAULT 'U',
  `birthdate` datetime DEFAULT NULL,
  `race` varchar(1024) DEFAULT NULL,
  `ethnicity` varchar(1024) DEFAULT NULL,
  `zip_code` varchar(64) DEFAULT NULL,
  `latitude` varchar(64) DEFAULT NULL,
  `longitude` varchar(64) DEFAULT NULL,
  `obs_date` datetime DEFAULT NULL,
  `weight_pounds` decimal(6,2) DEFAULT NULL,
  `height_inches` decimal(5,2) DEFAULT NULL,
  `calculated_bmi` decimal(4,2) DEFAULT NULL,
  `calculated_age` decimal(4,1) DEFAULT NULL,
  `occupation_41` varchar(1024) DEFAULT NULL,
  `occupation_23_code` tinyint(3) unsigned DEFAULT NULL,
  `occupation_23` varchar(1024) DEFAULT NULL,
  `occupation_8` varchar(1024) DEFAULT NULL,
  `freq_sports_drink` varchar(16) DEFAULT NULL,
  `freq_soda` varchar(16) DEFAULT NULL,
  `freq_water` varchar(16) DEFAULT NULL,
  `freq_veg` varchar(16) DEFAULT NULL,
  `freq_fruit` varchar(16) DEFAULT NULL,
  `freq_fruit_juice` varchar(16) DEFAULT NULL,
  `freq_fast_food` varchar(16) DEFAULT NULL,
  `breast_fed` varchar(16) DEFAULT NULL,
  `formula_quantity` varchar(16) DEFAULT NULL,
  `problem_nursing` varchar(16) DEFAULT NULL,
  `freq_physical` varchar(16) DEFAULT NULL,
  `physical_quantity` varchar(16) DEFAULT NULL,
  `tv_quantity` varchar(16) DEFAULT NULL,
  `game_quantity` varchar(16) DEFAULT NULL,
  `bed_time` varchar(16) DEFAULT NULL,
  `sleep_quantity` varchar(16) DEFAULT NULL,
  `is_pregnant` varchar(16) DEFAULT NULL,
  `ready_nutrition` varchar(16) DEFAULT NULL,
  `ready_sleep` varchar(16) DEFAULT NULL,
  `ready_exercise` varchar(16) DEFAULT NULL,
  `ready_screen` varchar(16) DEFAULT NULL,
  `import_source` varchar(255) DEFAULT NULL,
  `import_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=217004 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

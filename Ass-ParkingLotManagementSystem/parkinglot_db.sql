-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 12, 2026 at 06:50 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `parkinglot_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `fine`
--

CREATE TABLE `fine` (
  `fine_id` varchar(50) NOT NULL,
  `license_plate` varchar(20) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `reason` varchar(100) DEFAULT NULL,
  `issue_date` datetime NOT NULL,
  `paid` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `floor`
--

CREATE TABLE `floor` (
  `floor_number` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `floor`
--

INSERT INTO `floor` (`floor_number`) VALUES
(1),
(2),
(3),
(4),
(5);

-- --------------------------------------------------------

--
-- Table structure for table `parking_spot`
--

CREATE TABLE `parking_spot` (
  `spot_id` varchar(20) NOT NULL,
  `floor_number` int(11) NOT NULL,
  `row_label` varchar(5) NOT NULL,
  `spot_number` int(11) NOT NULL,
  `type` enum('COMPACT','REGULAR','HANDICAPPED','RESERVED') NOT NULL,
  `hourly_rate` decimal(10,2) NOT NULL,
  `is_occupied` tinyint(1) DEFAULT 0,
  `current_vehicle_plate` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `parking_spot`
--

INSERT INTO `parking_spot` (`spot_id`, `floor_number`, `row_label`, `spot_number`, `type`, `hourly_rate`, `is_occupied`, `current_vehicle_plate`) VALUES
('F1-RA-S1', 1, 'A', 1, 'COMPACT', 2.00, 0, NULL),
('F1-RA-S4', 1, 'A', 4, 'REGULAR', 5.00, 0, NULL),
('F1-RB-S2', 1, 'B', 2, 'COMPACT', 2.00, 0, NULL),
('F1-RB-S5', 1, 'B', 5, 'REGULAR', 5.00, 0, NULL),
('F1-RC-S3', 1, 'C', 3, 'COMPACT', 2.00, 0, NULL),
('F1-RC-S6', 1, 'C', 6, 'REGULAR', 5.00, 0, NULL),
('F1-RH-S7', 1, 'H', 7, 'HANDICAPPED', 2.00, 0, NULL),
('F2-RA-S1', 2, 'A', 1, 'COMPACT', 2.00, 0, NULL),
('F2-RA-S4', 2, 'A', 4, 'REGULAR', 5.00, 0, NULL),
('F2-RB-S2', 2, 'B', 2, 'COMPACT', 2.00, 0, NULL),
('F2-RB-S5', 2, 'B', 5, 'REGULAR', 5.00, 0, NULL),
('F2-RC-S3', 2, 'C', 3, 'COMPACT', 2.00, 0, NULL),
('F2-RC-S6', 2, 'C', 6, 'REGULAR', 5.00, 0, NULL),
('F2-RH-S7', 2, 'H', 7, 'HANDICAPPED', 2.00, 0, NULL),
('F3-RA-S1', 3, 'A', 1, 'COMPACT', 2.00, 0, NULL),
('F3-RA-S4', 3, 'A', 4, 'REGULAR', 5.00, 0, NULL),
('F3-RB-S2', 3, 'B', 2, 'COMPACT', 2.00, 0, NULL),
('F3-RB-S5', 3, 'B', 5, 'REGULAR', 5.00, 0, NULL),
('F3-RC-S3', 3, 'C', 3, 'COMPACT', 2.00, 0, NULL),
('F3-RC-S6', 3, 'C', 6, 'REGULAR', 5.00, 0, NULL),
('F3-RH-S7', 3, 'H', 7, 'HANDICAPPED', 2.00, 0, NULL),
('F4-RA-S1', 4, 'A', 1, 'COMPACT', 2.00, 0, NULL),
('F4-RA-S4', 4, 'A', 4, 'REGULAR', 5.00, 0, NULL),
('F4-RB-S2', 4, 'B', 2, 'COMPACT', 2.00, 0, NULL),
('F4-RB-S5', 4, 'B', 5, 'REGULAR', 5.00, 0, NULL),
('F4-RC-S3', 4, 'C', 3, 'COMPACT', 2.00, 0, NULL),
('F4-RC-S6', 4, 'C', 6, 'REGULAR', 5.00, 0, NULL),
('F4-RH-S7', 4, 'H', 7, 'HANDICAPPED', 2.00, 0, NULL),
('F5-RA-S1', 5, 'A', 1, 'COMPACT', 2.00, 0, NULL),
('F5-RA-S4', 5, 'A', 4, 'REGULAR', 5.00, 0, NULL),
('F5-RB-S2', 5, 'B', 2, 'COMPACT', 2.00, 0, NULL),
('F5-RB-S5', 5, 'B', 5, 'REGULAR', 5.00, 0, NULL),
('F5-RC-S3', 5, 'C', 3, 'COMPACT', 2.00, 0, NULL),
('F5-RC-S6', 5, 'C', 6, 'REGULAR', 5.00, 0, NULL),
('F5-RH-S7', 5, 'H', 7, 'HANDICAPPED', 2.00, 0, NULL),
('F1-RV-S1', 1, 'V', 1, 'RESERVED', 10.00, 0, NULL),
('F2-RV-S1', 2, 'V', 1, 'RESERVED', 10.00, 0, NULL),
('F3-RV-S1', 3, 'V', 1, 'RESERVED', 10.00, 0, NULL),
('F4-RV-S1', 4, 'V', 1, 'RESERVED', 10.00, 0, NULL),
('F5-RV-S1', 5, 'V', 1, 'RESERVED', 10.00, 0, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `revenue`
--

CREATE TABLE `revenue` (
  `id` int(11) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `timestamp` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `vehicle_entry`
--

CREATE TABLE `vehicle_entry` (
  `license_plate` varchar(20) NOT NULL,
  `entry_time` datetime NOT NULL,
  `spot_id` varchar(20) DEFAULT NULL,
  `exit_time` datetime DEFAULT NULL,
  `vehicle_type` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `fine`
--
ALTER TABLE `fine`
  ADD PRIMARY KEY (`fine_id`);

--
-- Indexes for table `floor`
--
ALTER TABLE `floor`
  ADD PRIMARY KEY (`floor_number`);

--
-- Indexes for table `parking_spot`
--
ALTER TABLE `parking_spot`
  ADD PRIMARY KEY (`spot_id`),
  ADD KEY `floor_number` (`floor_number`);

--
-- Indexes for table `revenue`
--
ALTER TABLE `revenue`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `vehicle_entry`
--
ALTER TABLE `vehicle_entry`
  ADD PRIMARY KEY (`license_plate`,`entry_time`),
  ADD KEY `spot_id` (`spot_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `revenue`
--
ALTER TABLE `revenue`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `parking_spot`
--
ALTER TABLE `parking_spot`
  ADD CONSTRAINT `parking_spot_ibfk_1` FOREIGN KEY (`floor_number`) REFERENCES `floor` (`floor_number`) ON DELETE CASCADE;

--
-- Constraints for table `vehicle_entry`
--
ALTER TABLE `vehicle_entry`
  ADD CONSTRAINT `vehicle_entry_ibfk_1` FOREIGN KEY (`spot_id`) REFERENCES `parking_spot` (`spot_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

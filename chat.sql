-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 01, 2019 at 05:36 PM
-- Server version: 10.1.40-MariaDB
-- PHP Version: 7.3.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `chat`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` tinyblob NOT NULL,
  `salt` varchar(20) NOT NULL,
  `last_ip` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `salt`, `last_ip`) VALUES
(1, 'test', 0xfd19145e3fdfc19ff83c45b6a2b000d8febd95758da9842f791a4949f8906123, 'jeQ2kfZVsLrWK6i5xZyt', NULL),
(2, 'merge', 0x4ffce4120ad9845b63e89dd740eb6a0c998a3f7f06a016a8d676f95ba664a999, 'gtAVN2kh3Azas68gr46O', NULL),
(3, 'merge2', 0x6807aeeec962417c546c64b26a0d1e433c0aba01aaec1630f437f5cc4ebaefb0, 'YIMbbwYBX/WoPBpQdCkg', NULL),
(6, 'test', 0x1108bc3e4f506bad4746f193708b905bb96739fafe88102b9990ea7aacf59d2e, 'jItN3rFVD3gLpy8Z/Mw2', NULL),
(20, 'q', 0x542c31a9cf69abb28958e4fc704dcc9834e563de45fe53d22066c68c9da7e562, 'vVwCFRdg33wRy8TvCPi7', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

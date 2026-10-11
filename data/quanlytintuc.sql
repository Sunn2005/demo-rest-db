-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               11.4.8-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for quanlytintuc
CREATE DATABASE IF NOT EXISTS `quanlytintuc` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `quanlytintuc`;

-- Dumping structure for table quanlytintuc.danhmuc
CREATE TABLE IF NOT EXISTS `danhmuc` (
  `MADM` int(11) NOT NULL AUTO_INCREMENT,
  `TENDANHMUC` varchar(100) NOT NULL,
  `NGUOIQUANLY` varchar(100) DEFAULT NULL,
  `GHICHU` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MADM`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytintuc.danhmuc: ~5 rows (approximately)
DELETE FROM `danhmuc`;
INSERT INTO `danhmuc` (`MADM`, `TENDANHMUC`, `NGUOIQUANLY`, `GHICHU`) VALUES
	(1, 'Thời sự', 'Nguyễn Văn A', 'Tin tức thời sự trong nước và quốc tế'),
	(2, 'Kinh tế', 'Lê Thị B', 'Tin tức tài chính, chứng khoán, thị trường'),
	(3, 'Thể thao', 'Trần Văn C', 'Tin thể thao trong nước và quốc tế'),
	(4, 'Giải trí', 'Phạm Thị D', 'Tin giải trí, showbiz, phim ảnh'),
	(5, 'Công nghệ', 'Hoàng Văn E', 'Tin tức công nghệ, khoa học, internet');

-- Dumping structure for table quanlytintuc.tintuc
CREATE TABLE IF NOT EXISTS `tintuc` (
  `MATT` int(11) NOT NULL AUTO_INCREMENT,
  `TIEUDE` varchar(200) NOT NULL,
  `NOIDUNGTT` mediumtext DEFAULT NULL,
  `LIENKET` varchar(200) DEFAULT NULL,
  `MADM` int(11) DEFAULT NULL,
  PRIMARY KEY (`MATT`),
  KEY `fk_tintuc_danhmuc` (`MADM`),
  CONSTRAINT `fk_tintuc_danhmuc` FOREIGN KEY (`MADM`) REFERENCES `danhmuc` (`MADM`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytintuc.tintuc: ~10 rows (approximately)
DELETE FROM `tintuc`;
INSERT INTO `tintuc` (`MATT`, `TIEUDE`, `NOIDUNGTT`, `LIENKET`, `MADM`) VALUES
	(1, 'Chính phủ họp bàn về tình hình kinh tế', 'Nội dung chi tiết về cuộc họp...', 'https://news.vn/tintuc1', 1),
	(2, 'Thị trường chứng khoán tăng mạnh', 'VN-Index vượt mốc 1.200 điểm...', 'https://news.vn/tintuc2', 2),
	(3, 'Đội tuyển Việt Nam thắng Thái Lan 2-1', 'Trận đấu hấp dẫn trên sân Mỹ Đình...', 'https://news.vn/tintuc3', 3),
	(4, 'Ca sĩ nổi tiếng ra mắt album mới', 'Album được phát hành trên nhiều nền tảng...', 'https://news.vn/tintuc4', 4),
	(5, 'Huawei ra mắt điện thoại mới', 'Smartphone trang bị công nghệ AI...', 'https://news.vn/tintuc5', 5),
	(6, 'Thời tiết miền Trung mưa lớn', 'Nhiều tỉnh chịu ảnh hưởng mưa bão...', 'https://news.vn/tintuc6', 1),
	(7, 'Giá vàng thế giới tăng', 'Giá vàng tăng do nhu cầu trú ẩn...', 'https://news.vn/tintuc7', 2),
	(8, 'Cầu thủ trẻ lập hat-trick', 'Tỏa sáng trong giải U23 châu Á...', 'https://news.vn/tintuc8', 3),
	(9, 'Phim bom tấn ra mắt khán giả Việt', 'Phim chiếu rạp thu hút đông đảo khán giả...', 'https://news.vn/tintuc9', 4),
	(10, 'Tesla giới thiệu công nghệ pin mới', 'danhmucPin bền hơn, sạc nhanh hơn...', 'https://news.vn/tintuc10', 5);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

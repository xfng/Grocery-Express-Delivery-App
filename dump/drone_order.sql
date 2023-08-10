-- MySQL dump 10.13  Distrib 8.0.28, for macos11 (x86_64)
--
-- Host: 127.0.0.1    Database: drone
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `customer_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `ordertype_id` varchar(255) NOT NULL,
  `order_type` varchar(255) DEFAULT NULL,
  `assigned_drone_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`order_id`,`store_name`),
  KEY `storeName` (`store_name`),
  KEY `customerAccount` (`customer_account`),
  KEY `order_ibfk_4` (`ordertype_id`),
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`store_name`) REFERENCES `store` (`store_name`),
  CONSTRAINT `order_ibfk_2` FOREIGN KEY (`customer_account`) REFERENCES `customer` (`account`),
  CONSTRAINT `order_ibfk_4` FOREIGN KEY (`ordertype_id`) REFERENCES `ordertype` (`ordertype_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES ('purchaseA','kroger','aapple2','2023-07-23','2',NULL,'1'),('purchaseA','publix','ccherry4',NULL,'1',NULL,NULL),('purchaseC','kroger','aapple2','2023-07-23','2',NULL,'2'),('purchaseD','kroger','ccherry4',NULL,'1',NULL,NULL),('purchaseE','kroger','aapple2',NULL,'1',NULL,'1'),('purchaseR','kroger','aapple2',NULL,'1',NULL,'1'),('purchaseZ','kroger','aapple2',NULL,'1',NULL,'1'),('returnA','kroger','aapple2','2023-07-24','4',NULL,'2');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-07-24  0:22:18

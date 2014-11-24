-- MySQL dump 10.13  Distrib 5.5.24, for osx10.6 (i386)
--
-- Host: localhost    Database: gitfactor
-- ------------------------------------------------------
-- Server version	5.5.24

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `commit`
--

DROP TABLE IF EXISTS `commit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commit` (
  `id_commit` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `hash` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `parent` int(11) DEFAULT NULL,
  `message` varchar(1024) DEFAULT NULL,
  `id_repository` int(10) unsigned NOT NULL,
  `status` varchar(20) DEFAULT NULL,
  `author_name` varchar(255) DEFAULT NULL,
  `id_tag` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_commit`,`id_repository`),
  UNIQUE KEY `id_commit_UNIQUE` (`id_commit`),
  KEY `fk_commit_repository_idx` (`id_repository`),
  KEY `fk_commit_tag1_idx` (`id_tag`),
  CONSTRAINT `fk_commit_repository` FOREIGN KEY (`id_repository`) REFERENCES `repository` (`id_repository`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_commit_tag` FOREIGN KEY (`id_tag`) REFERENCES `tag` (`id_tag`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=258789 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `operation`
--

DROP TABLE IF EXISTS `operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operation` (
  `id_operation` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `id_refactoring` int(10) unsigned NOT NULL,
  `class_name` varchar(1024) DEFAULT NULL,
  `operation_visibility` varchar(45) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id_operation`,`id_refactoring`),
  UNIQUE KEY `id_operation_UNIQUE` (`id_operation`),
  KEY `fk_operation_refactoring1_idx` (`id_refactoring`),
  CONSTRAINT `fk_operation_refactoring1` FOREIGN KEY (`id_refactoring`) REFERENCES `refactoring` (`id_refactoring`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=182709 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `refactoring`
--

DROP TABLE IF EXISTS `refactoring`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refactoring` (
  `id_refactoring` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `id_commit` int(10) unsigned NOT NULL,
  `source_class_name` varchar(1024) DEFAULT NULL,
  `target_class_name` varchar(1024) DEFAULT NULL,
  `attribute_name` varchar(1024) DEFAULT NULL,
  `description` varchar(10000) DEFAULT NULL,
  PRIMARY KEY (`id_refactoring`,`id_commit`),
  KEY `fk_refactoring_commit1_idx` (`id_commit`),
  CONSTRAINT `fk_refactoring_commit1` FOREIGN KEY (`id_commit`) REFERENCES `commit` (`id_commit`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=118188 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repository`
--

DROP TABLE IF EXISTS `repository`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repository` (
  `id_repository` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `url` varchar(255) NOT NULL,
  `author` varchar(45) NOT NULL,
  `total_commits` varchar(45) DEFAULT NULL,
  `start` datetime DEFAULT NULL,
  `end` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `total_stars` int(11) DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  `default_branch` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_repository`),
  UNIQUE KEY `id_repository_UNIQUE` (`id_repository`)
) ENGINE=InnoDB AUTO_INCREMENT=897 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag` (
  `id_tag` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `id_repository` int(10) unsigned DEFAULT NULL,
  `date` timestamp NULL DEFAULT NULL,
  `author_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_tag`),
  KEY `fk_repository_idx` (`id_repository`),
  CONSTRAINT `fk_repository` FOREIGN KEY (`id_repository`) REFERENCES `repository` (`id_repository`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=989 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-11-24  8:22:43

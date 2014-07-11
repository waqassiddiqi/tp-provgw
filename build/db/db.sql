-- MySQL dump 10.13  Distrib 5.6.19, for Win64 (x86_64)
--
-- Host: localhost    Database: tp_skycall
-- ------------------------------------------------------
-- Server version	5.6.19

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
-- Table structure for table `mapping_tab`
--

DROP TABLE IF EXISTS `mapping_tab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mapping_tab` (
  `mapping_id` int(11) NOT NULL AUTO_INCREMENT,
  `virtual_id` varchar(45) DEFAULT NULL,
  `skype_id` varchar(120) DEFAULT NULL,
  `date_created` datetime DEFAULT NULL,
  PRIMARY KEY (`mapping_id`)
) ENGINE=MyISAM AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `skype_list_tab`
--

DROP TABLE IF EXISTS `skype_list_tab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `skype_list_tab` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `subscriber_id` int(11) DEFAULT NULL,
  `mapping_id` int(11) DEFAULT NULL,
  `date_created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subscriber_tab`
--

DROP TABLE IF EXISTS `subscriber_tab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscriber_tab` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `msisdn` varchar(20) DEFAULT NULL,
  `subtype` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `subscribed_date` datetime DEFAULT NULL,
  `next_renewal_date` datetime DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `last_updated_date` datetime DEFAULT NULL,
  `last_successful_charging_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `svc_mgmt_tab`
--

DROP TABLE IF EXISTS `svc_mgmt_tab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `svc_mgmt_tab` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `msisdn` varchar(20) DEFAULT NULL,
  `skype_id` varchar(45) DEFAULT NULL,
  `action_type` tinyint(2) DEFAULT NULL COMMENT '1-subscribe,2-terminate,3-add_list,4-remove_list,5-view_list',
  `stat` tinyint(2) DEFAULT '0' COMMENT '0-success,1-failed',
  `channel` varchar(100) DEFAULT NULL COMMENT '1-SMS,2-CS,3-WEB',
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=60 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `virtual_id_tab`
--

DROP TABLE IF EXISTS `virtual_id_tab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `virtual_id_tab` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `virtual_id` varchar(11) NOT NULL,
  PRIMARY KEY (`id`,`virtual_id`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'tp_skycall'
--
/*!50003 DROP PROCEDURE IF EXISTS `sp_getVirtualId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE PROCEDURE `sp_getVirtualId`(IN skypeId varchar(100), IN msisdn_in VARCHAR(50))
BEGIN
	DECLARE virtualId VARCHAR(11) DEFAULT NULL;
	DECLARE mappingId INT;
	DECLARE subscriberId INT;

	SELECT id INTO subscriberId FROM subscriber_tab WHERE msisdn = msisdn_in LIMIT 1;
	
	SELECT virtual_id INTO virtualId FROM mapping_tab WHERE skype_id = skypeId;

	IF virtualId IS NULL THEN
		SELECT virtual_id INTO virtualId FROM virtual_id_tab LIMIT 1;

		INSERT INTO mapping_tab(skype_id, virtual_id, date_created)
				SELECT skypeId, virtualId, NOW();
	END IF;

	DELETE FROM virtual_id_tab WHERE virtual_id = virtualId;
	
	SELECT mapping_id INTO mappingId FROM mapping_tab WHERE skype_id = skypeId AND virtual_id = virtualId;

	SELECT mappingId, skypeId, virtualId;

	INSERT INTO skype_list_tab(subscriber_id, mapping_id, date_created) 
		SELECT subscriberId, mappingId, NOW();

	SELECT virtualId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-07-06 20:38:24

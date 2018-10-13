/*
Navicat MySQL Data Transfer

Source Server         : TJDMySQL
Source Server Version : 50638
Source Host           : localhost:3306
Source Database       : netdisk

Target Server Type    : MYSQL
Target Server Version : 50638
File Encoding         : 65001

Date: 2018-10-13 08:45:25
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_file_items
-- ----------------------------
DROP TABLE IF EXISTS `sys_file_items`;
CREATE TABLE `sys_file_items` (
  `file_md5` varchar(255) NOT NULL,
  `file_size` decimal(15,0) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `upload_time` datetime DEFAULT NULL,
  `uid_first` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`file_md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `uid` bigint(100) NOT NULL AUTO_INCREMENT,
  `user_email` varchar(255) NOT NULL,
  `user_password` varchar(255) NOT NULL,
  `user_size` bigint(15) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_file_drictory
-- ----------------------------
DROP TABLE IF EXISTS `user_file_drictory`;
CREATE TABLE `user_file_drictory` (
  `did` bigint(100) NOT NULL AUTO_INCREMENT,
  `dname` varchar(255) NOT NULL,
  `rdid` bigint(100) NOT NULL,
  `uid` bigint(255) NOT NULL,
  PRIMARY KEY (`did`),
  KEY `user_file_directory_uid` (`uid`),
  KEY `user_file_directory_rdid` (`rdid`),
  CONSTRAINT `user_file_directory_rdid` FOREIGN KEY (`rdid`) REFERENCES `user_file_drictory` (`did`) ON DELETE CASCADE,
  CONSTRAINT `user_file_directory_uid` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_file_items
-- ----------------------------
DROP TABLE IF EXISTS `user_file_items`;
CREATE TABLE `user_file_items` (
  `fid` bigint(255) NOT NULL AUTO_INCREMENT,
  `fname` varchar(255) NOT NULL,
  `upload_time` datetime DEFAULT NULL,
  `did` bigint(100) NOT NULL,
  `uid` bigint(100) NOT NULL,
  `file_size` decimal(15,0) NOT NULL,
  `file_md5` varchar(255) NOT NULL,
  PRIMARY KEY (`fid`),
  KEY `user_file_items_md5` (`file_md5`),
  KEY `user_file_items_foreign` (`did`),
  CONSTRAINT `user_file_items_foreign` FOREIGN KEY (`did`) REFERENCES `user_file_drictory` (`did`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;

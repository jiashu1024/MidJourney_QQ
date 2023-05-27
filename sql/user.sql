/*
 Navicat Premium Data Transfer

 Source Server         : tencent
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : mysql.zhangjiashu.tech:27172
 Source Schema         : midjourney

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 27/05/2023 15:21:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `qq` varchar(10) NOT NULL,
  `role` varchar(255) NOT NULL,
  `fast_count` int(11) DEFAULT NULL,
  `fast_expire_time` datetime DEFAULT NULL,
  `relax_count` int(11) DEFAULT NULL,
  `relax_expire_time` datetime DEFAULT NULL,
  PRIMARY KEY (`qq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

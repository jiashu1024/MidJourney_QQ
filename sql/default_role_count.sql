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

 Date: 27/05/2023 15:21:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for default_role_count
-- ----------------------------
DROP TABLE IF EXISTS `default_role_count`;
CREATE TABLE `default_role_count` (
  `role` varchar(255) NOT NULL,
  `fast_count` int(11) DEFAULT NULL,
  `relax_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of default_role_count
-- ----------------------------
BEGIN;
INSERT INTO `default_role_count` (`role`, `fast_count`, `relax_count`) VALUES ('admin', 9999, 9999);
INSERT INTO `default_role_count` (`role`, `fast_count`, `relax_count`) VALUES ('normal', 10, 5);
INSERT INTO `default_role_count` (`role`, `fast_count`, `relax_count`) VALUES ('plus', 100, 50);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

/*
 Navicat Premium Data Transfer

 Source Server         : 天翼云
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : tyy.zhangjiashu.tech:3306
 Source Schema         : midjourney

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 03/06/2023 21:26:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for image_message
-- ----------------------------
DROP TABLE IF EXISTS `image_message`;
CREATE TABLE `image_message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `qq` varchar(255) DEFAULT NULL,
  `group_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `message_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

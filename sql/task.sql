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

 Date: 27/05/2023 15:21:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` varchar(16) NOT NULL,
  `mode` varchar(255) NOT NULL,
  `action` varchar(255) DEFAULT NULL,
  `final_prompt` text,
  `image_url` varchar(255) DEFAULT NULL,
  `result` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  `message_chain` text,
  `create_time` datetime DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  `request_id` varchar(255) DEFAULT NULL,
  `message_hash` varchar(255) DEFAULT NULL,
  `source_key` varchar(255) DEFAULT NULL,
  `related_task_id` varchar(16) DEFAULT NULL,
  `root_task_id` varchar(16) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`task_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

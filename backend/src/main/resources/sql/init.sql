-- 流浪动物救助系统 数据库初始化脚本（含建库语句与演示数据）
CREATE DATABASE IF NOT EXISTS stray_animal_rescue DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE stray_animal_rescue;

-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: stray_animal_rescue
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `adoption_application`
--

DROP TABLE IF EXISTS `adoption_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adoption_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `animal_id` bigint NOT NULL COMMENT '动物ID',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申请理由',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0-待审核 1-通过 2-拒绝',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE,
  KEY `idx_animal` (`animal_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='领养申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adoption_application`
--

LOCK TABLES `adoption_application` WRITE;
/*!40000 ALTER TABLE `adoption_application` DISABLE KEYS */;
INSERT INTO `adoption_application` VALUES (8,2,2,'我很喜欢小狗，尤其是拉布拉多，小布看起来很可爱！我非常喜欢它，想带它回家！',1,'情况属实，同意领养','2025-03-23 01:00:03','2025-03-23 15:17:49'),(9,5,3,'家里有养宠经验，有固定住所，会好好照顾它。',0,NULL,'2025-03-23 15:50:57','2025-03-23 15:50:56'),(10,2,14,'233234423234',3,'2','2025-11-18 20:20:21','2025-11-18 21:45:17'),(11,2,9,'我很喜欢小动物，家庭条件也允许，希望能通过申请。',1,'33333333333333333','2025-11-18 22:30:18','2025-11-18 22:33:32');
/*!40000 ALTER TABLE `adoption_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animal`
--

DROP TABLE IF EXISTS `animal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '动物ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `category_id` bigint NOT NULL COMMENT '类别ID',
  `breed` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '品种',
  `gender` tinyint NOT NULL COMMENT '性别 0-雄性 1-雌性',
  `age` int DEFAULT NULL COMMENT '年龄(月)',
  `weight` decimal(5,2) DEFAULT NULL COMMENT '体重(kg)',
  `health_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '健康状态',
  `features` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '特征',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '描述',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '图片URL',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0-待领养 1-已领养 2-救助中',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览数',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `favorite_count` int NOT NULL DEFAULT '0' COMMENT '收藏数',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '???',
  `rescue_time` datetime DEFAULT NULL COMMENT '救助时间',
  `rescue_location` varchar(100) DEFAULT NULL COMMENT '救助地点',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_category` (`category_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='动物表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal`
--

LOCK TABLES `animal` WRITE;
/*!40000 ALTER TABLE `animal` DISABLE KEYS */;
INSERT INTO `animal` VALUES (1,'小a',1,'英短',1,14,25.00,'健康','毛短、身材短、尾巴短、四肢短、耳朵短','是一直14个月大的25kg的英短小猫，他的名字叫小a','/uploads//animal/cca1c27a0b2f46b8afaf1517f66f6774.jpg',2,0,0,0,0,'2025-03-23 07:26:07','北京市海淀区','2025-03-21 10:40:18','2025-11-18 21:47:11'),(2,'小布',2,'拉布拉多',0,12,25.00,'生病','友善温和‌，聪明易训‌，忠诚可靠','一只友善温和‌，聪明易训‌，忠诚可靠的小拉布拉多','/uploads//animal/83b157a2dd024d659e0c4344c59ccaf3.jpg',1,0,0,0,0,NULL,'北京市丰台区','2025-03-22 03:56:50','2025-11-18 21:47:11'),(3,'小仓',5,'银狐仓鼠',0,9,10.00,'亚健康','全身毛茸茸的白毛，小巧玲珑','银狐仓鼠从背面看与可爱的小白兔非常相似，全身毛茸茸的白毛，小巧玲珑的非常讨人喜。关于银狐仓鼠的繁殖交配问题，全年皆可。基本特征还是像仓鼠的，炯炯有神的眼睛给人感觉精神爽朗。银狐仓鼠对生活中的气味非常敏感，倘若你想与它玩耍，如果你手上沾有美味食物的味道，这时你就要小心了，他很有可能把你的手当做食物而咬伤到你。','/uploads//animal/e9cedb8be8ea4aefa00be3a20c2c7794.jpg',0,0,0,0,0,NULL,'北京市西城区','2025-03-23 15:23:30','2025-11-18 21:47:11'),(4,'小橘',1,'橘猫',0,8,4.50,'健康','橘色短毛，圆脸大眼，性格温顺','这是一只8个月大的橘猫，非常亲人，喜欢被抚摸。它有着标志性的橘色条纹和圆滚滚的身材，是个小吃货。性格温顺，适合家庭饲养。','/uploads/animal/cat_1.jpg',0,47,12,9,2,'2025-03-15 10:20:00','北京市东城区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(5,'小白',1,'中华田园猫',1,6,3.20,'健康','纯白色，蓝眼睛，优雅安静','一只6个月大的纯白色小猫，有着漂亮的蓝色眼睛。性格安静优雅，喜欢晒太阳和独处。非常干净，会自己使用猫砂盆。','/uploads/animal/cat_2.jpg',0,40,15,10,0,'2025-03-16 14:30:00','北京市朝阳区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(6,'小黑',1,'黑猫',0,10,4.80,'健康','全身黑色，绿眼睛，活泼好动','10个月大的黑猫，有着神秘的绿色眼睛。虽然外表看起来高冷，但其实非常粘人。喜欢玩耍，精力充沛，需要有耐心的主人。','/uploads/animal/cat_3.jpg',0,52,19,12,1,'2025-03-17 09:15:00','北京市海淀区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(7,'小花',1,'三花猫',1,12,3.80,'健康','三色花纹，温柔体贴','12个月大的三花猫，有着漂亮的三色花纹。性格温柔体贴，非常适合陪伴老人和小孩。喜欢安静的环境，是个乖巧的小天使。','/uploads/animal/cat_2.jpg',0,41,11,7,0,'2025-03-18 16:45:00','北京市丰台区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(8,'小灰',1,'英短蓝猫',0,9,5.20,'健康','蓝灰色短毛，圆脸，憨厚可爱','9个月大的英短蓝猫，有着标准的圆脸和蓝灰色被毛。性格憨厚可爱，不怕生，喜欢和人互动。是个小吃货，容易发胖需要控制饮食。','/uploads/animal/cat_5.jpg',0,67,22,15,0,'2025-03-19 11:20:00','北京市西城区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(9,'小金',2,'金毛',0,18,28.00,'健康','金色长毛，温顺友善，聪明伶俐','18个月大的金毛犬，性格温顺友善，对人类特别友好。聪明伶俐，容易训练，是理想的家庭伴侣犬。喜欢运动，需要每天散步。','/uploads/animal/dog_1.jpg',1,112,28,20,3,'2025-03-14 08:30:00','北京市东城区','2025-03-23 10:00:00','2025-11-18 22:33:32'),(10,'小哈',2,'哈士奇',0,15,22.00,'健康','黑白毛色，蓝眼睛，活泼好动','15个月大的哈士奇，有着标志性的蓝色眼睛和黑白毛色。性格活泼好动，精力旺盛，需要大量运动。聪明但有点调皮，需要耐心训练。','/uploads/animal/dog_2.jpg',0,73,25,18,0,'2025-03-15 13:45:00','北京市朝阳区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(11,'小柯',2,'柯基',1,14,11.50,'健康','短腿长身，棕白色，可爱活泼','14个月大的柯基犬，有着标志性的短腿和长身体。性格活泼可爱，喜欢和人玩耍。虽然腿短但跑得很快，需要适量运动。','/uploads/animal/dog_1.jpg',0,56,20,13,0,'2025-03-16 10:15:00','北京市海淀区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(12,'小泰',2,'泰迪',0,10,5.80,'健康','棕色卷毛，小巧可爱，聪明活泼','10个月大的泰迪犬，有着可爱的棕色卷毛。体型小巧，非常适合公寓饲养。聪明活泼，容易训练，是很好的伴侣犬。','/uploads/animal/dog_4.jpg',0,48,16,11,0,'2025-03-17 15:30:00','北京市丰台区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(13,'小边',2,'边牧',0,16,18.50,'健康','黑白毛色，聪明机警，服从性强','16个月大的边境牧羊犬，智商极高，学习能力强。性格机警，服从性好，是工作犬的优秀代表。需要大量的运动和智力刺激。','/uploads/animal/dog_5.jpg',0,61,21,14,0,'2025-03-18 09:00:00','北京市西城区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(14,'小萨',2,'萨摩耶',1,17,20.00,'健康','纯白长毛，微笑天使，温柔友善','17个月大的萨摩耶，有着标志性的\"微笑\"表情和雪白的长毛。性格温柔友善，对人类特别友好。需要定期梳理毛发，适合有时间照顾的家庭。','/uploads/animal/dog_6.jpg',0,83,26,19,0,'2025-03-19 14:20:00','北京市东城区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(15,'小雪',5,'雪地仓鼠',1,6,0.08,'健康','白色毛发，红眼睛，温顺可爱','6个月大的雪地仓鼠，全身雪白，有着可爱的红色眼睛。性格温顺，容易饲养，适合新手。需要准备仓鼠笼、跑轮等基本设施。','/uploads/animal/hamster_1.jpg',0,32,9,6,0,'2025-03-20 10:30:00','北京市朝阳区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(16,'小布',5,'布丁仓鼠',0,5,0.09,'健康','黄色毛发，黑眼睛，活泼好动','5个月大的布丁仓鼠，有着漂亮的黄色毛发。性格活泼好动，喜欢在跑轮上运动。饲养简单，是很好的入门宠物。','/uploads/animal/hamster_2.jpg',0,29,8,5,0,'2025-03-21 11:45:00','北京市海淀区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(17,'小白兔',3,'荷兰侏儒兔',1,8,1.20,'健康','纯白色，耳朵短小，温顺可爱','8个月大的荷兰侏儒兔，体型小巧，纯白色毛发。性格温顺可爱，喜欢吃蔬菜和干草。需要准备兔笼和活动空间。','/uploads/animal/rabbit_1.jpg',0,44,14,9,0,'2025-03-22 09:30:00','北京市丰台区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(18,'小灰兔',3,'垂耳兔',0,10,1.80,'健康','灰色毛发，长耳朵下垂，温柔安静','10个月大的垂耳兔，有着标志性的下垂长耳朵。性格温柔安静，喜欢被抚摸。需要定期梳理毛发，适合有耐心的主人。','/uploads/animal/rabbit_2.jpg',0,37,12,8,0,'2025-03-22 14:15:00','北京市西城区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(19,'小黄',2,'中华田园犬',0,13,15.00,'健康','黄色短毛，忠诚护主，适应力强','13个月大的中华田园犬，有着健康的黄色短毛。性格忠诚护主，适应力强，容易饲养。是本土犬种，非常适合中国家庭。','/uploads/animal/dog_7.jpg',0,55,17,12,0,'2025-03-20 08:45:00','北京市东城区','2025-03-23 10:00:00','2025-11-18 21:47:11'),(20,'小黑狗',2,'中华田园犬',0,11,12.50,'健康','黑色短毛，机警聪明，看家护院','11个月大的中华田园犬，全身黑色短毛。性格机警聪明，有很强的看家护院能力。对主人忠诚，容易训练。','/uploads/animal/dog_8.jpg',0,49,15,10,0,'2025-03-21 13:20:00','北京市朝阳区','2025-03-23 10:00:00','2025-11-18 21:47:11');
/*!40000 ALTER TABLE `animal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animal_category`
--

DROP TABLE IF EXISTS `animal_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '类别ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类别名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='动物类别表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_category`
--

LOCK TABLES `animal_category` WRITE;
/*!40000 ALTER TABLE `animal_category` DISABLE KEYS */;
INSERT INTO `animal_category` VALUES (1,'猫','猫的分类','2025-03-21 10:29:06','2025-03-21 10:29:06'),(2,'狗','狗的分类','2025-03-21 10:29:21','2025-03-21 10:29:21'),(3,'兔','宠物兔','2025-11-18 20:12:38','2025-11-18 20:12:38'),(5,'鼠','宠物鼠','2025-03-23 15:21:43','2025-03-23 15:21:43');
/*!40000 ALTER TABLE `animal_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animal_comment`
--

DROP TABLE IF EXISTS `animal_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `animal_id` bigint NOT NULL COMMENT '??ID',
  `user_id` bigint NOT NULL COMMENT '??ID',
  `content` text NOT NULL COMMENT '????',
  `parent_id` bigint DEFAULT NULL COMMENT '???ID????',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_animal` (`animal_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='?????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_comment`
--

LOCK TABLES `animal_comment` WRITE;
/*!40000 ALTER TABLE `animal_comment` DISABLE KEYS */;
INSERT INTO `animal_comment` VALUES (1,4,2,'小橘真的好可爱啊！',NULL,'2025-11-18 21:08:53','2025-11-18 21:08:53'),(2,4,5,'我也觉得，橘猫都很温顺',NULL,'2025-11-18 21:08:53','2025-11-18 21:08:53'),(3,9,2,'金毛是最好的家庭伴侣犬！',NULL,'2025-11-18 21:08:53','2025-11-18 21:08:53'),(4,9,5,'同意！我家也养了一只金毛',NULL,'2025-11-18 21:08:53','2025-11-18 21:08:53'),(5,6,2,'黑猫其实很可爱，不要迷信',NULL,'2025-11-18 21:08:53','2025-11-18 21:08:53'),(6,9,2,'为',NULL,'2025-11-18 21:29:51','2025-11-18 21:29:51');
/*!40000 ALTER TABLE `animal_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animal_extended`
--

DROP TABLE IF EXISTS `animal_extended`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_extended` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `animal_id` bigint NOT NULL COMMENT '动物ID',
  `size` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '体型分类：小型/中型/大型',
  `personality` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性格标签（JSON数组）["温顺","活泼","粘人"]',
  `adoption_fee` int DEFAULT '0' COMMENT '领养费用（0表示免费）',
  `special_needs` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '特殊需求（JSON数组）["需要大空间","适合新手"]',
  `days_in_rescue` int DEFAULT '0' COMMENT '救助天数',
  `popularity_score` double DEFAULT '0' COMMENT '热度得分',
  `energy_level` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活力等级：低/中/高',
  `friendliness` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '友好度：低/中/高',
  `trainability` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可训练度：低/中/高',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_animal_id` (`animal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动物扩展属性表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_extended`
--

LOCK TABLES `animal_extended` WRITE;
/*!40000 ALTER TABLE `animal_extended` DISABLE KEYS */;
INSERT INTO `animal_extended` VALUES (1,1,'大型','[\"温顺\",\"独立\",\"安静\"]',0,'[\"适合公寓\",\"需要猫砂盆\"]',242,0,'中','高','中','2025-11-18 22:53:11','2025-11-18 22:53:11'),(2,2,'大型','[\"活泼\",\"友善\",\"忠诚\"]',0,'[\"需要运动空间\",\"需要每日遛狗\"]',241,0,'高','高','高','2025-11-18 22:53:11','2025-11-18 22:53:11'),(3,3,'中型','[\"可爱\",\"温顺\",\"小巧\"]',0,'[\"需要笼子\",\"适合新手\"]',240,0,'低','高','中','2025-11-18 22:53:11','2025-11-18 22:53:11'),(4,4,'小型','[\"温顺\",\"独立\",\"安静\"]',0,'[\"适合公寓\",\"需要猫砂盆\"]',240,29.9,'中','高','中','2025-11-18 22:53:11','2025-11-18 22:53:11'),(5,5,'小型','[\"温顺\",\"独立\",\"安静\"]',0,'[\"适合公寓\",\"需要猫砂盆\"]',240,32.2,'中','高','中','2025-11-18 22:53:11','2025-11-18 22:53:11'),(6,6,'小型','[\"温顺\",\"独立\",\"安静\"]',0,'[\"适合公寓\",\"需要猫砂盆\"]',240,40.9,'中','高','中','2025-11-18 22:53:11','2025-11-18 22:53:11'),(7,7,'小型','[\"温顺\",\"独立\",\"安静\"]',0,'[\"适合公寓\",\"需要猫砂盆\"]',240,27,'中','高','中','2025-11-18 22:53:11','2025-11-18 22:53:11'),(8,8,'中型','[\"温顺\",\"独立\",\"安静\"]',0,'[\"适合公寓\",\"需要猫砂盆\"]',240,50.5,'中','高','中','2025-11-18 22:53:11','2025-11-18 22:53:11'),(9,9,'大型','[\"活泼\",\"友善\",\"忠诚\"]',0,'[\"需要运动空间\",\"需要每日遛狗\"]',240,73.2,'高','高','高','2025-11-18 22:53:11','2025-11-18 22:53:11'),(10,10,'大型','[\"活泼\",\"友善\",\"忠诚\"]',0,'[\"需要运动空间\",\"需要每日遛狗\"]',240,57.4,'高','高','高','2025-11-18 22:53:11','2025-11-18 22:53:11'),(11,11,'中型','[\"活泼\",\"友善\",\"忠诚\"]',0,'[\"需要运动空间\",\"需要每日遛狗\"]',240,43.8,'高','高','高','2025-11-18 22:53:11','2025-11-18 22:53:11'),(12,12,'中型','[\"活泼\",\"友善\",\"忠诚\"]',0,'[\"需要运动空间\",\"需要每日遛狗\"]',240,36.6,'高','高','高','2025-11-18 22:53:11','2025-11-18 22:53:11'),(13,13,'大型','[\"活泼\",\"友善\",\"忠诚\"]',0,'[\"需要运动空间\",\"需要每日遛狗\"]',240,47,'高','高','高','2025-11-18 22:53:11','2025-11-18 22:53:11'),(14,14,'大型','[\"活泼\",\"友善\",\"忠诚\"]',0,'[\"需要运动空间\",\"需要每日遛狗\"]',240,61.8,'高','高','高','2025-11-18 22:53:11','2025-11-18 22:53:11'),(15,15,'小型','[\"可爱\",\"温顺\",\"小巧\"]',0,'[\"需要笼子\",\"适合新手\"]',240,21.9,'低','高','中','2025-11-18 22:53:11','2025-11-18 22:53:11'),(16,16,'小型','[\"可爱\",\"温顺\",\"小巧\"]',0,'[\"需要笼子\",\"适合新手\"]',240,19.3,'低','高','中','2025-11-18 22:53:11','2025-11-18 22:53:11'),(17,17,'小型','[\"友善\",\"温顺\"]',0,'[\"需要笼子\",\"适合新手\"]',240,32,'低','高','中','2025-11-18 22:53:11','2025-11-18 22:53:11'),(18,18,'小型','[\"友善\",\"温顺\"]',0,'[\"需要笼子\",\"适合新手\"]',240,27.5,'低','高','中','2025-11-18 22:53:11','2025-11-18 22:53:11'),(19,19,'大型','[\"活泼\",\"友善\",\"忠诚\"]',0,'[\"需要运动空间\",\"需要每日遛狗\"]',240,40.4,'高','高','高','2025-11-18 22:53:11','2025-11-18 22:53:11'),(20,20,'中型','[\"活泼\",\"友善\",\"忠诚\"]',0,'[\"需要运动空间\",\"需要每日遛狗\"]',240,35.2,'高','高','高','2025-11-18 22:53:11','2025-11-18 22:53:11');
/*!40000 ALTER TABLE `animal_extended` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animal_favorite`
--

DROP TABLE IF EXISTS `animal_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `animal_id` bigint NOT NULL COMMENT '动物ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_animal` (`user_id`,`animal_id`) USING BTREE,
  KEY `idx_animal` (`animal_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='动物收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_favorite`
--

LOCK TABLES `animal_favorite` WRITE;
/*!40000 ALTER TABLE `animal_favorite` DISABLE KEYS */;
INSERT INTO `animal_favorite` VALUES (2,5,8,'2025-11-18 20:12:38'),(3,5,13,'2025-11-18 20:12:38'),(4,2,6,'2025-11-18 20:15:44'),(5,2,4,'2025-11-18 23:35:43');
/*!40000 ALTER TABLE `animal_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animal_like`
--

DROP TABLE IF EXISTS `animal_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `animal_id` bigint NOT NULL COMMENT '动物ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_animal` (`user_id`,`animal_id`) USING BTREE,
  KEY `idx_animal` (`animal_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='动物点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_like`
--

LOCK TABLES `animal_like` WRITE;
/*!40000 ALTER TABLE `animal_like` DISABLE KEYS */;
INSERT INTO `animal_like` VALUES (1,2,4,'2025-11-18 20:12:38'),(2,2,9,'2025-11-18 20:12:38'),(3,5,5,'2025-11-18 20:12:38'),(4,5,8,'2025-11-18 20:12:38'),(5,2,6,'2025-11-18 20:15:42'),(6,2,11,'2025-11-18 20:15:48');
/*!40000 ALTER TABLE `animal_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement`
--

LOCK TABLES `announcement` WRITE;
/*!40000 ALTER TABLE `announcement` DISABLE KEYS */;
INSERT INTO `announcement` VALUES (1,'《共建爱心家园 守护流浪动物——XX社区流浪动物保护计划倡议书》','亲爱的社区居民朋友们：\n\n在我们生活的社区里，活跃着一群特殊的小生命——流浪动物。它们渴望温暖的家园，也需要我们的关怀与守护。为了让每个生命都能被温柔以待，XX社区即日起正式启动\"爱心守护计划\"，现向全体居民发出如下倡议：\n\n一、科学关爱 文明相助\n▶ 发现流浪动物时请勿驱赶伤害，可联系动物保护站（电话：XXX-XXXXXXX）进行专业救助\n▶ 投喂时请使用专用容器，避免直接接触，投喂后及时清理现场\n\n二、以爱为家 领养接力\n▶ 开放\"周末爱心领养日\"（每周六10:00-16:00社区广场）\n▶ 提供免费绝育疫苗服务，成功领养家庭可获宠物大礼包\n\n三、物资捐赠 温暖传递\n急需物资清单：\n✓ 宠物粮（密封包装） ✓ 保暖毛毯\n✓ 宠物药品 ✓ 清洁用品\n捐赠点：社区服务中心3号窗口（每日8:30-17:30）\n\n四、志愿服务 为爱同行\n招募岗位：\n◆ 临时看护志愿者 ◆ 爱心宣传大使\n◆ 活动协助专员 ◆ 医疗护送司机\n报名方式：扫描公告下方二维码填写申请表\n\n五、文明倡导 守护和谐\n▶ 养宠家庭请做好登记，外出牵好牵引绳\n▶ 发现虐待行为请立即拨打24小时举报热线：XXX-XXXXXXX\n\n每一份善意都将化作照亮生命的微光，让我们携手为流浪动物筑起温暖的避风港。即日起至2023年X月X日，参与本计划的居民可累计爱心积分，兑换社区定制纪念品。\n\n守护热线：XXX-XXXXXXX（8:30-20:30）\n微信公众号：XX社区爱心驿站\n\nXX社区居民委员会\n2023年X月X日',1,'2025-03-20 22:58:29','2025-03-22 04:36:54'),(3,'关于文明关爱流浪动物、共建和谐社区的倡议书','尊敬的社区居民：\n\n为营造温暖有爱的社区环境，倡导尊重生命、科学管理的理念，现就流浪动物保护相关事项倡议如下：\n\n不伤害、不驱赶\n▶ 若遇流浪动物，请勿攻击或惊吓，避免引发动物应激反应。\n▶ 发现受伤、患病动物，可联系社区物业或动物保护机构（联系电话：XXX-XXXXXXX）。\n\n科学救助，理性投喂\n▶ 投喂时请选择安全区域，使用无盐无调料的干净食物。\n▶ 投喂后及时清理残留物，避免污染环境（可联系物业领取专用喂食盒）。\n\n领养代替购买\n▶ 若您有条件并愿意给予流浪动物长期庇护，请联系「XX动物救助站」（地址：XXX；电话：XXX），为它们寻找温暖的家。\n\n责任养宠，拒绝遗弃\n▶ 家养宠物请办理登记、定期免疫，外出牵绳，避免走失。\n▶ 任何理由的宠物遗弃行为均违背公序良俗，社区将配合相关部门追责。\n\n生命无价，善意有光\n您的每一份微小善举，都能让流浪动物多一份生存希望。让我们携手传递温暖，共建人宠和谐的美好家园！\n\n咨询/救助通道\n社区物业服务中心：XXX-XXXXXXX\nXX动物保护协会：XXX-XXXXXXX\n\nXX社区居委会 / XX物业管理处\n2023年X月X日',1,'2025-03-22 18:49:53','2025-03-22 18:49:53');
/*!40000 ALTER TABLE `announcement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carousel`
--

DROP TABLE IF EXISTS `carousel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carousel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '标题',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='轮播图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carousel`
--

LOCK TABLES `carousel` WRITE;
/*!40000 ALTER TABLE `carousel` DISABLE KEYS */;
INSERT INTO `carousel` VALUES (2,'猫狗1','/carousel/7bdc4166620541469112c2025e0612a4.jpg',1,1,'2025-03-20 18:42:32','2025-03-20 18:42:32'),(3,'猫狗2','/carousel/675e1ae3ea5747938bc79a05676ae059.jpg',0,0,'2025-03-20 18:44:47','2025-03-20 18:44:47'),(4,'流浪狗1','/carousel/10fc736a8c994ff195fbadcde2550145.jpg',0,1,'2025-03-20 20:42:22','2025-03-20 20:42:22'),(5,'流浪猫1','/carousel/8ba92e6387234f57b956814d68ea2e9e.jpg',1,1,'2025-03-20 20:43:28','2025-03-20 20:43:28'),(6,'猫狗3','/carousel/c2f8a93182b44cf083d04c720f61db60.jpg',1,0,'2025-03-22 15:22:29','2025-03-22 15:22:29');
/*!40000 ALTER TABLE `carousel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_comment`
--

DROP TABLE IF EXISTS `forum_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `topic_id` bigint NOT NULL COMMENT '话题ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_topic` (`topic_id`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE,
  KEY `idx_parent` (`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='论坛评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_comment`
--

LOCK TABLES `forum_comment` WRITE;
/*!40000 ALTER TABLE `forum_comment` DISABLE KEYS */;
INSERT INTO `forum_comment` VALUES (5,1,'感谢大家的评论！',2,NULL,1,'2025-03-23 14:23:49','2025-03-23 14:23:49'),(6,1,'真的很感谢!',2,5,0,'2025-03-23 14:24:02','2025-03-23 14:24:02'),(7,1,'不客气，我们恰巧知道的多!',5,5,1,'2025-03-23 14:24:34','2025-03-23 14:24:34'),(8,1,'感谢',2,5,1,'2025-03-23 14:26:49','2025-03-23 14:26:49'),(9,1,'哈哈哈哈\n',2,5,1,'2025-03-23 14:26:57','2025-03-23 14:26:57'),(10,1,'嫩恩',5,5,0,'2025-03-23 14:27:15','2025-03-23 14:27:15'),(11,1,'嗯嗯',5,5,1,'2025-03-23 14:27:21','2025-03-23 14:27:21'),(12,1,'爱护它',5,NULL,1,'2025-03-23 14:27:32','2025-03-23 14:27:32'),(15,2,'1',2,NULL,1,'2025-11-18 21:30:06','2025-11-18 21:30:06'),(16,2,'3',2,NULL,1,'2025-11-18 21:30:08','2025-11-18 21:30:08');
/*!40000 ALTER TABLE `forum_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_topic`
--

DROP TABLE IF EXISTS `forum_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_topic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '话题ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '图片URL',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览数',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '评论数',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='论坛话题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_topic`
--

LOCK TABLES `forum_topic` WRITE;
/*!40000 ALTER TABLE `forum_topic` DISABLE KEYS */;
INSERT INTO `forum_topic` VALUES (1,'怎么护养我家的拉布拉多','最近养了一个拉布拉多，不知道怎么护养','/uploads//forum/topic/9043e80148444b99b45167a714f250ca.jpg',2,58,6,0,1,'2025-03-23 13:18:11','2025-03-23 14:23:36'),(2,'急急急！！如何养小仓鼠！','最近想养仓鼠了有没有人可以教教我！','',5,10,2,0,1,'2025-03-23 15:31:20','2025-03-23 15:31:20'),(3,'新手养猫需要准备什么？','准备领养一只小猫，想请教大家需要提前准备哪些用品和注意事项。','',5,4,2,0,0,'2025-03-23 15:31:47','2025-03-23 15:31:47');
/*!40000 ALTER TABLE `forum_topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `knowledge_article`
--

DROP TABLE IF EXISTS `knowledge_article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `knowledge_article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '封面图片',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '视频URL',
  `video_cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '视频封面URL',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_category` (`category_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='知识文章表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `knowledge_article`
--

LOCK TABLES `knowledge_article` WRITE;
/*!40000 ALTER TABLE `knowledge_article` DISABLE KEYS */;
INSERT INTO `knowledge_article` VALUES (2,'如何养好拉布拉多——关注用品选择与宠物保健（拉布拉多宠物用品的选购与养护知识详解）','拉布拉多作为一种非常聪明、活泼、稳定和可爱的犬种，越来越受到人们的喜爱。养好一只拉布拉多需要选择好合适的用品和正确的养护方法。本文将为您提供养护拉布拉多的全面指南，包括用品选择、日常保健、食品搭配等方面的详细介绍，帮助您成为一位优秀的宠物父母。\n\n如何养好拉布拉多——关注用品选择与宠物保健（拉布拉多宠物用品的选购与养护知识详解）\n\n一：选购狗粮需谨慎\n\n无论是幼犬还是成犬，给拉布拉多选择适合的狗粮非常重要。狗粮质量直接影响到犬只的健康和成长发育。要选择高质量、均衡营养、适口性好的狗粮，尽量不要选择添加剂和人工调味剂的狗粮。\n\n二：饮用水的质量也要注意\n\n水也是狗狗健康的重要保障，因此喝用水的质量也很重要。为了确保狗狗喝到新鲜、干净的水，我们应该定期更换水壶，并在喝水前检查水质是否清洁。\n\n三：购买舒适的犬床\n\n犬床是拉布拉多睡觉和休息的地方，我们应该选择一款优质、舒适的犬床，并注意保持其清洁卫生。同时，不要让狗狗在地板上睡觉，这样会影响它们的身体健康。\n\n四：保护狗狗的足部\n\n为拉布拉多选择舒适、合适的鞋子非常重要，可以有效地保护它们的足部。在选择鞋子时，要根据狗狗的体形和脚型来选择适当的鞋子，还要注意鞋子的质量和舒适度。\n\n五：定期洗澡\n\n拉布拉多是一种长毛犬，需要定期洗澡来保持其清洁卫生。在洗澡前，我们应该先给拉布拉多刷毛，帮助它们去除杂物和死皮。同时，在选择洗浴用品时，要选择适合狗狗皮肤的产品，不要使用过于刺激性的化学品。\n\n如何养好拉布拉多——关注用品选择与宠物保健（拉布拉多宠物用品的选购与养护知识详解）\n\n六：做好牙齿护理\n\n拉布拉多的口腔健康也非常重要。我们应该定期为狗狗刷牙，并提供一些有助于口腔健康的零食和骨头。同时，我们还应该注意观察狗狗的口腔健康情况，及时发现并处理口腔问题。\n\n七：保持拉布拉多的体重\n\n过胖会影响拉布拉多的身体健康，因此我们应该注意让它们保持适当的体重。可以通过控制饮食和定期锻炼来控制狗狗的体重。\n\n八：定期检查拉布拉多的耳朵\n\n拉布拉多的耳朵是容易感染和生虫的部位。我们应该定期检查它们的耳朵，并使用合适的药物来预防和治疗耳部问题。\n\n九：定期做心理训练\n\n拉布拉多是一种非常聪明和活泼的犬种，因此我们应该定期进行心理训练，帮助它们保持积极乐观的心态。可以通过玩耍、交流等方式来加强与狗狗的交流，让它们感到被爱和关心。\n\n十：给狗狗补充营养品\n\n拉布拉多的身体健康也需要有足够的营养支持，因此我们可以适当给狗狗补充一些维生素和氨基酸等营养品，帮助它们健康成长。\n\n十一：合理搭配食品\n\n为拉布拉多合理搭配食品非常重要，应该根据狗狗的年龄、体重和健康情况来制定适合的饮食计划。可以选择肉类、蔬菜等多种食物来搭配食用。\n\n十二：定期检查拉布拉多的眼睛\n\n拉布拉多的眼睛也是容易受到感染和损伤的部位。我们应该定期检查它们的眼睛，并及时发现并处理眼部问题。\n\n十三：保持室内清洁\n\n为了确保拉布拉多的健康，我们应该保持室内的清洁卫生，定期打扫家庭环境、清洁垃圾箱等。这样可以减少狗狗的疾病传播和室内臭味。\n\n十四：带狗狗去宠物医院做体检\n\n定期带拉布拉多去宠物医院做一次全面的体检非常重要，可以及时发现和处理潜在的健康问题，保证狗狗的身体健康。\n\n十五：\n\n养护拉布拉多需要选择好合适的用品和正确的养护方法。在选择狗粮、饮水、犬床等用品时，应该选择高质量、舒适、适合的产品。同时，还要注意定期为狗狗做洗澡、口腔护理、心理训练等方面的养护。希望这份指南能够帮助您成为一位优秀的宠物父母，养好一只可爱的拉布拉多。','/uploads//knowledge/images/436c3d0d23e24084a2f5ca2ee3bc400d.jpg','/uploads//knowledge/videos/8b2cf0d6f71e459e85afecf5ae6c2638.mp4','/uploads//knowledge/covers/0dfe75b2dd9a44b7aba55bf028ac2173.jpg',1,3,1,'2025-03-22 18:06:59','2025-03-23 11:12:03'),(3,'100条新手养猫常识，养猫必看指南（持续更新）','自我介绍一下哈：我叫小七，8年养猫老铲屎官，知道的多了一点，大家就都叫我“猫叫兽”。\n\n平时很多朋友问我一些关于猫咪常见问题，我也整理一下，纯干货分享，新手猫友可以好好看看哈，有其他问题也可以评论区提问，我会给大家一一解答~!!\n\n1、吃什么猫粮容易胖？\n我跟大家说一下：高蛋白、高脂肪含量的猫粮容易胖，买猫粮的时候可以好好看看成分含量表。\n\n2、三个月左右猫可以吃猫粮吗？\n可以吃，不过要泡软了，慢慢过渡成干粮，如果只有满月左右，不建议吃，如果一定要吃的话泡点羊奶，少喂一点，百丽幼猫还是可以的！\n\n3、我想养猫，养公猫还是母猫有区别吗？\n外观：0-6个月没有明显差异（对比人类）成年之后同品种公猫比母猫大一些，母猫多少会比公猫清秀一点，脸会更加清秀，喜欢大圆饼脸，就选公猫，5个月左右公猫蛋蛋会出来，毛茸茸的还挺可爱！\n\n干饭：干饭上公猫比母猫更能干饭！！\n\n发情：公猫绝育之前发情的时候，一般会对墙呲尿，母猫就是从早上、中午、晚上不停的叫，她会求你去摸她，你一摸它，她就厥屁股。\n\n绝育费用：公猫绝育比较简单，一个小手术，蛋蛋拿掉，500左右，母猫需要切开腹部，把卵巢子宫都摘掉，恢复时间也长，费用1000起。\n\n响应上：你要是喊公猫，它会屁跌屁跌过来跟你玩耍，如果要是母猫，可能只是动一动耳朵（这个可能就是高冷吧！！！）\n\n性格差异：公猫像养不大的崽子，对于世界时刻有好奇，一直很活泼，破坏力时刻存在的，想要活泼的，就要公猫。\n\n母猫更加成熟稳重，成年之后对于周围的好奇心会明显减弱，很多时候比较文静。\n\n所以你知道要养公猫还是母猫了吗？\n\n4、我的两个月左右的小猫到家，一定要关在笼子里吗？\n人到了新环境容易水土不服 猫也是。关笼子是为了缩小活动范围 让它有个适应过程。避免应激，过渡一段就好了！\n\n猫车是一辆载有猫咪平日里必备的猫粮、猫砂、猫药品各种优惠和福利的豪车，在这里可以更快了解到最新优惠信息，让各位铲屎官们能花最少的钱买到最性价比高的产品。（大额优惠红包有限，具体以实际金额为准，并非100%概率获得）\n\n5、折耳猫好养吗？\n折耳猫，最好不要养。我家猫4个月体重3.4斤，我见过4个月2.2斤的，太难伺候了！\n\n6、有没有必要给猫猫买一个大笼子吗？\n不用。猫咪习惯了，家里的环境几乎不搞破坏的。把易碎品收好就行，不建议笼养猫咪。\n\n7、猫猫一定要绝育吗？舍不得动手术！\n城市里养猫，大家都觉得是绝育好。我的猫绝育一年多了，感觉还ok\n\n8、刚接小幼猫回家，需要准备什么猫粮？\n小猫可以吃皇家奶糕。吃罐头。幼猫我喂过百利。有幼猫粮，颗粒小一点。5个月左右吃成猫粮。\n\n9、猫爬架值得买吗？它们会玩吗？\n可以买，猫猫大部分比较喜欢猫爬架\n\n10、你好，刚买的猫，去宠物医院做一个体检，这样可以确保猫猫健康了吗？还需要别的吗？\n只要没有传染病 就没有太大问题。不用担心。\n\n11、两个月左右的幼猫，应该注意什么呀？\n没有传染病。要注意喂食的量和肠胃。。不要急。多吃罐头这种软的食物，猫粮要买幼猫小颗的，泡软。小猫要养好肠胃。\n\n别的行业我不知道，猫粮、猫砂这一行在活动期间是真的放血，各位铲屎官们一定不能错过这种机会啊。猫车群内每天都会有车！猫粮、猫砂、罐头、生活用品相关等的豪车和福利','/uploads//knowledge/images/41568879e7734f0d9c6b179043198ce0.jpg','/uploads//knowledge/videos/55c6b2d35a4a4a68befa5d2706b8bcdd.mp4','',1,11,1,'2025-03-22 18:31:14','2025-11-18 20:21:06');
/*!40000 ALTER TABLE `knowledge_article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `knowledge_category`
--

DROP TABLE IF EXISTS `knowledge_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `knowledge_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='知识分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `knowledge_category`
--

LOCK TABLES `knowledge_category` WRITE;
/*!40000 ALTER TABLE `knowledge_category` DISABLE KEYS */;
INSERT INTO `knowledge_category` VALUES (1,'护养知识','关爱爱护保护小动物的小tips','2025-03-22 15:16:30','2025-03-22 15:17:06');
/*!40000 ALTER TABLE `knowledge_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recommendation_log`
--

DROP TABLE IF EXISTS `recommendation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recommendation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `animal_id` bigint NOT NULL COMMENT '推荐的动物ID',
  `recommendation_score` double DEFAULT '0' COMMENT '推荐得分',
  `recommendation_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推荐原因',
  `algorithm_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '算法类型：CF/CB/HOT/HYBRID',
  `is_clicked` tinyint DEFAULT '0' COMMENT '是否点击：0-否 1-是',
  `is_adopted` tinyint DEFAULT '0' COMMENT '是否最终领养：0-否 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_animal` (`animal_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recommendation_log`
--

LOCK TABLES `recommendation_log` WRITE;
/*!40000 ALTER TABLE `recommendation_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `recommendation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rescue_info`
--

DROP TABLE IF EXISTS `rescue_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rescue_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '救助ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '位置',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '图片URL',
  `user_id` bigint DEFAULT NULL COMMENT '发布用户ID',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0-待救助 1-救助中 2-已解决',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='救助信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rescue_info`
--

LOCK TABLES `rescue_info` WRITE;
/*!40000 ALTER TABLE `rescue_info` DISABLE KEYS */;
INSERT INTO `rescue_info` VALUES (1,'有流浪狗在街边趴着不起，四肢无力看起来','有流浪狗在街边趴着不起，四肢无力看起来','示例市示例区爱心路12号','yyy','13600000000','/uploads//rescue/c75cb67f0364418b9e1acbe1315388b7.jpg',1,2,'2025-03-22 03:15:05','2025-03-23 12:12:58'),(2,'发现一只状态好像不是很好的流浪猫','发现一只状态好像不是很好的流浪猫，感觉这只猫的目前状态不是很好','示例市示例区幸福街45号','user1','13800000001','/uploads//rescue/1f3b6108fcd945e8a811b7ddb570a40b.jpeg',2,1,'2025-03-23 12:39:10','2025-03-23 12:39:10');
/*!40000 ALTER TABLE `rescue_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '地址',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT '角色 0-普通用户 1-管理员',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','123456','管理员',NULL,NULL,NULL,NULL,1,1,'2025-03-19 19:37:51','2025-03-23 15:47:52'),(2,'user1','123456','yyy','13800000000','user1@example.com','Tamil Nadu Bellary MG Rd Bangalore 5755','/avatar/default.png',0,1,'2025-03-20 14:48:37','2025-11-18 21:09:34'),(5,'user2','123456','Daniel','13900000000','Daniel@example.com','示例市示例区阳光小区1栋','/avatar/ba06ae68343445d1bb189050204b411b.jpg',0,1,'2025-03-20 15:00:33','2025-03-20 15:00:33');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_animal_interaction`
--

DROP TABLE IF EXISTS `user_animal_interaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_animal_interaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `animal_id` bigint NOT NULL COMMENT '动物ID',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `total_view_duration` bigint DEFAULT '0' COMMENT '总浏览时长（秒）',
  `avg_view_duration` double DEFAULT '0' COMMENT '平均浏览时长（秒）',
  `is_liked` tinyint DEFAULT '0' COMMENT '是否点赞：0-否 1-是',
  `is_favorited` tinyint DEFAULT '0' COMMENT '是否收藏：0-否 1-是',
  `is_commented` tinyint DEFAULT '0' COMMENT '是否评论：0-否 1-是',
  `has_applied` tinyint DEFAULT '0' COMMENT '是否申请领养：0-否 1-是',
  `interaction_score` double DEFAULT '0' COMMENT '交互得分',
  `last_interaction_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后交互时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_animal` (`user_id`,`animal_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_animal` (`animal_id`),
  KEY `idx_interaction_time` (`last_interaction_time`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户动物交互详情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_animal_interaction`
--

LOCK TABLES `user_animal_interaction` WRITE;
/*!40000 ALTER TABLE `user_animal_interaction` DISABLE KEYS */;
INSERT INTO `user_animal_interaction` VALUES (1,2,4,1,0,0,1,0,0,0,1,'2025-11-18 20:12:38','2025-11-18 22:53:11','2025-11-18 22:53:11'),(2,2,5,2,0,0,0,0,0,0,0.6,'2025-11-18 21:07:47','2025-11-18 22:53:11','2025-11-18 22:53:11'),(3,2,6,1,0,0,1,1,0,0,2,'2025-11-18 20:15:44','2025-11-18 22:53:11','2025-11-18 22:53:11'),(4,2,9,24,0,0,1,0,0,0,7.9,'2025-11-18 22:30:01','2025-11-18 22:53:11','2025-11-18 22:53:11'),(5,2,10,1,0,0,0,0,0,0,0.3,'2025-11-18 20:12:38','2025-11-18 22:53:11','2025-11-18 22:53:11'),(6,2,11,1,0,0,1,0,0,0,1,'2025-11-18 20:15:48','2025-11-18 22:53:11','2025-11-18 22:53:11'),(7,2,14,4,0,0,0,0,0,0,1.2,'2025-11-18 21:07:38','2025-11-18 22:53:11','2025-11-18 22:53:11'),(8,5,5,1,0,0,1,0,0,0,1,'2025-11-18 20:12:38','2025-11-18 22:53:11','2025-11-18 22:53:11'),(9,5,6,1,0,0,0,0,0,0,0.3,'2025-11-18 20:12:38','2025-11-18 22:53:11','2025-11-18 22:53:11'),(10,5,8,1,0,0,0,1,0,0,1.3,'2025-11-18 20:12:38','2025-11-18 22:53:11','2025-11-18 22:53:11'),(11,5,10,1,0,0,0,0,0,0,0.3,'2025-11-18 20:12:38','2025-11-18 22:53:11','2025-11-18 22:53:11'),(12,5,12,1,0,0,0,0,0,0,0.3,'2025-11-18 20:12:38','2025-11-18 22:53:11','2025-11-18 22:53:11'),(13,5,13,1,0,0,0,0,0,0,0.3,'2025-11-18 20:12:38','2025-11-18 22:53:11','2025-11-18 22:53:11');
/*!40000 ALTER TABLE `user_animal_interaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_behavior`
--

DROP TABLE IF EXISTS `user_behavior`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_behavior` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '行为ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `animal_id` bigint NOT NULL COMMENT '动物ID',
  `behavior_type` tinyint NOT NULL COMMENT '行为类型 1-浏览 2-点赞 3-收藏',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_animal` (`user_id`,`animal_id`) USING BTREE,
  KEY `idx_behavior_type` (`behavior_type`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户行为记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_behavior`
--

LOCK TABLES `user_behavior` WRITE;
/*!40000 ALTER TABLE `user_behavior` DISABLE KEYS */;
INSERT INTO `user_behavior` VALUES (1,2,4,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(2,2,4,2,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(3,2,5,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(4,2,6,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(5,2,6,3,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(6,2,9,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(7,2,9,2,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(8,2,10,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(9,2,11,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(10,5,5,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(11,5,5,2,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(12,5,6,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(13,5,8,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(14,5,8,3,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(15,5,10,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(16,5,12,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(17,5,13,1,'2025-11-18 20:12:38','2025-11-18 20:12:38'),(18,2,6,2,'2025-11-18 20:15:42','2025-11-18 20:15:42'),(19,2,6,3,'2025-11-18 20:15:44','2025-11-18 20:15:44'),(20,2,11,2,'2025-11-18 20:15:48','2025-11-18 20:15:48'),(21,2,14,1,'2025-11-18 21:04:53','2025-11-18 21:04:53'),(22,2,14,1,'2025-11-18 21:05:03','2025-11-18 21:05:03'),(23,2,14,1,'2025-11-18 21:05:14','2025-11-18 21:05:14'),(24,2,14,1,'2025-11-18 21:07:38','2025-11-18 21:07:38'),(25,2,5,1,'2025-11-18 21:07:47','2025-11-18 21:07:47'),(26,2,9,1,'2025-11-18 21:08:02','2025-11-18 21:08:02'),(27,2,9,1,'2025-11-18 21:08:41','2025-11-18 21:08:41'),(28,2,9,1,'2025-11-18 21:08:58','2025-11-18 21:08:58'),(29,2,9,1,'2025-11-18 21:09:31','2025-11-18 21:09:31'),(30,2,9,1,'2025-11-18 21:09:37','2025-11-18 21:09:37'),(31,2,9,1,'2025-11-18 21:12:36','2025-11-18 21:12:36'),(32,2,9,1,'2025-11-18 21:14:54','2025-11-18 21:14:54'),(33,2,9,1,'2025-11-18 21:26:53','2025-11-18 21:26:53'),(34,2,9,1,'2025-11-18 21:30:29','2025-11-18 21:30:29'),(35,2,9,1,'2025-11-18 21:31:45','2025-11-18 21:31:45'),(36,2,9,1,'2025-11-18 21:31:49','2025-11-18 21:31:49'),(37,2,9,1,'2025-11-18 21:32:00','2025-11-18 21:32:00'),(38,2,9,1,'2025-11-18 21:32:04','2025-11-18 21:32:04'),(39,2,9,1,'2025-11-18 21:32:19','2025-11-18 21:32:19'),(40,2,9,1,'2025-11-18 21:32:28','2025-11-18 21:32:28'),(41,2,9,1,'2025-11-18 21:32:53','2025-11-18 21:32:53'),(42,2,9,1,'2025-11-18 21:33:43','2025-11-18 21:33:43'),(43,2,9,1,'2025-11-18 21:34:02','2025-11-18 21:34:02'),(44,2,9,1,'2025-11-18 21:35:52','2025-11-18 21:35:52'),(45,2,9,1,'2025-11-18 21:36:02','2025-11-18 21:36:02'),(46,2,9,1,'2025-11-18 21:37:52','2025-11-18 21:37:52'),(47,2,9,1,'2025-11-18 21:40:54','2025-11-18 21:40:54'),(48,2,9,1,'2025-11-18 22:30:01','2025-11-18 22:30:01'),(49,2,4,1,'2025-11-18 23:35:41','2025-11-18 23:35:41'),(50,2,4,3,'2025-11-18 23:35:43','2025-11-18 23:35:43'),(51,2,4,1,'2025-11-18 23:43:29','2025-11-18 23:43:29'),(52,2,14,1,'2025-11-18 23:47:19','2025-11-18 23:47:19'),(53,2,5,1,'2025-11-18 23:50:17','2025-11-18 23:50:17');
/*!40000 ALTER TABLE `user_behavior` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_dynamic_preference`
--

DROP TABLE IF EXISTS `user_dynamic_preference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_dynamic_preference` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `category_views` text COLLATE utf8mb4_unicode_ci COMMENT '类别浏览统计（JSON）{"猫":15,"狗":5}',
  `breed_views` text COLLATE utf8mb4_unicode_ci COMMENT '品种浏览统计（JSON）{"英短":8,"橘猫":7}',
  `size_views` text COLLATE utf8mb4_unicode_ci COMMENT '体型浏览统计（JSON）{"小型":10,"中型":5}',
  `personality_views` text COLLATE utf8mb4_unicode_ci COMMENT '性格浏览统计（JSON）{"温顺":12,"活泼":8}',
  `last_update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户动态偏好表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_dynamic_preference`
--

LOCK TABLES `user_dynamic_preference` WRITE;
/*!40000 ALTER TABLE `user_dynamic_preference` DISABLE KEYS */;
INSERT INTO `user_dynamic_preference` VALUES (1,2,'{\"狗\":32,\"猫\":8}','{}','{}',NULL,'2025-11-18 22:53:11'),(2,5,'{\"狗\":3,\"猫\":5}','{}','{}',NULL,'2025-11-18 22:53:11');
/*!40000 ALTER TABLE `user_dynamic_preference` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_profile`
--

DROP TABLE IF EXISTS `user_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `preferred_categories` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '偏好类别（JSON数组）["猫","狗"]',
  `preferred_size` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '偏好体型：小型/中型/大型',
  `preferred_age_range` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '偏好年龄段：幼年/成年/老年',
  `preferred_gender` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '偏好性别：雄性/雌性/无偏好',
  `living_environment` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '居住环境：公寓/别墅/农村',
  `experience` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '养宠经验：新手/有经验/专业',
  `preferred_location` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '偏好地点',
  `active_hours` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活跃时间段（JSON数组）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户画像表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profile`
--

LOCK TABLES `user_profile` WRITE;
/*!40000 ALTER TABLE `user_profile` DISABLE KEYS */;
INSERT INTO `user_profile` VALUES (1,1,'[\"猫\",\"狗\"]','中型','成年','无偏好','公寓','有经验','北京市朝阳区',NULL,'2025-11-18 22:53:11','2025-11-18 22:53:11'),(2,2,'[\"猫\",\"狗\"]','大型','老年','无偏好','别墅','专业','北京市朝阳区',NULL,'2025-11-18 22:53:11','2025-11-18 23:59:39'),(3,5,'[\"猫\",\"狗\"]','中型','成年','无偏好','公寓','有经验','北京市朝阳区',NULL,'2025-11-18 22:53:11','2025-11-18 22:53:11');
/*!40000 ALTER TABLE `user_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_animal_features`
--

DROP TABLE IF EXISTS `v_animal_features`;
/*!50001 DROP VIEW IF EXISTS `v_animal_features`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_animal_features` AS SELECT 
 1 AS `animal_id`,
 1 AS `name`,
 1 AS `category_id`,
 1 AS `category_name`,
 1 AS `breed`,
 1 AS `gender`,
 1 AS `age`,
 1 AS `weight`,
 1 AS `health_status`,
 1 AS `status`,
 1 AS `rescue_location`,
 1 AS `size`,
 1 AS `personality`,
 1 AS `adoption_fee`,
 1 AS `special_needs`,
 1 AS `days_in_rescue`,
 1 AS `popularity_score`,
 1 AS `energy_level`,
 1 AS `friendliness`,
 1 AS `trainability`,
 1 AS `view_count`,
 1 AS `like_count`,
 1 AS `favorite_count`,
 1 AS `comment_count`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_user_features`
--

DROP TABLE IF EXISTS `v_user_features`;
/*!50001 DROP VIEW IF EXISTS `v_user_features`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_user_features` AS SELECT 
 1 AS `user_id`,
 1 AS `username`,
 1 AS `preferred_categories`,
 1 AS `preferred_size`,
 1 AS `preferred_age_range`,
 1 AS `preferred_gender`,
 1 AS `living_environment`,
 1 AS `experience`,
 1 AS `preferred_location`,
 1 AS `category_views`,
 1 AS `breed_views`,
 1 AS `size_views`,
 1 AS `interacted_animals_count`,
 1 AS `total_interaction_score`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `video`
--

DROP TABLE IF EXISTS `video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '视频ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '描述',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频URL',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '封面URL',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '播放数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_category` (`category_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='视频表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video`
--

LOCK TABLES `video` WRITE;
/*!40000 ALTER TABLE `video` DISABLE KEYS */;
INSERT INTO `video` VALUES (1,'流浪狗','笼子里和路边的流浪狗','/uploads//video/9581ce43ab8f4155a339962e4815f5ad.mp4','/uploads//cover/c3315eea8bc44f589bf73b125db262e5.jpg',1,0,1,'2025-03-21 08:27:56','2025-03-21 08:27:56'),(2,'流浪猫','流浪猫','/uploads//video/2087ecd33832443ba8cdb1197dbc4abd.mp4','/uploads//cover/8ab129004ea348918f0659924356fc6f.jpg',2,0,1,'2025-03-21 11:04:56','2025-03-21 11:10:49');
/*!40000 ALTER TABLE `video` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_category`
--

DROP TABLE IF EXISTS `video_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='视频分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_category`
--

LOCK TABLES `video_category` WRITE;
/*!40000 ALTER TABLE `video_category` DISABLE KEYS */;
INSERT INTO `video_category` VALUES (1,'流浪狗','流浪动物流浪狗','2025-03-21 08:27:06','2025-03-21 08:27:20'),(2,'流浪猫','流浪动物流浪猫','2025-03-21 11:10:21','2025-03-21 11:10:34');
/*!40000 ALTER TABLE `video_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `v_animal_features`
--

/*!50001 DROP VIEW IF EXISTS `v_animal_features`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_animal_features` AS select `a`.`id` AS `animal_id`,`a`.`name` AS `name`,`a`.`category_id` AS `category_id`,`ac`.`name` AS `category_name`,`a`.`breed` AS `breed`,`a`.`gender` AS `gender`,`a`.`age` AS `age`,`a`.`weight` AS `weight`,`a`.`health_status` AS `health_status`,`a`.`status` AS `status`,`a`.`rescue_location` AS `rescue_location`,`ae`.`size` AS `size`,`ae`.`personality` AS `personality`,`ae`.`adoption_fee` AS `adoption_fee`,`ae`.`special_needs` AS `special_needs`,`ae`.`days_in_rescue` AS `days_in_rescue`,`ae`.`popularity_score` AS `popularity_score`,`ae`.`energy_level` AS `energy_level`,`ae`.`friendliness` AS `friendliness`,`ae`.`trainability` AS `trainability`,`a`.`view_count` AS `view_count`,`a`.`like_count` AS `like_count`,`a`.`favorite_count` AS `favorite_count`,`a`.`comment_count` AS `comment_count` from ((`animal` `a` left join `animal_category` `ac` on((`a`.`category_id` = `ac`.`id`))) left join `animal_extended` `ae` on((`a`.`id` = `ae`.`animal_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_user_features`
--

/*!50001 DROP VIEW IF EXISTS `v_user_features`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_user_features` AS select `u`.`id` AS `user_id`,`u`.`username` AS `username`,`up`.`preferred_categories` AS `preferred_categories`,`up`.`preferred_size` AS `preferred_size`,`up`.`preferred_age_range` AS `preferred_age_range`,`up`.`preferred_gender` AS `preferred_gender`,`up`.`living_environment` AS `living_environment`,`up`.`experience` AS `experience`,`up`.`preferred_location` AS `preferred_location`,`udp`.`category_views` AS `category_views`,`udp`.`breed_views` AS `breed_views`,`udp`.`size_views` AS `size_views`,count(distinct `uai`.`animal_id`) AS `interacted_animals_count`,sum(`uai`.`interaction_score`) AS `total_interaction_score` from (((`user` `u` left join `user_profile` `up` on((`u`.`id` = `up`.`user_id`))) left join `user_dynamic_preference` `udp` on((`u`.`id` = `udp`.`user_id`))) left join `user_animal_interaction` `uai` on((`u`.`id` = `uai`.`user_id`))) group by `u`.`id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-19  0:10:29

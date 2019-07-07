CREATE DATABASE `jenkins` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';

CREATE TABLE `git_repository` (
    `id`                  bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
    `path_with_namespace` varchar(100)        NOT NULL COMMENT '工程路径',
    `create_time`         datetime            NOT NULL COMMENT '创建时间，status=1',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `path_unique` (`path_with_namespace`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET = utf8mb4 COMMENT ='Gitlab项目源';

CREATE TABLE `jenkins_project` (
   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
   `git_repository_id` bigint(20) NOT NULL COMMENT '源项目ID',
   `job` varchar(255) NOT NULL COMMENT '项目名称',
   `create_time` datetime NOT NULL COMMENT '创建时间',
   PRIMARY KEY (`id`),
   UNIQUE KEY `job_unique` (`job`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Jenkins项目表';

CREATE TABLE `job_exec_queue` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
  `jenkins_project_id` bigint(20) NOT NULL COMMENT 'jenkins_project表ID',
  `build_number` int(5) DEFAULT NULL COMMENT 'jenkins中job的构建编号',
  `trigger_event` varchar(255) NOT NULL COMMENT '触发事件，webhook中请求头X-Gitlab-Event字段值',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '状态，1-初始化，2-执行中，3-执行完成，4-放弃执行，5-执行失败',
  `create_time` datetime NOT NULL COMMENT '创建时间，status=1',
  `start_exec_time` datetime DEFAULT NULL COMMENT '开始执行时间，status=2',
  `end_exec_time` datetime DEFAULT NULL COMMENT '执行结束时间，status=3、4、5',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `id_num_unique` (`jenkins_project_id`,`build_number`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='构建队列，每条记录是一个流程，创建->开始执行->结束';

CREATE TABLE `operation_log` (
     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增ID',
     `exec_status` tinyint(2) NOT NULL COMMENT '执行状态，对应job_exec_queue的status字段',
     `job_exec_queue_id` bigint(20) NOT NULL COMMENT '构建队列ID，对应job_exec_queue的id字段',
     `exec_content` varchar(255) DEFAULT NULL COMMENT '执行内容描述',
     `create_time` datetime NOT NULL COMMENT '创建时间',
     PRIMARY KEY (`id`) USING BTREE,
     UNIQUE KEY `id_type_unique` (`job_exec_queue_id`,`exec_status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';


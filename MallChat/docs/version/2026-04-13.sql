-- 创建服务器表
CREATE TABLE IF NOT EXISTS `server` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '服务器ID',
  `name` VARCHAR(255) NOT NULL COMMENT '服务器名称',
  `icon` VARCHAR(512) DEFAULT NULL COMMENT '服务器图标URL',
  `description` TEXT DEFAULT NULL COMMENT '服务器描述',
  `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务器表';

-- 创建权限表
CREATE TABLE IF NOT EXISTS `permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` VARCHAR(255) NOT NULL COMMENT '权限名称',
  `description` TEXT DEFAULT NULL COMMENT '权限描述',
  `code` VARCHAR(50) NOT NULL COMMENT '权限代码',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 创建角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `server_id` BIGINT NOT NULL COMMENT '服务器ID',
  `name` VARCHAR(255) NOT NULL COMMENT '角色名称',
  `color` VARCHAR(20) DEFAULT NULL COMMENT '角色颜色',
  `permissions` JSON NOT NULL COMMENT '权限列表（JSON格式）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_server_id` (`server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建服务器成员表
CREATE TABLE IF NOT EXISTS `server_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成员ID',
  `server_id` BIGINT NOT NULL COMMENT '服务器ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `nickname` VARCHAR(255) DEFAULT NULL COMMENT '服务器内昵称',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_server_user` (`server_id`, `user_id`),
  INDEX `idx_server_id` (`server_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务器成员表';

-- 创建频道表
CREATE TABLE IF NOT EXISTS `channel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '频道ID',
  `server_id` BIGINT NOT NULL COMMENT '服务器ID',
  `name` VARCHAR(255) NOT NULL COMMENT '频道名称',
  `type` VARCHAR(20) NOT NULL COMMENT '频道类型',
  `position` INT NOT NULL DEFAULT 0 COMMENT '频道排序位置',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_server_id` (`server_id`),
  INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='频道表';

-- 插入默认权限
INSERT INTO `permission` (`name`, `description`, `code`) VALUES
('创建频道', '创建频道权限', 'CREATE_CHANNEL'),
('删除频道', '删除频道权限', 'DELETE_CHANNEL'),
('管理成员', '管理成员权限', 'MANAGE_MEMBERS'),
('管理角色', '管理角色权限', 'MANAGE_ROLES'),
('发送消息', '发送消息权限', 'SEND_MESSAGES'),
('加入语音', '加入语音频道权限', 'JOIN_VOICE')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

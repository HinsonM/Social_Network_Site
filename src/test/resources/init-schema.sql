show tables;

DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NULL,
  `user_id` INT NOT NULL,
  `created_date` DATETIME NOT NULL,
  `comment_count` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `date_index` (`created_date` ASC));

  DROP TABLE IF EXISTS `user`;
  CREATE TABLE `user`(
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL DEFAULT '',
    `password` varchar(128) NOT NULL DEFAULT '',
    `salt` varchar(32) NOT NULL DEFAULT '',
    `head_url` varchar(256) NOT NULL DEFAULT '',
    `recent_login_time` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


  DROP TABLE IF EXISTS `login_ticket`;
  CREATE TABLE `login_ticket` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `ticket` VARCHAR(45) NOT NULL,
    `expired` DATETIME NOT NULL,
    `status` INT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `ticket_UNIQUE` (`ticket` ASC)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- 创建entity_index可以分类不同comment，或者快速搜索，例如场景： 我要找我发表的评论有多少评论，我要找我发表的帖子有多少评论，
  DROP TABLE IF EXISTS `comment`;
  CREATE TABLE `comment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `content` TEXT NOT NULL,
  `user_id` INT NOT NULL,
  `entity_id` INT NOT NULL,
  `entity_type` INT NOT NULL,
  `created_date` DATETIME NOT NULL,
  `status` INT NOT NULL DEFAULT 0, --删除与否
  PRIMARY KEY (`id`),
  INDEX `entity_index` (`entity_id` ASC, `entity_type` ASC)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--在created_date上创建名为created_date的index，在conversation_id上创建名为conversation_index的index
--因为这样可以通过日期以及回话快速找到该记录、
--要在conversation_index设立index，因为要建立一个会话页面，将所有人的私信集中起来
--为什么哟啊建立一个created_date的index呢？可以根据时间搜索聊天记录，筛选
  DROP TABLE IF EXISTS `message`;
  CREATE TABLE `message` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `from_id` INT NULL,
    `to_id` INT NULL,
    `content` TEXT NULL,
    `created_date` DATETIME NULL,
    `has_read` INT NULL,
    `conversation_id` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `conversation_index` (`conversation_id` ASC),
    INDEX `created_date` (`created_date` ASC))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


    DROP TABLE IF EXISTS `feed`;
    CREATE TABLE `feed` (
      `id` INT NOT NULL AUTO_INCREMENT,
      `created_date` DATETIME NULL,
      `user_id` INT NULL,
      `data` TINYTEXT NULL,
      `type` INT NULL,
      PRIMARY KEY (`id`),
      INDEX `user_index` (`user_id` ASC))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8;

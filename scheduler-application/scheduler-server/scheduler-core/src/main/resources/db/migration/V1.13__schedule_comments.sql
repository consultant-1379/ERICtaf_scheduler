CREATE TABLE IF NOT EXISTS `Comment` (
  `id`                 BIGINT(20)   NOT NULL     AUTO_INCREMENT,
  `scheduleId`         BIGINT(20)   NOT NULL,
  `message`            TEXT         NOT NULL,
  `created`            TIMESTAMP    NOT NULL,
  `updated`            TIMESTAMP    NULL,
  `createdBy`          VARCHAR(255) NOT NULL,
  `updatedBy`          VARCHAR(255) NULL,

  PRIMARY KEY (`id`),
  KEY `FK_SCHEDULE_COMMENT` (`scheduleId`),
  CONSTRAINT `FK_SCHEDULE_COMMENT` FOREIGN KEY (`scheduleId`) REFERENCES `Schedule` (`id`)
);
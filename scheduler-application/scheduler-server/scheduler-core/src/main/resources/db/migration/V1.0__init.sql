CREATE TABLE IF NOT EXISTS `Product` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(255) NOT NULL,
  `description` TEXT         NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `ProductDrop` (
  `id`        BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `name`      VARCHAR(255) NOT NULL,
  `productId` BIGINT(20)   NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_DROP_PRODUCT` (`productId`),
  CONSTRAINT `FK_DROP_PRODUCT` FOREIGN KEY (`productId`) REFERENCES `Product` (`id`)
);

CREATE TABLE IF NOT EXISTS `Iso` (
  `id`      BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `name`    VARCHAR(255) NOT NULL,
  `version` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ISO_NAME_VERSION` (`name`, `version`)
);

CREATE TABLE IF NOT EXISTS `IsoDrops` (
  `id`     BIGINT(20) NOT NULL AUTO_INCREMENT,
  `isoId`  BIGINT(20) NOT NULL,
  `dropId` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ISODROPS_ISO` (`isoId`),
  CONSTRAINT `FK_ISODROPS_ISO` FOREIGN KEY (`isoId`) REFERENCES `Iso` (`id`),
  KEY `FK_ISODROPS_DROPS` (`dropId`),
  CONSTRAINT `FK_ISODROPS_DROPS` FOREIGN KEY (`dropId`) REFERENCES `ProductDrop` (`id`),
  UNIQUE KEY `UK_ISOID_DROPID` (`dropId`, `isoId`)
);

CREATE TABLE IF NOT EXISTS `Testware` (
  `id`         BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `group`      VARCHAR(255) NOT NULL,
  `name`       VARCHAR(255) NOT NULL,
  `version`    VARCHAR(255) NOT NULL,
  `packaging`  VARCHAR(255) NOT NULL,
  `classifier` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Package` (
  `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `group`         VARCHAR(255) NOT NULL,
  `name`          VARCHAR(255) NOT NULL,
  `version`       VARCHAR(255) NOT NULL,
  `packaging`     VARCHAR(255) NOT NULL,
  `classifier`    VARCHAR(255) NOT NULL,
  `number`        VARCHAR(255) NOT NULL,
  `mediaCategory` VARCHAR(255) NULL,
  `mediaPath`     VARCHAR(255) NULL,
  `Platform`      VARCHAR(255) NULL,
  `url`           TEXT         NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `IsoPackages` (
  `id`        BIGINT(20) NOT NULL AUTO_INCREMENT,
  `isoId`     BIGINT(20) NOT NULL,
  `packageId` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ISOPACKAGES_ISO` (`isoId`),
  CONSTRAINT `FK_ISOPACKAGES_ISO` FOREIGN KEY (`isoId`) REFERENCES `Iso` (`id`),
  KEY `FK_ISOPACKAGES_PACKAGE` (`packageId`),
  CONSTRAINT `FK_ISOPACKAGES_PACKAGE` FOREIGN KEY (`packageId`) REFERENCES `Package` (`id`)
);

CREATE TABLE IF NOT EXISTS `Schedule` (
  `id`                 BIGINT(20)   NOT NULL     AUTO_INCREMENT,
  `isoId`              BIGINT(20)   NOT NULL,
  `originalScheduleId` BIGINT(20)   NULL,
  `isLastVersion`      BIT                       DEFAULT TRUE,
  `version`            INTEGER      NOT NULL,
  `title`              TEXT         NOT NULL,
  `xml`                TEXT         NOT NULL,
  `created`            TIMESTAMP    NOT NULL,
  `updated`            TIMESTAMP    NULL,
  `createdBy`          VARCHAR(255) NOT NULL,
  `updatedBy`          VARCHAR(255),

  PRIMARY KEY (`id`),
  KEY `FK_SCHEDULE_ISO` (`isoId`),
  CONSTRAINT `FK_SCHEDULE_ISO` FOREIGN KEY (`isoId`) REFERENCES `Iso` (`id`),
  KEY `FK_SCHEDULE_VERSIONS` (`originalScheduleId`),
  CONSTRAINT `FK_SCHEDULE_VERSIONS` FOREIGN KEY (`originalScheduleId`) REFERENCES `Schedule` (`id`),
  UNIQUE KEY `UK_ORIGINAL_SCHEDULE_VERSION` (`originalScheduleId`, `version`)
);

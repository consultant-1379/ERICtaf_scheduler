CREATE TABLE IF NOT EXISTS `TestwareIso` (
  `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `isoId`         BIGINT(20)   NOT NULL,
  `name`          VARCHAR(255) NOT NULL,
  `version`       VARCHAR(255) NOT NULL,
  `latestInDrop`  BIT DEFAULT FALSE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_TESTWARE_ISO_NAME_VERSION` (`name`, `version`),
  KEY `FK_TESTWAREISO_ISO` (`isoId`),
  CONSTRAINT `FK_TESTWAREISO_ISO` FOREIGN KEY (`isoId`) REFERENCES `Iso` (`id`)
);

DROP TABLE IsoTestwares;

CREATE TABLE IF NOT EXISTS `IsoTestwares` (
  `id`                BIGINT(20) NOT NULL AUTO_INCREMENT,
  `testwareIsoId`     BIGINT(20) NOT NULL,
  `testwareId`        BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ISO_TESTWARES_TESTWAREISO` (`testwareIsoId`),
  CONSTRAINT `FK_ISO_TESTWARES_TESTWAREISO` FOREIGN KEY (`testwareIsoId`) REFERENCES `TestwareIso` (`id`),
  KEY `FK_ISO_TESTWARES_TESTWARE` (`testwareId`),
  CONSTRAINT `FK_ISO_TESTWARES_TESTWARE` FOREIGN KEY (`testwareId`) REFERENCES `Testware` (`id`)
);
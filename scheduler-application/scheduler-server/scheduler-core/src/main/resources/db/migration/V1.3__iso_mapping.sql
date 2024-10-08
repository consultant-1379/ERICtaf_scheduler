CREATE TABLE IF NOT EXISTS `Mapping` (
  `id`           BIGINT(20) NOT NULL     AUTO_INCREMENT,
  `packageId`    BIGINT(20) NOT NULL,
  `testwareId`   BIGINT(20) NOT NULL,
  `isoMappingId` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_MAPPING_PACKAGE` (`packageId`),
  CONSTRAINT `FK_MAPPING_PACKAGE` FOREIGN KEY (`packageId`) REFERENCES `Package` (`id`),
  KEY `FK_MAPPING_TESTWARE` (`testwareId`),
  CONSTRAINT `FK_MAPPING_TESTWARE` FOREIGN KEY (`testwareId`) REFERENCES `Testware` (`id`)
);

CREATE TABLE IF NOT EXISTS `IsoMapping` (
  `id`        BIGINT(20)   NOT NULL     AUTO_INCREMENT,
  `isoId`     BIGINT(20)   NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_MAPPING_ISO` (`isoId`),
  CONSTRAINT `FK_MAPPING_ISO` FOREIGN KEY (`isoId`) REFERENCES `Iso` (`id`)
);

CREATE TABLE IF NOT EXISTS `IsoMappingChange` (
  `id`           BIGINT(20)   NOT NULL         AUTO_INCREMENT,
  `isoMappingId` BIGINT(20)   NOT NULL,
  `created`      TIMESTAMP    NOT NULL,
  `createdBy`    VARCHAR(255) NOT NULL,
  `action`       VARCHAR(100) NOT NULL,
  `packageId`    BIGINT(20)   NOT NULL,
  `testwareId`   BIGINT(20)   NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_MAPPING_CHANGE_ISO_MAPPING` (`isoMappingId`),
  CONSTRAINT `FK_MAPPING_CHANGE_ISO_MAPPING` FOREIGN KEY (`isoMappingId`) REFERENCES `IsoMapping` (`id`),
  KEY `FK_MAPPING_CHANGE_PACKAGE` (`packageId`),
  CONSTRAINT `FK_MAPPING_CHANGE_PACKAGE` FOREIGN KEY (`packageId`) REFERENCES `Package` (`id`),
  KEY `FK_MAPPING_CHANGE_TESTWARE` (`testwareId`),
  CONSTRAINT `FK_MAPPING_CHANGE_TESTWARE` FOREIGN KEY (`testwareId`) REFERENCES `Testware` (`id`)
);

CREATE TABLE IF NOT EXISTS `IsoTestwares` (
  `id`        BIGINT(20) NOT NULL AUTO_INCREMENT,
  `isoId`     BIGINT(20) NOT NULL,
  `testwareId` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ISO_TESTWARES_ISO` (`isoId`),
  CONSTRAINT `FK_ISO_TESTWARES_ISO` FOREIGN KEY (`isoId`) REFERENCES `Iso` (`id`),
  KEY `FK_ISO_TESTWARES_TESTWARE` (`testwareId`),
  CONSTRAINT `FK_ISO_TESTWARES_TESTWARE` FOREIGN KEY (`testwareId`) REFERENCES `Testware` (`id`)
);



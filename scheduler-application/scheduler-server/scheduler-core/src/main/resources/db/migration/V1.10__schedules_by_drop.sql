ALTER TABLE `Schedule` DROP FOREIGN KEY `FK_SCHEDULE_ISO`;
ALTER TABLE `Schedule` CHANGE `isoId` `dropId` BIGINT(20) NOT NULL;
ALTER TABLE `Schedule` ADD CONSTRAINT `FK_SCHEDULE_DROP` FOREIGN KEY (`dropId`) REFERENCES `ProductDrop` (`id`);
ALTER TABLE `Schedule` ADD COLUMN `enabled` BIT DEFAULT FALSE;
ALTER TABLE `Iso` ADD COLUMN `latestInDrop` BIT DEFAULT FALSE;

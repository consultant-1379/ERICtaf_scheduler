ALTER TABLE `Package` CHANGE `group` `groupId` VARCHAR(255) NOT NULL;
ALTER TABLE `Package` CHANGE `name` `artifactId` VARCHAR(255) NOT NULL;
ALTER TABLE `Package` CHANGE `number` `cxpNumber` VARCHAR(255) NULL;

ALTER TABLE `Package` MODIFY `packaging` VARCHAR(255) NULL;
ALTER TABLE `Package` MODIFY `classifier` VARCHAR(255) NULL;

ALTER TABLE `Testware` CHANGE `group` `groupId` VARCHAR(255) NOT NULL;
ALTER TABLE `Testware` CHANGE `name` `artifactId` VARCHAR(255) NOT NULL;
ALTER TABLE `Testware` ADD `cxpNumber` VARCHAR(255);

ALTER TABLE `Testware` MODIFY `classifier` VARCHAR(255) NULL;
ALTER TABLE `Testware` MODIFY `packaging` VARCHAR(255) NULL;

#Packages and Testware must be unique on GAV
ALTER TABLE `Package` ADD CONSTRAINT `UK_GROUP_ID_ARTIFACT_ID_VERSION` UNIQUE (`groupId`, `artifactId`, `version`);
ALTER TABLE `Testware` ADD CONSTRAINT `UK_GROUP_ID_ARTIFACT_ID_VERSION` UNIQUE (`groupId`, `artifactId`, `version`);

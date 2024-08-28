UPDATE `Schedule` SET `type`=3;
UPDATE `Schedule` SET `type`=2 WHERE `id`=4 OR `originalScheduleId`=4;
UPDATE `Schedule` SET `type`=8 WHERE `id`=10;
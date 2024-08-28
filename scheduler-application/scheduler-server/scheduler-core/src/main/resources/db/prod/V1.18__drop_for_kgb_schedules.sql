INSERT INTO `Product` (`id`, `name`, `description`) VALUES
  (3, 'KGB', 'KGB');

INSERT INTO `ProductDrop` (`name`, `productId`) VALUES
  ('KGB', (SELECT `id` FROM `Product` WHERE `name` = 'KGB' AND `description` = 'KGB'));

INSERT INTO `Iso` (`name`, `version`) VALUES
  ('KGB_ISO', '1.0');

INSERT INTO `TestwareIso` (`name`, `version`, `isoId`) VALUES
  ('KGB_TW_ISO', '1.0', (SELECT `id` FROM `Iso` WHERE `name` = 'KGB_ISO' AND `version` = '1.0'))

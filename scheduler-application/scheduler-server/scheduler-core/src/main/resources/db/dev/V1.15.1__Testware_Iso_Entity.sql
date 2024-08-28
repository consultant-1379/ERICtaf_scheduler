INSERT INTO `TestwareIso` (`id`, `name`, `version`, `latestInDrop`, `isoId`) VALUES
  (1, 'CXP9027746', '0.0.1', false, 1),
  (2, 'CXP9027746', '0.0.2', true, 1);

INSERT INTO `IsoTestwares` (`id`, `testwareIsoId`, `testwareId`) VALUES
  (1, 1, 1),
  (2, 1, 2),
  (3, 1, 3),
  (4, 2, 1),
  (5, 2, 2);
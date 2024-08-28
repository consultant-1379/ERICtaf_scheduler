INSERT INTO `Package` (`id`, `groupId`, `artifactId`, `version`, `cxpNumber`) VALUES
  (1, 'com.ericsson.taf', 'ERICtafpackage_123', '0.0.1', 'CXP123'),
  (2, 'com.ericsson.taf', 'ERICtafpackage_456', '3.1.1', 'CXP456'),
  (3, 'com.ericsson.taf', 'ERICtafpackage_789', '4.7.2', 'CXP456');

INSERT INTO Testware (`id`, `groupId`, `artifactId`, `version`, `cxpNumber`) VALUES
  (1, 'com.ericsson.ci.cloud.testware', 'ERICTAFeniq_cdb_setup_CXP9027959', '1.0.20', 'CXPValid1'),
  (2, 'com.ericsson.ci.simnet', 'ERICTAFenmnisimdep_CXP9031884', '1.1.7', 'CXPValid2'),
  (3, 'com.ericsson.cifwk.taf.testware', 'ERICTAFcifwk_CXP9030790', '1.1.100', 'CXPValid3');

INSERT INTO `IsoMapping` (`id`, `isoId`) VALUES
  (1, 1),
  (2, 2),
  (3, 3);

INSERT INTO `Mapping` (`id`, `packageId`, `testwareId`, `isoMappingId`) VALUES
  (1, 1, 1, 1),
  (2, 2, 2, 2),
  (3, 3, 3, 3);

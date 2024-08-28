INSERT INTO `Product` (`id`, `name`, `description`) VALUES
  (1, 'ENM', 'ENM productName'),
  (2, 'CI', 'CI productName'),
  (3, 'OSS', 'OSS description'),
  (4, 'KGB', 'KGB');

INSERT INTO `ProductDrop` (`id`, `name`, `productId`) VALUES
  (1, '1.0.enm.early', 1),
  (2, '1.1.ci', 2),
  (3, '1.0.oss', 3),
  (4, '2.X.enm', 1),
  (5, '15.14', 1),
  (6, 'KGB', 4);

INSERT INTO `Iso` (`id`, `name`, `version`) VALUES
  (1, 'CXP1234ISO', '1.0.1'),
  (2, 'CXP7812ISO', '2.0.2'),
  (3, 'CXP9999ISO', '3.0.3'),
  (4, 'CXP1111ISO', '0.0.1'),
  (5, 'CXP5555ISO', '5.5.5');

INSERT INTO `IsoDrops` (`id`, `isoId`, `dropId`) VALUES
  (1, 1, 1),
  (2, 1, 2),
  (3, 2, 3),
  (4, 3, 1),
  (5, 3, 2),
  (6, 3, 3),
  (7, 4, 3),
  (8, 5, 4);

INSERT INTO `Schedule` (`id`, `title`, `created`, `updated`, `createdBy`,
                        `isoId`, `originalScheduleId`, `isLastVersion`, `version`,
                        `xml`) VALUES
  (1, 'First Schedule', '2015-07-28 09:45', '2015-07-28 10:45', 'enikoal', 1, NULL, 0, 1,
   '<?xml version="1.0"?>
    <schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
        <item>
            <name>ERICTAFeniq_cdb_setup_CXP9027959 1</name>
            <component>com.ericsson.ci.cloud.testware:ERICTAFeniq_cdb_setup_CXP9027959</component>
            <suites>CDBInstall.xml</suites>
        </item>
    </schedule>
   '),
  (2, 'First Schedule V2', '2015-07-28 09:45', '2015-07-29 12:45', 'enikoal', 1, 1, 0, 2,
   '<?xml version="1.0"?>
    <schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
        <item>
            <name>ERICTAFeniq_cdb_setup_CXP9027959 2</name>
            <component>com.ericsson.ci.cloud.testware:ERICTAFeniq_cdb_setup_CXP9027959</component>
            <suites>CDBInstall.xml</suites>
        </item>
    </schedule>
   '),
  (3, 'First Schedule V3', '2015-07-28 09:45', '2015-07-29 12:45', 'enikoal', 1, 1, 1, 3,
   '<?xml version="1.0"?>
    <schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
        <item>
            <name>ERICTAFeniq_cdb_setup_CXP9027959 3</name>
            <component>com.ericsson.ci.cloud.testware:ERICTAFeniq_cdb_setup_CXP9027959</component>
            <suites>CDBInstall.xml</suites>
        </item>
    </schedule>
   '),
  (4, 'Second Schedule V1', '2015-07-28 09:45', '2015-07-29 12:45', 'enikoal', 2, NULL, 0, 1,
   '<?xml version="1.0"?>
    <schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
      <item>
        <name>Schedule Items</name>
        <component>com.ericsson.cifwk.taf.executor:te-taf-testware</component>
        <suites>suite1.xml, suite2.xml</suites>
      </item>
    </schedule>
    '),
  (5, 'Second Schedule V2', '2015-07-28 09:45', '2015-07-29 12:45', 'enikoal', 2, 4, 1, 2,
   '<?xml version="1.0"?>
    <schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
      <item>
        <name>Schedule Items</name>
        <component>com.ericsson.cifwk.taf.executor:te-taf-testware</component>
        <suites>suite1.xml, suite2.xml</suites>
      </item>
    </schedule>
  '),
  (6, 'Third Schedule V1', '2015-07-29 09:45', '2015-07-30 12:45', 'enikoal', 2, NULL, 0, 1,
   '<?xml version="1.0"?>
    <schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
      <item>
        <name>Schedule Items</name>
        <component>com.ericsson.cifwk.taf.executor:te-taf-testware</component>
        <suites>suite1.xml, suite2.xml</suites>
      </item>
    </schedule>
  '),
  (7, 'Third Schedule V2', '2015-07-29 09:45', '2015-07-30 12:45', 'enikoal', 2, 6, 0, 2,
   '<?xml version="1.0"?>
    <schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
      <item>
        <name>Schedule Items</name>
        <component>com.ericsson.cifwk.taf.executor:te-taf-testware</component>
        <suites>suite1.xml, suite2.xml</suites>
      </item>
    </schedule>
  '),
  (8, 'A Schedule to edit', '2015-07-28 09:45', '2015-07-28 10:45', 'enikoal', 1, NULL, 1, 1,
   '<?xml version="1.0"?>
    <schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
        <item>
            <name>ERICTAFeniq_cdb_setup_CXP9027959 1</name>
            <component>com.ericsson.ci.cloud.testware:ERICTAFeniq_cdb_setup_CXP9027959</component>
            <suites>CDBInstall.xml,EUUpgrade.xml</suites>
        </item>
    </schedule>
   '),
   (9, 'Third Schedule V2', '2015-07-29 09:45', '2015-08-04 14:45', 'eniakel', 2, 6, 1, 3,
   '<?xml version="1.0"?>
    <schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
      <item>
        <name>Schedule Items Latest Version</name>
        <component>com.ericsson.cifwk.taf.executor:te-taf-testware</component>
        <suites>suite1.xml, suite2.xml</suites>
      </item>
    </schedule>
  '),
  (10, 'Another Schedule', '2015-07-29 09:45', '2015-08-04 14:45', 'ejhnhng', 2, NULL, 1, 1,
   '<?xml version="1.0"?>
    <schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
      <item>
        <name>Schedule Items Latest Version</name>
        <component>com.ericsson.cifwk.taf.executor:te-taf-testware</component>
        <suites>suite1.xml, suite2.xml</suites>
      </item>
    </schedule>
  ');

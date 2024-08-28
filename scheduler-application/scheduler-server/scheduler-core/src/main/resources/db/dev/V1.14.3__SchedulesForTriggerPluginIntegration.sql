INSERT INTO `Schedule` (`id`, `title`, `created`, `updated`, `createdBy`, `team`,
                        `type`, `dropId`, `originalScheduleId`, `isLastVersion`, `version`, `approvalStatus`, `enabled`,
                        `xml`) VALUES
  (12, 'Schedule For Trigger Plugin 1', '2015-11-24 13:15', '2015-11-24 13:15', 'ekirshe', 'CI-TAF',
   2, 5, NULL, 0, 1, 'APPROVED', 1,
   '<?xml version="1.0"?>
<schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
    <item>
        <name>Sample TE testware</name>
        <component>com.ericsson.cifwk.taf.executor:te-taf-testware:1.0.41</component>
        <suites>success.xml</suites>
    </item>
</schedule>
'),
  (13, 'Schedule For Trigger Plugin 1', '2015-11-24 13:15', '2015-11-24 13:15', 'ekirshe', 'CI-TAF',
   2, 5, 12, 0, 2, 'APPROVED', 1,
   '<?xml version="1.0"?>
<schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
    <item>
        <name>Sample TE testware</name>
        <component>com.ericsson.cifwk.taf.executor:te-taf-testware:1.0.42</component>
        <suites>success.xml</suites>
    </item>
</schedule>
'),
  (14, 'Schedule For Trigger Plugin 1', '2015-11-24 13:15', '2015-11-24 13:15', 'ekirshe', 'CI-TAF',
   2, 5, 12, 1, 3, 'APPROVED', 1,
   '<?xml version="1.0"?>
<schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
    <item>
        <name>Sample TE testware</name>
        <component>com.ericsson.cifwk.taf.executor:te-taf-testware:1.0.43</component>
        <suites>success.xml</suites>
    </item>
</schedule>
');


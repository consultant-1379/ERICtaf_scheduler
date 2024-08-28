INSERT INTO `Schedule` (`id`, `title`, `created`, `updated`, `createdBy`, `team`,
                        `type`, `dropId`, `originalScheduleId`, `isLastVersion`, `version`, `approvalStatus`, `enabled`,
                        `xml`) VALUES
  (15, 'Schedule To Include', '2015-11-24 13:15', '2015-11-24 13:15', 'ejhnhng', 'CI-TAF',
   2, 5, NULL, 1, 1, 'APPROVED', 1,
   '<?xml version="1.0"?>
<schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
    <item>
        <name>ERICTAFcbawritenode_CXP9032159 1</name>
        <component>com.ericsson.oss.mediation:ERICTAFcbawritenode_CXP9032159</component>
        <suites>CBAWriteNode.xml</suites>
    </item>
</schedule>
');


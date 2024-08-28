INSERT INTO `Schedule` (`id`, `title`, `created`, `updated`, `createdBy`, `team`,
                        `type`, `dropId`, `originalScheduleId`, `isLastVersion`, `version`, `approvalStatus`,
                        `xml`) VALUES
  (11, 'Schedule For Testware changes', '2015-11-17 13:15', '2015-11-17 13:15', 'evicovc', 'CI-TAF',
   2, 5, NULL, 1, 1, 'UNAPPROVED',
   '<?xml version="1.0"?>
<schedule xmlns="http://taf.lmera.ericsson.se/schema/te"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
    <item>
        <name>ERICTAFgenericidentitymgmtservice_CXP9031924 1</name>
        <component>com.ericsson.oss.services.security.identitymgmt:ERICTAFgenericidentitymgmtservice_CXP9031924:1.7.4</component>
        <suites>RoleManagement.xml, TargetGroupManagement.xml</suites>
    </item>
</schedule>
');


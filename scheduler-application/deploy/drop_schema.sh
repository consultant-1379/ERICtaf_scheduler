#!/bin/sh
case "$ENV" in
test )
  HOST=atvts2827.athtem.eei.ericsson.se
  USER=scheduler
  PASSWORD="$PASSWORD_TEST"
  PORT=3306
  SCHEMA=taf_scheduler_test
  ;;
*)
  echo "Invalid environment: '$ENV'"
  exit 1
esac


echo "Dropping and recreating schema: $SCHEMA"
mysql -h ${HOST} -u ${USER} --password=${PASSWORD} --port ${PORT} <<EOF
create table IF NOT EXISTS $SCHEMA.EMPTY_TABLE (
    name VARCHAR(255) NOT NULL
);
SET FOREIGN_KEY_CHECKS = 0;
SET @tables = NULL;
SELECT GROUP_CONCAT(table_schema, '.', table_name) INTO @tables
  FROM information_schema.tables
  WHERE table_schema = '${SCHEMA}';
SET @tables = CONCAT('DROP TABLE ', @tables);
PREPARE stmt FROM @tables;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
SET FOREIGN_KEY_CHECKS = 1;
EOF

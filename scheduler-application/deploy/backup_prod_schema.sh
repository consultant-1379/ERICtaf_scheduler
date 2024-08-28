#!/bin/sh

currentTime=`date +"%m_%d_%Y"`
TEMP_FILE="/proj/PDU_OSS_CI_TAF/Scheduler_Backup/Scheduler_backup_${currentTime}.sql"
export PATH=/opt/SELIW/mysql/5.6.13/bin:$PATH

HOST=mysqlpc03.lmera.ericsson.se
USER=nam_taf_admin
PASSWORD="$PASSWORD_PROD"
PORT=3338
SCHEMA=taf_scheduler

echo "Backing Up Database"
mysqldump -h ${HOST} -u ${USER} -p${PASSWORD} --port ${PORT} ${SCHEMA} > ${TEMP_FILE}

echo "Saving file to ${TEMP_FILE}"
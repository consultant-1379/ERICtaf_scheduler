#!/bin/sh
SSHPASS=/proj/PDU_OSS_CI_TAF/tools/sshpass-1.05/sshpass

case "$ENV" in
prod )
  HOST=eselivm2v210l.lmera.ericsson.se
  USER=tafuser
  PASSWORD="$PASSWORD_PROD"
  WEBAPP_DIR=/proj/PDU_OSS_CI_TAF/taf-scheduler
  WEBAPP_STATIC_DIR=/proj/PDU_OSS_CI_TAF/taf-scheduler-static
  TOMCAT_START=echo
  TOMCAT_STOP=echo
  ;;
test )
  HOST=atvts2827.athtem.eei.ericsson.se
  USER=root
  PASSWORD="$PASSWORD_TEST"
  WEBAPP_DIR=/root/taf_scheduler/apache-tomcat-7.0.40/webapps
  WEBAPP_STATIC_DIR=/tmp/taf_scheduler/static
  TOMCAT_START=/root/taf_scheduler/apache-tomcat-7.0.40/bin/startup.sh
  TOMCAT_STOP=/root/taf_scheduler/apache-tomcat-7.0.40/bin/shutdown.sh
  ;;
*)
  echo "Invalid environment: '$ENV'"
  exit 1
esac

echo "
Deployment environment:
HOST=${HOST}
USER=${USER}
WEBAPP_DIR=${WEBAPP_DIR}
WEBAPP_STATIC_DIR=${WEBAPP_STATIC_DIR}
TOMCAT_START=${TOMCAT_START}
TOMCAT_STOP=${TOMCAT_STOP}
"

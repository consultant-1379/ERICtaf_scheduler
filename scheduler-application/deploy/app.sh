#!/bin/sh
set -e

source `dirname $0`/env.sh

SERVER_ARTIFACT=scheduler
SERVER_ARTIFACT_DIR=scheduler-application/scheduler-server/scheduler-war/target

CLIENT_ARTIFACT=client-dist
CLIENT_ARTIFACT_DIR=scheduler-application/scheduler-client/target

echo "Uploading artifacts"
${SSHPASS} -p "$PASSWORD" scp ${SERVER_ARTIFACT_DIR}/${SERVER_ARTIFACT}-*.war ${USER}@${HOST}:/tmp/${SERVER_ARTIFACT}.war

pushd ${CLIENT_ARTIFACT_DIR}/${CLIENT_ARTIFACT}
zip -r /tmp/${CLIENT_ARTIFACT}.zip ./*
popd
${SSHPASS} -p "$PASSWORD" scp /tmp/${CLIENT_ARTIFACT}.zip ${USER}@${HOST}:/tmp/${CLIENT_ARTIFACT}.zip
rm -f /tmp/${CLIENT_ARTIFACT}.zip

echo "Running deployment commands"
${SSHPASS} -p "$PASSWORD" ssh ${USER}@${HOST} <<EOF

echo "Stopping tomcat"
$TOMCAT_STOP

echo "Moving web app"
rm -rf ${WEBAPP_DIR}/ROOT.war
rm -rf ${WEBAPP_DIR}/ROOT

mv -f /tmp/${SERVER_ARTIFACT}.war ${WEBAPP_DIR}/ROOT.war

echo "Moving client static files"
rm -rf ${WEBAPP_STATIC_DIR}/*
unzip /tmp/${CLIENT_ARTIFACT}.zip -d ${WEBAPP_STATIC_DIR}/

echo "Cleaning up"
rm -f /tmp/${CLIENT_ARTIFACT}.zip

echo "Starting tomcat"
$TOMCAT_START

echo "Starting nginx"
service nginx start

EOF
echo "Complete"

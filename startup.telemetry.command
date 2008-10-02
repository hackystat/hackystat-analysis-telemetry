echo -n -e "\033]0;$HACKYSTAT_VERSION Telemetry\007"; cd $HACKYSTAT_SERVICE_DIST/hackystat-analysis-telemetry; java -Xmx512M -jar telemetry.jar

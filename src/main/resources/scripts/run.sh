#!/bin/sh

cd `dirname $0`

java -Xmx4000M -Xms1500M \
     -Djava.util.logging.config.file=logging.properties \
     -cp "lib/*" \
     gov.nih.nci.export.Exporter \
     "$@"

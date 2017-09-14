Protege Server Exporter
======================
A simple utility to be run from the command line, that connects to a running Protege server and exports a specified ontology to a file.

To build:

````
mvn install
````

To run:

````
cd target/exporter-distribution/exporter

./run.sh

````

The exporter requires a config file and by default looks for `exporter.properties`

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

The exporter requires a config file and by default looks for `exporter.properties`. An example is found [here][1].

To run with a different config:

````
./run.sh --config <config-filename>
````

----
[1]: https://github.com/NCIEVS/protege-server-export/blob/master/src/main/resources/config/exporter.properties

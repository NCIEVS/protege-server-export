package gov.nih.nci.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.protege.editor.owl.client.LocalHttpClient;
import org.protege.editor.owl.client.api.OpenProjectResult;
import org.protege.editor.owl.client.api.exception.AuthorizationException;
import org.protege.editor.owl.client.api.exception.ClientRequestException;
import org.protege.editor.owl.server.versioning.api.ServerDocument;
import org.protege.editor.owl.server.versioning.api.VersionedOWLOntology;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import edu.stanford.protege.metaproject.api.ProjectId;
import edu.stanford.protege.metaproject.impl.ProjectIdImpl;

public class Exporter {
	
	private static final Logger log = Logger.getLogger(Exporter.class.getName());
	
	public static String USERNAME = "username";
	public static String PWD = "password";
	public static String HOST = "host";
	public static String PORT = "port";
	public static String PROJECT_ID = "project_id";
	public static String FORMAT = "format";
	public static String OUTPUT_FILENAME = "output_filename";
	public static String OUTPUT_FORMAT_TYPE = "output_format_type";
	public static String RDFXML = "RDFXML";
	
	private String config_filename = "exporter.properties";
	private Properties config = null;
	
	
	
	public Exporter(String[] args) {
		
		if (args.length > 0) {
			if (args.length == 2) {
				if (!args[0].equals("--config")) {
					help();
				} else {
					config_filename  = args[1];
				}
				
			} else {
				help();
			}
			
		} else {
			config_filename = "exporter.properties";
		}
		
		loadProps();
	}
	
	private void loadProps() {
		config = new Properties();
		try {
			config.load(new FileInputStream(config_filename));
		}
		catch (FileNotFoundException fnfe) {
			log.log(Level.SEVERE, "Configuration file for the Exporter not found", fnfe);
			help();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Can't open or otherwise load file into properties", e);
			help();
			
		}
		
	}
	
	public void run() {

		try {
			
			String host = config.getProperty(HOST);
			String port = config.getProperty(PORT);
			String username = config.getProperty(USERNAME);
			String password = config.getProperty(PWD);
			String project_id = config.getProperty(PROJECT_ID);
			String output_filename = config.getProperty(OUTPUT_FILENAME);
			
			String output_format_type = config.getProperty(OUTPUT_FORMAT_TYPE, RDFXML);
			
			LocalHttpClient client = new LocalHttpClient(username, password, host + ":" + port);
			
			ProjectId pid = new ProjectIdImpl(project_id);
			
			OpenProjectResult openProjectResult = 
					client.openProject(pid);
            ServerDocument serverDocument = openProjectResult.serverDocument;
            
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            
            VersionedOWLOntology vont = client.buildVersionedOntology(serverDocument, 
            		man, pid);
            
            File f =  new File(output_filename);
            man.setOntologyDocumentIRI(vont.getOntology(), IRI.create(f));
            
            final URI documentURI = man.getOntologyDocumentIRI(vont.getOntology()).toURI();

            IRI documentIRI = IRI.create(documentURI);
            
            OWLDocumentFormat doc_format = null;
            if (output_format_type.equals(RDFXML)) {
            	 doc_format = new RDFXMLDocumentFormat();
            } else {
            	log.log(Level.SEVERE, "This output format not supported, contact developer");
            }
            
            OntologySaver saver = OntologySaver.builder()
                    .addOntology(vont.getOntology(), doc_format, documentIRI)
                    .build();
            saver.saveOntologies();
            
            log.log(Level.INFO, "Finished");
            
            System.exit(0);
            
            
		
		} catch (AuthorizationException | ClientRequestException e) {
			log.log(Level.SEVERE, "User not authorized or unable to connect to the protege server", e);
			help();
		} catch (OWLOntologyStorageException e) {
			log.log(Level.SEVERE, "Issue in the ontology itself", e);
			help();			
		}
	}

	public static void main(String[] args) {
		
		Exporter exp = new Exporter(args);
		
		exp.run();
		

	}
	
	private void help() {
		StringBuffer buf = new StringBuffer();

		buf.append( "Usage: export [--config <file name>] \n\n" );
		buf.append( "Exports an ontology from a running Protégé server \n" );
		buf.append( "Specify an optional config file to use, otherwise it looks for \n"
				+ "export.properties in the directory from which the script is run \n");
		
		buf.append( "\n" );
		

		System.out.println( buf );
		System.exit( 0 );
	}

}

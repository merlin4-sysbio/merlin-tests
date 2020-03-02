package pt.uminho.ceb.biosystems.merlin.tests.marhsalxml;

import java.io.File;
import java.io.ObjectInputStream.GetField;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import org.stringtemplate.v4.compiler.CodeGenerator.list_return;

import pt.uminho.ceb.biosystems.merlin.dataAccess.InitDataAccess;
import pt.uminho.ceb.biosystems.merlin.entities.auxiliar.EntityInstances;
import pt.uminho.ceb.biosystems.merlin.entities.model.ModelGene;
import pt.uminho.ceb.biosystems.merlin.entities.model.ModelGeneHasOrthology;
import pt.uminho.ceb.biosystems.merlin.entities.model.ModelSequence;
import pt.uminho.ceb.biosystems.merlin.services.implementation.DatabaseServiceEntityExporter;

public class TestMarshal {

	
	@Test
	public void getGeneEntities() {
		try {
			
			//InitDataAccess.getInstance().getDatabaseExporterBatchService("keggDataMerlin4").dbtoXML();
			//InitDataAccess.getInstance().getDatabaseExporterBatchService("testKeggBatchLoader2").readxmldb();
			//InitDataAccess.getInstance().getDatabaseExporterBatchService("testKeggBatchLoader4").readxmldb();
			
			//String path = "C:\\Users\\Diogo\\Desktop\\MERLIN_MARSHAL\\";
			//InitDataAccess.getInstance().getDatabaseExporterBatchService("testKeggBatchLoader7").readxmldb(path);

			//int folderCount = new File("C:\\Users\\Diogo\\OneDrive - Universidade do Minho\\MERLIN\\merlin-aibench\\utilities").list().length;

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

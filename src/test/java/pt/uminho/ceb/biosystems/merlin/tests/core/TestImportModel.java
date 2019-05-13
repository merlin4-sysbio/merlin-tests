package pt.uminho.ceb.biosystems.merlin.tests.core;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import pt.uminho.ceb.biosystems.merlin.aibench.utilities.TimeLeftProgress;
import pt.uminho.ceb.biosystems.merlin.biocomponents.io.readers.ModelImporter;
import pt.uminho.ceb.biosystems.merlin.core.datatypes.WorkspaceInitialData;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.Connection;
import pt.uminho.ceb.biosystems.merlin.services.model.loaders.LoadMetabolicData;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.ErrorsException;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.JSBMLValidationException;

public class TestImportModel extends LoadMetabolicData{
	
//	private ConcurrentLinkedQueue<String> compoundsWithBiologicalRoles;
//	private ConcurrentHashMap<String,Integer> pathwaysID;

	
	public TestImportModel(ModelImporter modelData, Connection connection, WorkspaceInitialData databaseInitialData, TimeLeftProgress progress, AtomicInteger datum, AtomicBoolean cancel, AtomicInteger dataSize, long startTime) 
					throws IOException, ErrorsException, ParserConfigurationException, SAXException, JSBMLValidationException, SQLException{
		
		super(connection, null, databaseInitialData ,cancel,  dataSize);
		
//		this.compoundsWithBiologicalRoles = new ConcurrentLinkedQueue<String>();
//		this.pathwaysID = new ConcurrentHashMap<>();
		
		this.setKeggPathwaysHierarchy(modelData.getWorkspaceData().getResultPathwaysHierarchy());
		this.setResultMetabolites(modelData.getWorkspaceData().getResultMetabolites());
		this.setResultCompartments(modelData.getWorkspaceData().getResultCompartments());
		this.setResultEnzymes(modelData.getWorkspaceData().getResultEnzymes());
		this.setResultReactions(modelData.getWorkspaceData().getResultReactions());
		this.setResultGenes(modelData.getWorkspaceData().getResultGenes());
		
	}
}

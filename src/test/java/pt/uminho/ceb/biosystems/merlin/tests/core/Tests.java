package pt.uminho.ceb.biosystems.merlin.tests.core;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.biojava.bio.search.SeqSimilaritySearchHit;
import org.biojava.bio.search.SeqSimilaritySearchResult;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.blast.org.biojava3.ws.alignment.qblast.NCBIQBlastAlignmentProperties;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.blast.org.biojava3.ws.alignment.qblast.NCBIQBlastOutputFormat;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.blast.org.biojava3.ws.alignment.qblast.NCBIQBlastOutputProperties;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.blast.org.biojava3.ws.alignment.qblast.NcbiBlastService;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.datatypes.EntryData;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ebi.uniprot.UniProtAPI;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.utilities.Enumerators.FileExtensions;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.utilities.MySleep;
import pt.uminho.ceb.biosystems.merlin.biocomponents.io.SBMLLevelVersion;
import pt.uminho.ceb.biosystems.merlin.biocomponents.io.readers.ContainerBuilder;
import pt.uminho.ceb.biosystems.merlin.biocomponents.io.readers.MerlinImportUtils;
import pt.uminho.ceb.biosystems.merlin.biocomponents.io.writers.SBMLLevel3Writer;
import pt.uminho.ceb.biosystems.merlin.core.remote.loader.kegg.DatabaseInitialData;
import pt.uminho.ceb.biosystems.merlin.core.remote.loader.kegg.KeggLoader;
import pt.uminho.ceb.biosystems.merlin.core.remote.loader.kegg.LoadKeggData;
import pt.uminho.ceb.biosystems.merlin.core.remote.retriever.alignment.HomologyDataClient;
import pt.uminho.ceb.biosystems.merlin.core.remote.retriever.alignment.blast.ReadBlasttoList;
import pt.uminho.ceb.biosystems.merlin.core.remote.retriever.kegg.RetrieveKeggData;
import pt.uminho.ceb.biosystems.merlin.core.utilities.Enumerators.HomologySearchServer;
import pt.uminho.ceb.biosystems.merlin.core.utilities.LoadFromConf;
import pt.uminho.ceb.biosystems.merlin.database.connector.databaseAPI.ProjectAPI;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.Connection;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.DatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.MySQLDatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.utilities.Enumerators.Matrix;
import pt.uminho.ceb.biosystems.merlin.utilities.io.FileUtils;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.JSBMLReader;

public class Tests {

	final static Logger logger = LoggerFactory.getLogger(Tests.class);


	//	@Test
	public void test() throws Exception {

		String sequence = "MKKLVIYYSHDGNTKFIAETIAREINADITELKTKKTMNASGLMKVGWGVRQLVSQSEPIL"
				+ "LTMEKNPADYDLIIIGSPVWTYTFAPPIRTFFKKHSMVGKKIGLFCCHGGQKGRTLENMKKLLDGNMI"
				+ "IGECEFLEPLSYEKENNKKKAIVWAKKIVNE";
		int numberOfAlignments = 100;

		NCBIQBlastAlignmentProperties rqb = new NCBIQBlastAlignmentProperties();
		rqb.setBlastProgram("blastp");
		rqb.setBlastDatabase("nr");
		rqb.setBlastWordSize(2);
		rqb.setBlastExpect(1e-10);
		rqb.setBlastMatrix(Matrix.BLOSUM62.toString().toUpperCase());
		rqb.setHitlistSize(numberOfAlignments);
		rqb.setOrganism("272562");

		String newRid = null;

		NcbiBlastService client = new NcbiBlastService(30000, "a@a.aa");
		newRid = client.sendAlignmentRequest(sequence,rqb);

		logger.info("My requeste identifier {}", newRid);

		while(!client.isReady(newRid, GregorianCalendar.getInstance().getTimeInMillis())) {

			logger.info("Status for RID {} not ready", newRid);
			MySleep.myWait(15000);
		}

		//	newRid = "215CC1ZH014";

		NCBIQBlastOutputProperties rof = new NCBIQBlastOutputProperties();
		rof.setOutputFormat(NCBIQBlastOutputFormat. TEXT);
		rof.setAlignmentOutputFormat(NCBIQBlastOutputFormat.PAIRWISE);
		rof.setDescriptionNumber(numberOfAlignments);
		rof.setAlignmentNumber(numberOfAlignments);
		if(rqb.getOrganism()!=null)
			rof.setOrganisms(rqb.getOrganism());

		ReadBlasttoList blastToList = new ReadBlasttoList(client.getAlignmentResults(newRid, rof));

		for (SeqSimilaritySearchResult result : blastToList.getResults()) {

			@SuppressWarnings("unchecked")
			List<SeqSimilaritySearchHit> hits = (List<SeqSimilaritySearchHit>) result.getHits();

			for (int i = 0; i<hits.size();i++ ){

				SeqSimilaritySearchHit hit = hits.get(i);

				String id = hit.getAnnotation().getProperty("subjectDescription").toString();
				String[] xrefs = id.split("\\s");
				id = xrefs[0];

				if(id==null) {

					System.out.println("HIT ERROR "+hit);
					System.out.println("score "+hit.getScore()+" evalue "+hit.getEValue());
				}
				else {

					System.out.println("ID "+id);
					System.out.println("score "+hit.getScore()+" evalue "+hit.getEValue());
				}
			}
		}

		HomologyDataClient homologyDataEbiClient = new HomologyDataClient(blastToList, new String [7], new ConcurrentHashMap<String, String[]>(), 
				new ConcurrentHashMap<String, Boolean>(), new AtomicBoolean(false), HomologySearchServer.NCBI, rqb.getHitlistSize(), true, 36331);


		System.out.println(homologyDataEbiClient.getLocus_tag());
		System.out.println(homologyDataEbiClient.getEValue());
	}

	public static void kegg(String user, String password, String host, int port, String database, String organismID) throws Exception {

		long startTime = System.currentTimeMillis();

		RetrieveKeggData retrieveKeggData = new RetrieveKeggData(organismID, null, null);
		DatabaseAccess dba = new MySQLDatabaseAccess(user, password, host, port, database);

		int numberOfProcesses =  Runtime.getRuntime().availableProcessors()*2;
		List<Thread> threads = new ArrayList<Thread>();

		Connection conn = new Connection(dba);
		DatabaseInitialData databaseInitialData = new DatabaseInitialData(conn);
		databaseInitialData.retrieveAllData();
		for(int i=0; i<numberOfProcesses; i++) {

			Runnable load_KEGG_Data = new LoadKeggData(conn,retrieveKeggData, databaseInitialData, null, null, null, null, startTime);
			Thread thread = new Thread(load_KEGG_Data);
			threads.add(thread);
			thread.start();
		}

		for(Thread thread :threads)
		{
			thread.join();
		}

		long endTime2 = System.currentTimeMillis();

		Statement stmt = conn.createStatement();
		long startTime1 = System.currentTimeMillis();
		KeggLoader.buildViews(conn, stmt);
		long endTime1 = System.currentTimeMillis();

		long endTime = System.currentTimeMillis();

		System.out.println("Total elapsed time in execution of method Load_kegg is :"+ String.format("%d min, %d sec", 
				TimeUnit.MILLISECONDS.toMinutes(endTime2-startTime),TimeUnit.MILLISECONDS.toSeconds(endTime2-startTime) -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime2-startTime))));

		System.out.println("Total elapsed time in execution of method build view is :"+ String.format("%d min, %d sec", 
				TimeUnit.MILLISECONDS.toMinutes(endTime1-startTime1),TimeUnit.MILLISECONDS.toSeconds(endTime1-startTime1) -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime1-startTime1))));

		System.out.println("Total elapsed time in execution of method TOTAL is :"+ String.format("%d min, %d sec", 
				TimeUnit.MILLISECONDS.toMinutes(endTime-startTime),TimeUnit.MILLISECONDS.toSeconds(endTime-startTime) -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime-startTime))));
		conn.closeConnection();
	}

	//	@Test
	public void testReadConfExtensions() throws IOException{

		File extensionFile = new File("D:/projects/merlin/merlin-core/temp/../conf/ftpfiles_extensions.conf");

		HashMap<String, String> extensions = new HashMap<String,String>();

		BufferedReader bufferedReader = new BufferedReader(new FileReader(extensionFile));

		String text, type, extension;
		while ((text = bufferedReader.readLine()) != null) {
			if(text.toUpperCase().matches("^[A-Z].*$")) {
				type = text.toUpperCase().split("\t")[0];
				extension = text.split("\t")[1];

				extensions.put(type,extension);
			}
		}
		bufferedReader.close();

		String temPath = FileUtils.getWorkspacesFolderPath();

		System.out.println(temPath);
	}


	//	@Test
	public void readRNAfile(){

		Map<String, AbstractSequence<?>> codingSequences = new HashMap<String, AbstractSequence<?>>();

		PrintWriter tRNA, rRNA;

		try {
			codingSequences.putAll(FastaReaderHelper.readFastaDNASequence(new File("C:/Users/adias/Desktop/GCA_000008605.1_ASM860v1_rna_from_genomic.fna")));

			tRNA = new PrintWriter("C:/Users/adias/Desktop/Teste/" + "trna_from_genomic.fna", "UTF-8");
			rRNA = new PrintWriter("C:/Users/adias/Desktop/Teste/" + "rrna_from_genomic.fna", "UTF-8");

			for(String key:codingSequences.keySet()){

				String fileKey = key;
				fileKey = fileKey.split("\\s")[0];

				if(fileKey.contains("trna")){

					tRNA.write(">"+fileKey+"\n");
					tRNA.write(codingSequences.get(key).getSequenceAsString()+"\n");
				}
				else{
					rRNA.write(">"+fileKey+"\n");
					rRNA.write(codingSequences.get(key).getSequenceAsString()+"\n");
				}
			}
			tRNA.close();
			rRNA.close();

			System.out.println("File divided with success!");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	//    Map <String, String> map = new HashMap<>();
	//    
	//    System.out.println("BEGIN");
	//    
	//    rs = stmt.executeQuery("SELECT locusTag, query FROM geneHomology;");
	//    
	//    while(rs.next())
	//    	map.put(rs.getString(1), rs.getString(2));
	//    
	//    List<String> locus = new ArrayList<>();
	//    
	//    rs = stmt.executeQuery("SELECT locusTag FROM gene;");
	//    
	//    while(rs.next())
	//    	locus.add(rs.getString(1));
	//    
	//    for (String locusTag : locus){
	//    	stmt.execute("UPDATE gene SET sequence_id ='"+ map.get(locusTag) +"' WHERE locusTag  ='"+ locusTag +"';");
	//    }
	//    
	//    System.out.println("TERMINATED");


	//	@Test
	public void copyFile() throws IOException {

		Path source, destiny = null;

		File file = new File("C:/Users/Amaro/Desktop/protein.gpff");

		source = file.toPath();
		System.out.println(source);

		destiny = new File("C:/Users/Amaro/Desktop/testar/".concat(FileExtensions.CUSTOM_GENBANK_FILE.getExtension()).concat(".gpff")).toPath();

		Files.copy(source, destiny, StandardCopyOption.REPLACE_EXISTING);
	}

	//	@Test
	public void testDeleteFolder() throws IOException{

		File file = new File("C:/Users/adias/Desktop/Teste");
		FileUtils.deleteDirectory(file);
	}


	//	@Test
	public void testar(){

		String foreignTableLength = "33";
		String foreignTable2Length = "25";
		Integer length = Integer.parseInt(foreignTableLength);
		Integer length2 = Integer.parseInt(foreignTable2Length);

		Integer currentSKeyValue = Integer.parseInt("11");
		Integer currentSKeyValue2 = Integer.parseInt("39");
		Long newSKeyValue = Long.parseLong(currentSKeyValue + length+"");
		Long newSKeyValue2 = Long.parseLong(currentSKeyValue2 + length2+"");

	}


	//	@Test
	public void uptadteDatabasesForMerging() throws SQLException {

		DatabaseAccess dba = new MySQLDatabaseAccess("adias", "adias#", "193.137.11.210", 3306, "csa_swp_trm");
		
		Connection conn = new Connection(dba);
		Statement originalDBStatement = conn.createStatement();

		Map<String, String[]> tables = new HashMap<String, String[]>();
		tables.put("productList", null);
		tables.put("homologyData", "productList,ecNumberList".split(","));
		tables.put("fastaSequence", null);
		tables.put("ecNumber", "homologues_has_ecNumber".split(","));
		tables.put("homologySetup", "geneHomology".split(","));
		tables.put("ecNumberList", null);
		tables.put("productRank", "productRank_has_organism".split(","));
		tables.put("ecNumberRank", "ecNumberRank_has_organism".split(","));
		tables.put("organism", "homologues,productRank_has_organism,ecNumberRank_has_organism".split(","));
		tables.put("homologues", "geneHomology_has_homologues,homologues_has_ecNumber".split(","));
		tables.put("geneHomology", "fastaSequence,ecNumberRank,productRank,geneHomology_has_homologues,homologyData".split(","));

		
		DatabaseAccess dbAccess = new MySQLDatabaseAccess("adias", "adias#", "193.137.11.210", 3306, "copy_csa_trm");
		
		Connection newConn = new Connection(dbAccess);
		Statement queryStatement = newConn.createStatement();
		Statement executeStatement = newConn.createStatement();
		Statement executeStatement2 = newConn.createStatement();

		for(String table : tables.keySet()){

			System.out.println("Tabela--->"+table);

			String[] dependentTables = tables.get(table);
			ProjectAPI.updateSKeys(queryStatement, executeStatement, executeStatement2 , originalDBStatement, dependentTables, table);
		}

		queryStatement.close();
		executeStatement.close();
		newConn.closeConnection();

		System.out.println("Database 'copy_csa_trm' updated successfuly!");
	}

	
	//	@Test
	public void mergeDBs() throws SQLException{

		DatabaseAccess dba = new MySQLDatabaseAccess("adias", "adias#", "193.137.11.210", 3306, "");
		Connection conn = new Connection(dba);
		Statement stmt= conn.createStatement();

		String[] allTables = {"productList","homologyData","fastaSequence","ecNumber","homologySetup","ecNumberList","productRank","ecNumberRank",
				"geneHomology","organism","homologues","productRank_has_organism","ecNumberRank_has_organism","geneHomology_has_homologues",
		"homologues_has_ecNumber"};

		String sourceDB = "copy_csa_trm" , destDB = "csa_swp_trm";

		ProjectAPI.mergeTables(stmt, allTables, sourceDB, destDB);

		stmt.close();
		conn.closeConnection();

		System.out.println("Databases successfuly merged!");
	}


	//	@Test
	public void testMaxTabela() throws SQLException{

		DatabaseAccess dba = new MySQLDatabaseAccess("adias", "adias#", "193.137.11.210", 3306, "csa_swp");
		Connection conn = new Connection(dba);
		Statement stmt = conn.createStatement();

		ResultSet rs = stmt.executeQuery("SELECT Count(*) FROM homologyData;");

		String valorMax = rs.getString(1);

		System.out.println(valorMax);
	}

	//	@Test
	public void getEntry(){

		String query = "AAC73786.1";

		EntryData entryData = UniProtAPI.getEntryData(query, 511145);

		System.out.println(entryData);

	}

	//	@Test
	public void testerSqlShow() throws SQLException {

		DatabaseAccess dbAccess = new MySQLDatabaseAccess("adias", "adias#", "193.137.11.210", 3306, "copy_csa_trm");
		Connection newConn = new Connection(dbAccess);
		Statement stmt = newConn.createStatement();

		ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM geneHomology_has_homologues;");

		rs.next();

		System.out.println(rs.getString(1));

	}

//	@Test
	public void loadFromSBML() throws Exception{

		File model = new File("C:/Users/adias/Desktop/OneDrive - Universidade do Minho/models/M_hungatei_JBastos.sbml");

		JSBMLReader reader = new JSBMLReader(model.getAbsolutePath(), "NoName");			
		Container cont = new Container(reader);
		
//		System.out.println(cont.getReaction("R_rxn00835_c0").getGeneRuleString());
		
//		for(ReactionCI reaction : cont.getReactions().values())
//			System.out.println(reaction.getId() + "----->"+ reaction.getGeneRuleString());
		

		//		System.out.println(cont.getReaction("R_RXN0001_c0").getEc_number());
		//		System.out.println(cont.getReaction("R_RXN0001_c0").getEcNumbers());

		//		System.out.println(cont.getReaction("R_rxn14003_c0").identifyCompartments().isEmpty());
		//		System.out.println(cont.getReaction("R_rxn14003_c0").getName());

		//		System.out.println(cont.getGenes());
		//		System.out.println(cont.getGene("MHUN_RS09345").getGeneId());
		//		System.out.println(cont.getGene("MHUN_RS09345").getReactionIds());
		//		System.out.println(cont.getGene("MHUN_RS09345").getGeneName());
		//		
		//		
		//		cont.getCompartment("").getMetabolitesInCompartmentID();

		//		System.out.println(cont.getReaction("R_rxn01964_c0").getGeneRuleString());
		//		System.out.println(cont.getReaction("R_rxn01964_c0").getGenesIDs());
		//		System.out.println(cont.getReaction("R_rxn01964_c0").getProteinRule());
		//		System.out.println(cont.getReaction("R_rxn01964_c0").getProteinIds());
		//		
		//		System.out.println(cont.getReaction("R_rxn01964_c0").identifyCompartments());
		//		ReactionTypeEnum rt = ReactionTypeEnum.Transport;

		//		System.out.println(cont.getReactionsByType(rt));

		//		System.out.println(cont.getReaction("R_rxn13783_c0").getType());
		//		System.out.println(cont.getReaction("R_rxn13783_c0").getReactants());
		//		
		//		ReactionContainer reaction = new ReactionContainer("abc");
		//		
		//		System.out.println(reaction.getLocalisation()==null);

		//		System.out.println(cont.getCompartment(cont.getReaction("R_rxn14003_c0").identifyCompartments().toArray()[0].toString()).getName());

		//		System.out.println(cont.getECNumbers());

		//		System.out.println(cont.getDefaultEC().get("PUNP6").getLowerLimit());

		//		System.out.println(cont.getAllSubsystems(";"));
		//		System.out.println(cont.getReaction("").gets);

		//		System.out.println(cont.getAllEc_Numbers());

		//		Map<String, Set<String>> ecNumbers = cont.getECNumbers();
		//		
		//		System.out.println(ecNumbers.get("1.1.1.75"));

		
		MerlinImportUtils data = new MerlinImportUtils(cont);

		//		ConcurrentLinkedQueue<EnzymeContainer> enzymes = data.getResultEnzymes();
		//		
		//		System.out.println("check");
		//		
		//		while(!enzymes.isEmpty()) {
		//			
		//			System.out.println("check2");
		//
		//			EnzymeContainer enzyme;
		//			if((enzyme = enzymes.poll()) != null) 
		//				System.out.println("Enzyme Name: "+enzyme.getName());
		//		}


//
//		DatabaseAccess dba = new MySQLDatabaseAccess("adias", "#adias", "193.137.11.210", 3306, "M_hungatei_JF1");
//		Connection conn = new Connection(dba);
//		
//		DatabaseInitialData databaseInitialData = new DatabaseInitialData(conn);
//		databaseInitialData.retrieveAllData();
//
//		TimeLeftProgress progress = new TimeLeftProgress();
//		AtomicInteger datum = new AtomicInteger(0);
//		AtomicBoolean cancel = new AtomicBoolean(false);
//		AtomicInteger dataSize = new AtomicInteger(10); 
//		Long startTime = System.currentTimeMillis();
//
//
//		Runnable testImport;
//		try {
//			testImport = new LoadSbmlData(data, conn, databaseInitialData, progress, datum, cancel, dataSize, startTime);
//
//			testImport.run();
//
//			System.out.println("Model Successfully Loaded");
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////
//		conn.closeConnection();
	}



//	@Test
	public void teste(){

		//		System.out.println(FileUtils.getHomeFolderPath().concat("ModelSeedCompounds.tsv"));

		//		String filePath = FileUtils.getHomeFolderPath().concat("ModelSeedCompounds.tsv");
		//		String filePath = FileUtils.getHomeFolderPath().concat("ModelSeedReactions.tsv");
		//		String filePath = "C:/Users/adias/Desktop/KEGG_pathways.tsv";


		//		List<String> compoundsList = new ArrayList<>();
		//
		//		try {
		//			compoundsList = FileUtils.readLines(filePath);
		//		} 
		//
		//		catch (IOException e) {
		//			e.printStackTrace();
		//		} 


		//		for(String line : compoundsList)
		//			System.out.println(line);


		//		String[] infoList = compoundsList.get(149).split("\t");
		//		String[] infoList2 = compoundsList.get(38).split("\t");
		//		
		//		List<String> lista1 = new ArrayList<String> (Arrays.asList(infoList[4].split("\\|")));
		//		
		//		System.out.println(lista1);
		//		
		//		String[] reactions2 = infoList2[4].split("\\|");
		//		
		//		System.out.println(reactions2);
		//
		//		lista1.addAll(Arrays.asList(reactions2));
		//		System.out.println(lista1);

		//		try {
		//			System.out.println(infoList[1].substring(3));
		//		} catch (Exception e) {
		//			System.out.println("coisa");
		//		}
		//		
		//		System.out.println(infoList[3]);

		//		System.out.println(compoundsList.get(1));
		//		System.out.println(compoundsList.get(1).split("\t")[4]);



		//		try {
		//			Integer.parseInt(externalID.substring(1));	//verify we have an ID number with KeggID format ("R"+ numbers)
		//			System.out.println("entrou");
		//		} 
		//		catch (NumberFormatException e) {
		//			System.out.println("falhou");
		//			
		//		}

//		List<String> readerConfFile = null;
//		try {
//			readerConfFile = FileUtils.readLines("C:/Users/adias/git/merlin-core/conf/workbench.conf");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		for(String line : readerConfFile)
//			if(line.trim().startsWith("#merlin-version"))
//				System.out.println(line.split(":")[1].trim());
//		
		
		//REGEX TESTS

		//		String testar = "(((( HVO_2582 or ureA_HVO_0149 ) or ureB_HVO_0147 ) or ureC_HVO_0148 ) or ureX_HVO_A0429 )";
//		String testar = "((( 249 or 250 ) and ( 234 or 250 )) or ( 249 and ( 251 or 250 )))";
		
//		String testar2 = "(( 249 and 234 ) or ( 249 and 250 ) or ( 250 and 234 ) or ( 250 and 250 ) or ( 249 and 251 ) or ( 249 and 250 ))";
		
		String coisa = "1.A.45 The Phage P22 Injectisome (P22 Injectisome) Family";
		String coisa2 = "1.A.110.dsfihsdofhiposd";
		
//		System.out.println(coisa.matches("R\\d{5}"));
		
//		System.out.println(coisa.split("\\.\\d+\\.*\\s+")[1]);
		
//		System.out.println(coisa.matches("\\d\\.\\w\\.\\d+\\.*.*"));

//		String geneRuleString = testar2.trim().substring(1, testar2.length()-1).trim();
//		System.out.println(geneRuleString);

//		Integer conta = StringUtils.countMatches(substring, "(");
//		System.out.println(conta);
//		String[] splited = geneRule.split("[\\(\\)]");
		
		
//		String[] geneRules = geneRuleString.split(" or ");
//		System.out.println(Arrays.asList(geneRules));
//		
//
//		String[] t = Arrays.copyOfRange(splited, conta, splited.length);
//		List<String> lines = Arrays.asList(t);
//		List<Map<String,String[]>> rule = new ArrayList<>();
//

//		Collections.reverse(lines);

//		ModelSeedReactionsDB reacInfo = new ModelSeedReactionsDB();
//		
//		String equation = reacInfo.getReactionEquation("rxn13784");
//		String coisa = "sbdai[2]sd";
//		
//		String teste = equation.replaceAll("[\\[(]\\d[\\])]", "");
//		String teste2 = equation.replaceAll("\\W[^\\d]\\d\\W[^\\d]", "");
//		String teste3 = coisa.replaceAll("\\W", "");
////
////		
//		System.out.println(equation);
//		System.out.println(equation.trim().split("<=>")[0].isEmpty());
//		System.out.println(teste);


	}
	
//	@Test
	public void testReactionAnnotationsSBML3() throws IOException, Exception{
		
		String confPath = FileUtils.getConfFolderPath();
		Map<String,String> confs = LoadFromConf.loadDatabaseCredentials(confPath);
		
		String dbName = "spneumoniaeR6";
		
		DatabaseAccess dba = new MySQLDatabaseAccess(confs.get("username"), confs.get("password"), confs.get("host"), confs.get("port"), dbName);
		Container container = new Container(new ContainerBuilder(dba,"model_".concat(dbName),true,dbName,""));
		
//		ReactionCI reaction = container.getReaction("R_00172");
//		System.out.println(container.getReactions());
//		System.out.println(reaction.getName());
//		System.out.println(reaction.getEc_number());
//		System.out.println(reaction.getId());
//		System.out.println(reaction.getType());
		
//		Map<String,Map<String,String>> metsInfo = container.getMetabolitesExtraInfo();
//		Map<String,Map<String,String>> reacsInfo = container.getReactionsExtraInfo();
		
//		System.out.println(reacsInfo.get("R_00172").get("MERLIN_ID"));
		
//		for(ReactionCI reaction : container.getReactions().values()) {
//			System.out.println("id-->"+reaction.getId() +" " +reaction.getName().split("_")[0]+" GPR---->"+reaction.getGeneRuleString());
//			
//			System.out.println("id-->"+reaction.getId() +", reactionName: " +reaction.getName().split("_")[0]
//					+ ", reactionExtraInfo: " +reacsInfo.get(reaction.getId()));
			
//			if(reaction.getSubsystem()!=null || !reaction.getSubsystem().isEmpty())
//				System.out.println(reaction.getId()+"\t"+reaction.getSubsystem());
//		}
		
//		for(MetaboliteCI metabolite : container.getMetabolites().values()) {
//			
////			System.out.println(metabolite.getId());
//			System.out.println("id-->"+metabolite.getId() +", metaboliteName: " +metabolite.getName()//.split("_")[0]
//					+ ", metaboliteExtraInfo: " +metsInfo.get(metabolite.getId()));
//			
//			System.out.println(metabolite.getFormula());
//		}
		
//		System.out.println(reaction.getSubsystem());
//		System.out.println(reaction.getSusbystems())
		
//		System.out.println(container.getAllSubsystems(";"));
//		System.out.println(container.getReaction("45007"));
//		System.out.println(container.getReaction("R_00176").getSubsystem());
//		System.out.println(new ArrayList<>(container.getAllSubsystems("; ")).get(1));
//		System.out.println(container.getReactionsByPathway("Biomass Pathway"));
		
//		for(String pathway : container.getAllSubsystems("; "))
//			if(!pathway.isEmpty())
//				System.out.println(pathway+"\t"+container.getReactionsByPathway(pathway));
//		System.out.println(container.getPathwaysIDs());
		
//		System.out.println(container.getBiomassId());
//		
//		System.out.println(container.getReaction("R_00084").getName());
		
//		for(ReactionCI reaction : container.getReactions().values())
//			if(reaction.getName().contains("biomass") || reaction.getName().contains("BIOMASS"))
//				System.out.println(reaction.getId()+"\t"+reaction.getName());
		
		SBMLLevelVersion levelAndVersion = SBMLLevelVersion.L3V2;
		
		String path = "C:/Users/Amaro/Desktop/testarSBML3Writer.sbml";
		
		SBMLLevel3Writer escrever = new SBMLLevel3Writer(path, container, "309800", true, null, true, levelAndVersion, true);
		
		escrever.writeToFile();

	}
	
//	@Test
	public void testBeep(){
		
		Toolkit.getDefaultToolkit().beep();
		
	}
	
	@Test
	public void testWriteFile() throws IOException{
		
		FileWriter fstream;
		String output = "C:/Users/adias/Desktop/teste.txt";
		
		try {
			fstream = new FileWriter(output);
		} catch (Exception e) {
			
			File file = new File(output);
			file.createNewFile();
			fstream = new FileWriter(output);
		}
		
		BufferedWriter out = new BufferedWriter(fstream);
		out.write("asdasd");
		out.close();
	}
}	

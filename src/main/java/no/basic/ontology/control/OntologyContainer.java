package no.basic.ontology.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import no.chess.ontology.BlackBoardPosition;
import no.chess.ontology.BlackPiece;
import no.chess.ontology.ChessFactory;
import no.chess.ontology.ChessPosition;
import no.chess.ontology.Taken;
import no.chess.ontology.Vacant;
import no.chess.ontology.WhiteBoardPosition;
import no.chess.ontology.WhitePiece;
import no.chess.ontology.impl.DefaultTaken;


/**
 * @author oluf
 * This class serves as a container for a chosen ontology
 * The org.semanticweb.owlapi.model.OWLOntology ontModel contains all defines classes of the ontology.
 * They are collected from the model like this:
 * OWLClass c : ontModel.getClassesInSignature()
 * Any individuals from classes are collected using the com.clarkparsia.pellet.owlapiv3.PelletReasoner clarkpelletReasoner like this:
 *  NodeSet<OWLNamedIndividual> instances = clarkpelletReasoner.getInstances(c, false);
 *
 */
public class OntologyContainer {
	private OWLOntology ontModel = null;
	private OWLDataFactory owlDatafactory = null;
	private ChessFactory chessFactory = null;
	private Map<String,BlackBoardPosition> allBlackPositions;
	private Map<String,WhiteBoardPosition> allWhitePositions;
	private CodeGenerationInference owlInference;
	private Map<String,BlackPiece> allBlackPieces;
	private Map<String,WhitePiece> allWhitePieces;
	private HashSet<Taken> allTakenPositions;
	private HashSet<Vacant> allVacantPositions;
	private HashSet<ChessPosition> allChessPositions;
	private Set<OWLClass> allClasses;
	private PelletReasoner clarkpelletReasoner;

	public OntologyContainer(OWLOntology ontModel) {
		super();
		
		this.ontModel = ontModel;
//		ontModel.getAxioms();
//		ontModel.getClassesInSignature();
/*		System.out.println("Ontology container: signatures:");
		ontModel.getSignature().
		forEach(System.out::println);*/
//		printOntology();

	}

	public PelletReasoner getClarkpelletReasoner() {
		return clarkpelletReasoner;
	}

	/**
	 * @param clarkpelletReasoner
	 */
	public void setClarkpelletReasoner(PelletReasoner clarkpelletReasoner) {
		boolean takenEmpty = true;
		this.clarkpelletReasoner = clarkpelletReasoner;
		 System.out.println("Model container Pellet reasoner");
		 getAllTakenPositions();
		 if (allTakenPositions.isEmpty()) {
			 System.out.println("Taken positions are empty");
			 takenEmpty = false;
//			 allTakenPositions = new HashSet<Taken>();
		 }
		 allClasses =  ontModel.getClassesInSignature();
		for (OWLClass c : ontModel.getClassesInSignature()) {
		    if (c.getIRI().getFragment().equals("Taken")){
		        NodeSet<OWLNamedIndividual> instances = clarkpelletReasoner.getInstances(c, false);
		        String irs = c.getIRI().toString();
		        System.out.println("Fragment: "+c.getIRI().getFragment()+"IRS: "+irs);
		        for (OWLNamedIndividual i : instances.getFlattened()) {
		            System.out.println("Instance: "+i.getIRI().getFragment()+ " The taken individual "+i.toString()); 
/*		            if (!takenEmpty) {
		            	DefaultTaken taken = (DefaultTaken) chessFactory.createTaken(irs);
		            	allTakenPositions.add(taken);
		            	System.out.println(" New Taken: "+taken.toString());
		            }*/
		        }
		    }
		}
//		printIndividualsByclass(ontModel,"Class");

	}
	



	public Set<OWLClass> getAllClasses() {
		return allClasses;
	}

	public void setAllClasses(Set<OWLClass> allClasses) {
		this.allClasses = allClasses;
	}

	public OWLOntology getOntModel() {
		return ontModel;
	}

	public void setOntModel(OWLOntology ontModel) {
		this.ontModel = ontModel;
	}

	public OWLDataFactory getOwlDatafactory() {
		return owlDatafactory;
	}

	public void setOwlDatafactory(OWLDataFactory owlDatafactory) {
		this.owlDatafactory = owlDatafactory;
	}

	public ChessFactory getChessFactory() {
		return chessFactory;
	}

	public void setChessFactory(ChessFactory chessFactory) {
		this.chessFactory = chessFactory;
	}

	public Map<String, BlackBoardPosition> getAllBlackPositions() {
		return allBlackPositions;
	}

	public void setAllBlackPositions(Map<String, BlackBoardPosition> allBlackPositions) {
		this.allBlackPositions = allBlackPositions;
	}

	public Map<String, WhiteBoardPosition> getAllWhitePositions() {
		return allWhitePositions;
	}

	public void setAllWhitePositions(Map<String, WhiteBoardPosition> allWhitePositions) {
		this.allWhitePositions = allWhitePositions;
	}

	public CodeGenerationInference getOwlInference() {
		return owlInference;
	}

	public void setOwlInference(CodeGenerationInference owlInference) {
		this.owlInference = owlInference;
	}

	/**
	 * getallBlackpositions
	 * @return all Blackboard positions in a HashSet
	 */
	@SuppressWarnings("unchecked")
	public HashSet<BlackBoardPosition> getallgivenBlackpositions(){
		
		return (HashSet<BlackBoardPosition>) chessFactory.getAllBlackBoardPositionInstances();
	}
	/**
	 * getallWhitepositions
	 * @return all Whiteboard positions 
	 */
	public HashSet<WhiteBoardPosition> getallgivenWhitepositions(){
		
		return (HashSet<WhiteBoardPosition>) chessFactory.getAllWhiteBoardPositionInstances();
	}
	public HashSet<BlackPiece> getallgivenBlackpieces(){
		return (HashSet<BlackPiece>) chessFactory.getAllBlackPieceInstances();
	}
	public HashSet<WhitePiece> getallgivenWhitepieces(){
		return (HashSet<WhitePiece>) chessFactory.getAllWhitePieceInstances();
	}
	public HashSet<Taken> getAllTakenPositions() {
		 allTakenPositions = (HashSet<Taken>) chessFactory.getAllTakenInstances();
		return allTakenPositions;
	}

	public void setAllTakenPositions(HashSet<Taken> allTakenPositions) {
		this.allTakenPositions = allTakenPositions;
	}

	public HashSet<Vacant> getAllVacantPositions() {
		allVacantPositions = (HashSet<Vacant>) chessFactory.getAllVacantInstances();
		return allVacantPositions;
	}

	public void setAllVacantPositions(HashSet<Vacant> allVacantPositions) {
		this.allVacantPositions = allVacantPositions;
	}
	
	public HashSet<ChessPosition> getAllChessPositions() {
		 allChessPositions = (HashSet<ChessPosition>) chessFactory.getAllChessPositionInstances();
		return allChessPositions;
	}

	public void setAllChessPositions(HashSet<ChessPosition> allChessPositions) {
		this.allChessPositions = allChessPositions;
	}
	public void printOntology(){
		PrintWriter writer = null;
	    String outputFileName = "G:\\Min disk\\privat\\ontologies\\logicalaxioms.txt";
	      //create an output print writer for the results
	      try 
	      {
	         writer = new PrintWriter(outputFileName);
	      } catch (FileNotFoundException e) {
	         System.err.println("'" + outputFileName 
	            + "' is an invalid output file.");
	         return;
	      }
		 for (OWLLogicalAxiom ax : ontModel.getLogicalAxioms()) {
				writer.println("Ontology container logical axioms: "+ax.toString());
			}
	      writer.close();
	      System.out.println("Printing complete");
	      printStatements();
	}

	 public void printStatements(){
			PrintWriter writer = null;
		    String outputFileName = "G:\\Min disk\\privat\\ontologies\\axioms.txt";
		      //create an output print writer for the results
		      try 
		      {
		         writer = new PrintWriter(outputFileName);
		      } catch (FileNotFoundException e) {
		         System.err.println("'" + outputFileName 
		            + "' is an invalid output file.");
		         return;
		      }	
		      for (OWLAxiom ax :  ontModel.getAxioms()) {
		    	  writer.println("Ontology container axioms: "+ax.toString());
		      }
			writer.println();
		    writer.close();

	 }
	 private void printIndividualsByclass(OWLOntology ontology, String owlClass){
			PrintWriter writer = null;
		    String outputFileName = "G:\\Min disk\\privat\\ontologies\\indiviualsbyclass.txt";
		      //create an output print writer for the results
		      try 
		      {
		         writer = new PrintWriter(outputFileName);
		      } catch (FileNotFoundException e) {
		         System.err.println("'" + outputFileName 
		            + "' is an invalid output file.");
		         return;
		      }
		    for (OWLClass c : ontology.getClassesInSignature()) {
//		        if (c.getIRI().getShortForm().equals(owlClass)){
		            NodeSet<OWLNamedIndividual> instances = clarkpelletReasoner.getInstances(c, false);
		            writer.println("----Class ------: "+ c.getIRI().getShortForm());
		            int ct = 0;
		            for (OWLNamedIndividual i : instances.getFlattened()) {
		            	writer.println("Index: "+ct++ +" "+i.getIRI().getShortForm()); 
		            }
//		        }
		    }
			writer.println();
		    writer.close();

		}

}

package no.basic.ontology.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

//import no.naks.biovigilans.model.Vigilansmelding;

//import org.restlet.data.Parameter;

/**
 * @author olj Denne klassen er superklassen til alle modelklasser som
 *         representerer et skjermbilde (et brukergrensesnitt)
 * @param <T>
 *
 */
public class ParentModel<T> {

	protected boolean lagret = false; // Satt true dersom session objects er lagret
	private Map<String, String> formMap; // Inneholder brukers input verdier fra skjermbildet
	private String[] formNames; // Inneholder navn på input felt i skjermbildet
	private String accountRef;

	public ParentModel() {
		super();
		formMap = new HashMap<String, String>();
		// TODO Auto-generated constructor stub
	}

	public boolean isLagret() {
		return lagret;
	}

	public void setLagret(boolean lagret) {
		this.lagret = lagret;
	}

	public String extract(String s, Function<String, String> f) {
		return f.apply(s);
	}

	/**
	 * extractString This routine extracts a substring form a string using the
	 * Function interface It finds the last index of a string using separator
	 * 
	 * @param line       The original string
	 * @param separator  The separator
	 * @param startindex The startindex
	 * @return the substring
	 */
	public String extractString(String line, char separator, int startindex) {
		int index = line.lastIndexOf(separator);
		if (index == -1)
			return null;
		Function<String, String> f = (String s) -> line.substring(startindex, index);
		Function<String, String> ef = (String s) -> line.substring(index + 1);
		if (startindex == -1)
			return extract(line, ef);
		else
			return extract(line, f);

	}

	private String checkConstants(String body) {
		int fIndex = body.indexOf('"');
		if (fIndex <= 0)
			return null;
		int lIndex = body.indexOf('"', fIndex + 1);
		if (lIndex <= 0)
			return null;
		String label = body.substring(fIndex + 1, lIndex);
		return label;
	}

	private ArrayList<String> getVariables(String body) {
		ArrayList<String> vars = new ArrayList();
		char sep = ':';
//		int start = 2;
		int len = body.length();
		String br = "(";
		String endBr = ")";
		int index = body.lastIndexOf(br);
		int endIndex = body.indexOf(endBr, index);
		while (index >= 19) {
			String var = body.substring(index, endIndex + 1);
			String remove = extractString(var, sep, 0);
			remove = remove + ":";
			var = var.replace(remove, "?");
			var = var.replace(")", "");
			vars.add(var);
			index = index - 2;
			index = body.lastIndexOf(br, index);
			if (index <= 0)
				break;
			endIndex = body.indexOf(endBr, index);
		}
		String constant = checkConstants(body);
		if (constant != null)
			vars.add(constant);
		return vars;
	}

	/**
	 * createRuleString This method creates a rule string based on the toString of
	 * the rule predicate and rule body.
	 * 
	 * @param predicate
	 * @param rule
	 * @return a rule string
	 */
	public String createRuleString(String predicate, String rule) {
		char sep = '#';
		char sep2 = '/';
		char sep3 = '.';
		char sep4 = '>';
		String predicateName = extractString(predicate, sep, -1);
		String predtemp = extractString(predicate, sep, 0);
		String predtt = extractString(predtemp, sep2, -1);
		String predLabel = extractString(predtt, sep3, 0);
		if (predLabel == null)
			predLabel = predtt;
		predLabel = predLabel + ":";
		if (predicateName.contains(">")) {
			predicateName = extractString(predicateName, sep4, 0);
		}
		ArrayList<String> vars = getVariables(rule);
//  	  System.out.println("Createrulestring: "+predtemp+ " : " +predtt+ " variables: "+vars);
		predicateName = predicateName + "(";
		for (String var : vars) {
			predicateName = predicateName + var + ",";
		}
		int endC = predicateName.lastIndexOf(",");
		predicateName = predicateName.substring(0, endC);
		predicateName = predicateName + ")";
		return predLabel + predicateName;
	}

	/**
	 * createRuleLabel This method returns the name of a rule given the .toString
	 * ruleAnnotation
	 * 
	 * @param ruleAnnotation
	 * @return The name of the rule
	 */
	public String createRuleLabel(String ruleAnnotation) {
		int fIndex = ruleAnnotation.indexOf('"');
		int lIndex = ruleAnnotation.lastIndexOf('"');
		String label = ruleAnnotation.substring(fIndex + 1, lIndex);
		return label;
	}

	/**
	 * setValues Denne rutinen setter alle verdier mottatt fra bruker. Verdier må
	 * lagres avhengig av hvilke knapper bruker har valgt
	 * 
	 * @param entry
	 */
	/*
	 * public void setValues(Parameter entry){ String name = entry.getName(); String
	 * value = entry.getValue();
	 * 
	 * boolean finnes = formMap.containsKey(name); if(finnes){ String val =
	 * formMap.get(name); value = val + "-" + value; } formMap.put(name, value);
	 * 
	 * }
	 */

	public Map getFormMap() {
		return formMap;
	}

	public void setFormMap(Map formMap) {
		this.formMap = formMap;
	}

	public String[] getFormNames() {
		return formNames;
	}

	public void setFormNames(String[] formNames) {
		this.formNames = formNames;
	}

	public String getAccountRef() {
		return accountRef;
	}

	public void setAccountRef(String accountRef) {
		this.accountRef = accountRef;
	}

}

/*
 * Created on Oct 12, 2004
 *
 */
package org.neat4j.neat.data.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.log4j.Category;
import org.neat4j.neat.data.core.DataLoader;
import org.neat4j.neat.data.core.ExpectedOutputSet;
import org.neat4j.neat.data.core.NetworkDataSet;
import org.neat4j.neat.data.core.NetworkInput;
import org.neat4j.neat.data.core.NetworkInputSet;
import org.neat4j.neat.data.core.NetworkOutput;

/**
 * @author MSimmerson
 *
 */
public class CSVDataLoader implements DataLoader {
	private static final Category cat = Category.getInstance(CSVDataLoader.class);
	private String fileName;
	private int opCols;
	
	public CSVDataLoader(String fileName, int opCols) {
		this.fileName = fileName;
		this.opCols = opCols;
	}
	/**
	 * @see org.neat4j.ailibrary.nn.data.DataLoader#loadData()
	 */
	public NetworkDataSet loadData() {
		cat.info("Loading data from " + this.fileName);
		return (this.createDataSets());
	}
	
	private NetworkDataSet createDataSets() {
		cat.debug("Creating data sets");
		NetworkDataSet dataSet = new CSVDataSet();
		File csvFile = new File(this.fileName);
		FileInputStream fis;
		StringTokenizer sTok;
		String line;
		ExpectedOutputSet opSet;
		ArrayList eOps = new ArrayList();
		NetworkInputSet ipSet;
		ArrayList ips = new ArrayList();
		try {
			fis = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			while ((line = br.readLine()) != null) {
				sTok = new StringTokenizer(line, ",");
				ips.add(this.createInputPattern(sTok));
				eOps.add(this.createExpectedOutput(sTok));
			}
			ipSet = new CSVInputSet(ips);
			opSet = new CSVExpectedOutputSet(eOps);
			dataSet = new CSVDataSet(ipSet, opSet);
		} catch (FileNotFoundException e) {
			cat.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			cat.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			cat.error(e.getMessage());
			e.printStackTrace();
		}
		
		cat.debug("Creating data sets...Done");
		return (dataSet);
	}
	
	private NetworkInput createInputPattern(StringTokenizer sTok) {
		double[] pattern = new double[sTok.countTokens() - this.opCols];
		NetworkInput ip;
		int i = 0;
		
		while (sTok.hasMoreTokens() && i < pattern.length) {
			pattern[i++] = Double.parseDouble(sTok.nextToken());
		}
		
		ip = new CSVInput(pattern);
		
		return (ip);
	}

	private NetworkOutput createExpectedOutput(StringTokenizer sTok) {
		double[] pattern = new double[sTok.countTokens()];
		NetworkOutput op;
		int i = 0;
		
		while (sTok.hasMoreTokens() && i < pattern.length) {
			pattern[i++] = Double.parseDouble(sTok.nextToken());
		}
		
		op = new CSVExpectedOutput(pattern);
		
		return (op);
	}
}

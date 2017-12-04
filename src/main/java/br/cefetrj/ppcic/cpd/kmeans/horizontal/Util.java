package br.cefetrj.ppcic.cpd.kmeans.horizontal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author rodrigo tavares e joao parana
 *
 */

public class Util {
	
public static double[][] loadFile(String file, boolean ignoreHeader) throws Exception {
		
		
		
		String delimiter = null;
		
		if(file.toUpperCase().contains(".CSV"))
			delimiter = ",";
		
		else
			delimiter = " ";
		
		Scanner scan = new Scanner(new File(file));
		
		List<double[]> lData = new ArrayList<double[]>();
		
		if(ignoreHeader)
			scan.nextLine();
		
		while(scan.hasNextLine()){
			
			String[] sLine = scan.nextLine().split(delimiter);
			
			double[] dLine = new double[sLine.length];
			
			for(int i = 0; i < sLine.length; i++)
				dLine[i] = new Double(sLine[i]);
				
			lData.add(dLine);
			
		}
			
		scan.close();
		
		double[][] data = new double[lData.size()][lData.get(0).length];
		
		for(int i = 0; i < lData.size(); i++)
			data[i] = lData.get(i);
		
		return data;
		
	}
	
	public static double[] calculateDistanceToCentroid(double[] centroid, double[][] data){
		
		int m = data.length;
		
		double[] distToCent = new double[m];
		
		for(int r = 0; r < m; r++)
			distToCent[r] = distance(data[r], centroid);
		
		return distToCent;
		
	}
	
	private static double distance(double[] a, double[] b){
		
		int m = a.length;
		
		double sum = 0;
		
		for(int i = 0; i < m; i++)
			sum += Math.pow(a[i] - b[i], 2);
			
		return Math.sqrt(sum);
		
	}
	
	public static List<double[][]> getPartitionedData(double[][] data, int qtdCores){
		
		List<double[][]> partData = new ArrayList<double[][]>();
		
		int m = data.length;
		int qtdPart = m / qtdCores;
		
		int posIni = 0;
		
		for(int c = 0; c < qtdCores; c++){
			
			if(c != 0)
				posIni += qtdPart;
			
			if(c == (qtdCores - 1))
				qtdPart = -1;
			
			partData.add(getPartMatrix(data, posIni, qtdPart));
			
		}
		
		return partData;
		
	}
	
	private static double[][] getPartMatrix(double[][] data, int lineIni, int qtdPart){
		
		int lineEnd = -1;
			
		if(qtdPart != -1)
			lineEnd = lineIni + qtdPart;
		else
			lineEnd = data.length;
		
		double[][] partData = new double[lineEnd - lineIni][data[0].length];
			
		int pos = -1;
		
		for(int i = lineIni; i < lineEnd; i++){
			
			pos++;
			partData[pos] = data[i];
			
		}
		
		return partData;
		
	}
	
}

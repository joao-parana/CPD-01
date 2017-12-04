package br.cefetrj.ppcic.cpd.kmeans.uml;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import mpi.MPI;

/*

@startuml

class SingleKMeans {

}

class ParallelMain {

}

@enduml


*/

/**
 * 
 * @author rodrigo tavares e joao parana
 *
 */

interface KMeans {
	
	void group();
	
}

public class AllClasses {

	public static void main(String[] args) {

		if (args.length == 0) {
			SingleMain singleMain = new SingleMain();
			singleMain.doIt(null);
		} else {
			ParallelMain parallelMain = new ParallelMain();
			parallelMain.doIt(args);
		}
	}
}




/**
 * 
 * @author rodrigo tavares e joao parana
 *
 */

class ParallelKMeans extends SingleKMeans {
	
	private int qtdCores;
	
	public ParallelKMeans(int k, double[][] data, int qtdCores) {
		
		super(k, data);
		this.qtdCores = qtdCores;
		
	}
	
	@Override
	public double[][] calculateDistanceToCentroids(double[][] centroids){
		
		double[][] centroidsDist = new double[super.k][super.data.length];
		
		for(int i = 0; i < k; i++){
			
			for(int core = 1; core < qtdCores; core++){
				
				Object[] sendObj = new Object[2];
				sendObj[0] = centroids[i];
				
				//System.out.println("Enviando para o core " + core);
				
				MPI.COMM_WORLD.Isend(sendObj, 0, 2, MPI.OBJECT, core, 10);
				
			}
			
			//MPI.COMM_WORLD.Barrier();
			
			int lineCounter = -1;
			
			for(int core = 1; core < qtdCores; core++){
				
				Object[] sendObj = new Object[2];
				
				MPI.COMM_WORLD.Recv(sendObj, 0, 2, MPI.OBJECT, core, 10);
				
				//System.out.println("Recebido do core " + core);
				
				double[] distance = (double[]) sendObj[1];
				
				for(int j = 0; j < distance.length; j++){
					
					lineCounter++;
					centroidsDist[i][lineCounter] = distance[j];
							
				}
				
			}
			
		}
		
		return centroidsDist;
		
	}

}


/**
 * 
 * @author rodrigo tavares e joao parana
 *
 */

class ParallelMain {

public static void doIt(String[] args) {
		
		try{
			
			MPI.Init(args);
			
			int me = MPI.COMM_WORLD.Rank();
			int qtdCores = MPI.COMM_WORLD.Size();
			
			if(me == 0){
			
				if(args.length <= 0){
					
					System.out.println("Valor de k n�o fornecido.");
					return;
					
				}
					
				int k = new Integer(args[0]);
				
				System.out.println("k = " + k);
				
				String file = "W:\\CEFET\\2017.3\\CPD\\trabalhos\\trab_1\\datasets\\Geo-Magnetic field and WLAN\\measure1_smartwatch_sens_6mi.csv";
				
				double[][] data = Util.loadFile(file, true);
				
				List<double[][]> partData = Util.getPartitionedData(data, qtdCores - 1);
		    	
		    	System.out.println("N�mero de tuplas: " + data.length);
		    	
		    	for(int i = 0; i < partData.size(); i++)
		    		System.out.println("core " + (i + 1) + " recebeu " + partData.get(i).length + " tuplas");
		    	
		    	for(int core = 1; core < qtdCores; core++){
		    		
		    		Object[] sendObj = new Object[1];
		    		
		    		int partPos = core - 1;
		    		sendObj[0] = partData.get(partPos);
		    		
		    		//System.out.println("Enviando parte da matriz para o core " + core);
		    		
		    		MPI.COMM_WORLD.Isend(sendObj, 0, 1, MPI.OBJECT, core, 10);
		    		
		    	}
		    	
		    	MPI.COMM_WORLD.Barrier();
		    	
		    	KMeans kmeans = new ParallelKMeans(k, data, qtdCores);
		    	
		    	long start = new Date().getTime();
		    	
		    	kmeans.group();
		    	
		    	long end = new Date().getTime();
		    	
		    	System.out.println("Tempo (ms): " + (end - start));
		    	
			}else{
				
				Object[] sendObj = new Object[1];
				
				MPI.COMM_WORLD.Recv(sendObj, 0, 1, MPI.OBJECT, 0, 10);
				
				//System.out.println("Core " + me + " Recebido parte da matriz do core 0");
				
				double[][] data = (double[][]) sendObj[0];
				
				MPI.COMM_WORLD.Barrier();
				
				while(true){
					
					sendObj = new Object[2];
					
					MPI.COMM_WORLD.Recv(sendObj, 0, 2, MPI.OBJECT, 0, 10);
					
					//System.out.println("Recebido pelo core " + me);
					
					sendObj[1] = Util.calculateDistanceToCentroid((double[]) sendObj[0], data);
					
					//System.out.println("Core " + me + " - Enviando para o core 0");
					
					MPI.COMM_WORLD.Isend(sendObj,0,2,MPI.OBJECT,0,10);
					
					//MPI.COMM_WORLD.Barrier();
					
				}
				
			}
	    	
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
	}

}

/**
 * 
 * @author rodrigo tavares e joao parana
 *
 */

class SingleKMeans implements KMeans {
	
	protected int k;
	protected double[][] data;
	
	public SingleKMeans(int k, double[][] data){
		
		this.k = k;
		this.data = data;
		
	}
	
	public void group() {
		
		int m = data.length;
		int n = data[0].length;
		
		long[] lastGroup = new long[k];
		
		double[][] centroids = new double[k][n];
		
		for(int i = 0; i < k; i++)
			centroids[i] = data[i];
		
		long iterations = 0;
		
		while(true){
			
			iterations++;
			
			System.out.println("Iteracao " + iterations);
			
			List<List<double[]>> lstGroups = new ArrayList<List<double[]>>();
			
			for(int i = 0; i < k; i++)
				lstGroups.add(new ArrayList<double[]>());
			
			double[][] centroidsDist = calculateDistanceToCentroids(centroids);
			
			for(int i = 0; i < m; i++){
				
				double minDist = -1;
				int kgroup = 0;
				
				for(int j = 0; j < k; j++){
					
					double dist = centroidsDist[j][i];
					
					if(dist < minDist || minDist == -1){
						
						minDist = dist;
						kgroup = j;
					
					}
					
				}
				
				lstGroups.get(kgroup).add(data[i]);
				
			}
			
			boolean eq = true;
			
			for(int i = 0; i < k; i++){
				
				if(lastGroup[i] != lstGroups.get(i).size()){
					
					eq = false;
					lastGroup[i] = lstGroups.get(i).size();
					
				}
					
			}
			
			if(eq || iterations == 30)
				break;
			
			for(int i = 0; i < lstGroups.size(); i++){
				
				List<double[]> group = lstGroups.get(i);
				
				for(int j = 0; j < n; j++){
					
					double sumCol = 0;
					
					for(int r = 0; r < group.size(); r++){
						
						sumCol += group.get(r)[j];
						
					}
					
					centroids[i][j] = sumCol / group.size();
					
				}
				
			}
			
		}
		
		System.out.println("N�mero de itera��es: " + iterations);
		
		for(int i = 0; i < lastGroup.length; i++){
			
			double percent = (new Double(lastGroup[i]).doubleValue() * 100.00) / new Double(m).doubleValue();
			System.out.println("Cluster " + i + " possui " + lastGroup[i] + " pontos / " + percent + "%");
			
		}
		
	}
	
	public double[][] calculateDistanceToCentroids(double[][] centroids){
		
		double[][] centroidsDist = new double[k][data.length];
		
		for(int i = 0; i < k; i++)
			centroidsDist[i] = Util.calculateDistanceToCentroid(centroids[i], data);
		
		return centroidsDist;
		
	}
	
}


/**
 * 
 * @author rodrigo tavares e joao parana
 *
 */

class SingleMain {

	public static void doIt(String[] args) {
		
		try{
		
			if(args.length <= 0){
				
				System.out.println("O valor de k n�o foi fornecido.");
				return;
				
			}
				
			int k = new Integer(args[0]);
			
			System.out.println("k = " + k);
			
			String file = "W:\\CEFET\\2017.3\\CPD\\trabalhos\\trab_1\\datasets\\Geo-Magnetic field and WLAN\\measure1_smartwatch_sens_6mi.csv";
			
			double[][] data = Util.loadFile(file, true);
	    	
	    	System.out.println("N�mero de tuplas: " + data.length);
	    	
	    	KMeans kmeans = new SingleKMeans(k, data);
	    	
	    	long start = new Date().getTime();
	    	
	    	kmeans.group();
	    	
	    	long end = new Date().getTime();
	    	
	    	System.out.println("Tempo (ms): " + (end - start));
	    	
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
	}

}


class Teste {

	public static void main(String[] args) {
		
		double[][] data = new double[][]{{1, 2}, {2, 2}, {3, 2}};
		
		List<double[][]> partData = Util.getPartitionedData(data, 3);
		
		System.out.println("CPD");
		
	}

}

/**
 * 
 * @author rodrigo tavares e joao parana
 *
 */

class Util {
	
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

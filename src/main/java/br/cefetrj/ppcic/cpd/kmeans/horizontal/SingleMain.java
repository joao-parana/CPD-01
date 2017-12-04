package br.cefetrj.ppcic.cpd.kmeans.horizontal;

import java.util.Date;

/**
 * 
 * @author rodrigo tavares e joao parana
 *
 */

public class SingleMain {

	public static void main(String[] args) {
		
		try{
		
			if(args.length <= 0){
				
				System.out.println("O valor de k não foi fornecido.");
				return;
				
			}
				
			int k = new Integer(args[0]);
			
			System.out.println("k = " + k);
			
			String file = "W:\\CEFET\\2017.3\\CPD\\trabalhos\\trab_1\\datasets\\Geo-Magnetic field and WLAN\\measure1_smartwatch_sens_6mi.csv";
			
			double[][] data = Util.loadFile(file, true);
	    	
	    	System.out.println("Número de tuplas: " + data.length);
	    	
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

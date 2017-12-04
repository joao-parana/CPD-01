package br.cefetrj.ppcic.cpd.kmeans.vertical;

import java.util.Date;

public class TesteMain {

	public static void main(String[] args) {
		
		try{
			
			int k = 2;
				
			if(args.length > 0)
				k = new Integer(args[0]);
			
			if(k > 3)
				System.out.println("Número máximo para k é 3. A máquina possui 4 cores e processa um k por core.");
			
			if(k < 2)
				System.out.println("Número mínimo para k é 2.");
			
			System.out.println("k = " + k);
			
			String file = "W:\\CEFET\\2017.3\\CPD\\trabalhos\\trab_1\\datasets\\Geo-Magnetic field and WLAN\\measure1_smartwatch_sens_min.csv";
			//String file = "W:\\CEFET\\2017.3\\CPD\\trabalhos\\trab_1\\datasets\\teste.csv";
    	
	    	double[][] data = UtilVertical.loadFile(file, true);
	    	
	    	System.out.println("Número de tuplas: " + data.length);
	    	
	    	long start = new Date().getTime();
	    	
	    	new TesteKMeans(k, data).group();
	    	
	    	long end = new Date().getTime();
	    	
	    	System.out.println("Tempo (ms): " + (end - start));
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}

package cpd_kmeans;

import java.util.Date;

import mpi.MPI;

public class Main {

	public static void main(String[] args) {
		
		try{
			
			MPI.Init(args);
			
			int me = MPI.COMM_WORLD.Rank();
			
			if(me == 0){
			
				int k = 3;
				
				if(args.length > 0)
					k = new Integer(args[0]);
				
				if(k > 3)
					System.out.println("Número máximo para k é 3. A máquina possui 4 cores e processa um k por core.");
				
				if(k < 2)
					System.out.println("Número mínimo para k é 2.");
				
				System.out.println("k = " + k);
				
				String file = "measure1_smartwatch_sens_min.csv";
				//String file = "W:\\CEFET\\2017.3\\CPD\\trabalhos\\trab_1\\datasets\\teste.csv";
	    	
		    	double[][] data = Util.loadFile(file, true);
		    	
		    	System.out.println("Número de tuplas: " + data.length);
		    	
		    	long start = new Date().getTime();
		    	
		    	new KMeans(k, data).group(me);
		    	
		    	long end = new Date().getTime();
		    	
		    	System.out.println("Tempo (ms): " + (end - start));
		    	
			} else{
				
				while(true){
					
					Object[] sendObj = new Object[3];
					
					MPI.COMM_WORLD.Recv(sendObj, 0, 3, MPI.OBJECT, 0, 10);
					
					sendObj[2] = KMeans.calculateDistanceToCentroid((double[]) sendObj[0], (double[][]) sendObj[1]);
					
					MPI.COMM_WORLD.Isend(sendObj,0,3,MPI.OBJECT,0,10);
					//System.out.println("Enviando distancias do core " + me + " para core 0.");
					
					//MPI.COMM_WORLD.Barrier();
					
				}
				
			}
	    	
	    	MPI.Finalize();
	    	
		} catch(Exception e){
			e.printStackTrace();
		}

	}

}

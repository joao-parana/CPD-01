package br.cefetrj.ppcic.cpd.kmeans.horizontal;

import java.util.Date;
import java.util.List;

import mpi.MPI;

/**
 * 
 * @author rodrigo tavares e joao parana
 *
 */

public class ParallelMain {

public static void main(String[] args) {
		
		try{
			
			MPI.Init(args);
			
			int me = MPI.COMM_WORLD.Rank();
			int qtdCores = MPI.COMM_WORLD.Size();
			
			if(me == 0){
			
				if(args.length <= 0){
					
					System.out.println("Valor de k não fornecido.");
					return;
					
				}
					
				int k = new Integer(args[0]);
				
				System.out.println("k = " + k);
				
				String file = "measure1_smartwatch_sens_6mi.csv";
				
				double[][] data = Util.loadFile(file, true);
				
				List<double[][]> partData = Util.getPartitionedData(data, qtdCores - 1);
		    	
		    	System.out.println("Número de tuplas: " + data.length);
		    	
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
	    	
		} catch(Exception e){
			
			e.printStackTrace();
			
		}
		
	}

}

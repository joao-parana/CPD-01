package br.cefetrj.ppcic.cpd.kmeans.horizontal;

import mpi.MPI;

/**
 * 
 * @author rodrigo tavares e joao parana
 *
 */

public class ParallelKMeans extends SingleKMeans {
	
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

package cpd_kmeans;

import java.util.ArrayList;
import java.util.List;

import mpi.MPI;

public class KMeans {
	
	private int k;
	private double[][] data;
	long[] kQtd;
	
	public KMeans(int k, double[][] data){
		
		this.k = k;
		this.data = data;
		
	}
	
	public void group(int me) {
		
		int m = data.length;
		int n = data[0].length;
		
		long[] lastGroup = new long[k];
		
		double[][] centroids = new double[k][n];
		
		for(int i = 0; i < k; i++)
			centroids[i] = data[i];
		
		long iterations = 0;
		
		while(true){
			
			iterations++;
			
			double[][] centroidsDist = new double[k][m];
			List<List<double[]>> lstGroups = new ArrayList<List<double[]>>();
			
			for(int i = 0; i < centroids.length; i++)
				lstGroups.add(new ArrayList<double[]>());
			
			if(me==0){
				
				Object[] sendObj = new Object[3];
				sendObj[0] = centroids[0];
				sendObj[1] = data;
				
				MPI.COMM_WORLD.Isend(sendObj,0,3,MPI.OBJECT,1,10);
				//System.out.println("Enviando data e centroide para o core 1.");
				
				sendObj[0] = centroids[1];
				
				MPI.COMM_WORLD.Isend(sendObj,0,3,MPI.OBJECT,2,10);
				//System.out.println("Enviando data e centroide para o core 2.");
				
				if(k == 3){
					
					sendObj[0] = centroids[2];
					
					MPI.COMM_WORLD.Isend(sendObj,0,3,MPI.OBJECT,3,10);
					//System.out.println("Enviando data e centroide para o core 3.");
					
				}
				
				//System.out.println("Barrier - Esperando execução nos cores.");
				//MPI.COMM_WORLD.Barrier();
				
				MPI.COMM_WORLD.Recv(sendObj, 0, 3, MPI.OBJECT, 1, 10);
				//System.out.println("Recebendo distancias do core 1");
				
				centroidsDist[0] = (double[]) sendObj[2];
				
				MPI.COMM_WORLD.Recv(sendObj, 0, 3, MPI.OBJECT, 2, 10);
				//System.out.println("Recebendo distancias do core 2");
				
				centroidsDist[1] = (double[]) sendObj[2];
				
				if(k == 3){
					
					MPI.COMM_WORLD.Recv(sendObj, 0, 3, MPI.OBJECT, 3, 10);
					//System.out.println("Recebendo distancias do core 3");
					
					centroidsDist[2] = (double[]) sendObj[2];
					
				}
				
			}
			
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
			
			for(int i = 0; i < lastGroup.length; i++){
				
				if(lastGroup[i] != lstGroups.get(i).size()){
					
					eq = false;
					lastGroup[i] = lstGroups.get(i).size();
					
				}
					
			}
			
			if(eq){
				
				kQtd = lastGroup;
				break;
				
			}
			
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
		
		System.out.println("Número de iterações: " + iterations);
		
		for(int i = 0; i < kQtd.length; i++){
			
			double percent = (new Double(kQtd[i]).doubleValue() * 100.00) / new Double(m).doubleValue();
			System.out.println("Cluster " + i + " possui " + kQtd[i] + " pontos / " + percent + "%");
			
		}
		
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
	
}

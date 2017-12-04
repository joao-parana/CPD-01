package br.cefetrj.ppcic.cpd.kmeans.vertical;

import java.util.ArrayList;
import java.util.List;

public class TesteKMeans {
	
	private int k;
	private double[][] data;
	long[] kQtd;
	
	public TesteKMeans(int k, double[][] data){
		
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
			
			double[][] centroidsDist = new double[k][m];
			List<List<double[]>> lstGroups = new ArrayList<List<double[]>>();
			
			for(int i = 0; i < centroids.length; i++){
				
				lstGroups.add(new ArrayList<double[]>());
				
				centroidsDist[i] = calculateDistanceToCentroid(centroids[i], data);
				
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

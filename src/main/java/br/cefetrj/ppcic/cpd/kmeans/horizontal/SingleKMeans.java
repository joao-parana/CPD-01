package br.cefetrj.ppcic.cpd.kmeans.horizontal;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author rodrigo tavares e joao parana
 *
 */

public class SingleKMeans implements KMeans {
	
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
		
		System.out.println("Número de iterações: " + iterations);
		
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

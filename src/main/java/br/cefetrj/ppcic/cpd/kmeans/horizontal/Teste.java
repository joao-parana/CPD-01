package br.cefetrj.ppcic.cpd.kmeans.horizontal;

import java.util.List;

public class Teste {

	public static void main(String[] args) {
		
		double[][] data = new double[][]{{1, 2}, {2, 2}, {3, 2}};
		
		List<double[][]> partData = Util.getPartitionedData(data, 3);
		
		System.out.println("CPD");
		
	}

}

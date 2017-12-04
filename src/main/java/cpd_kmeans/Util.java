package cpd_kmeans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Util {
	
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
	
	public static <T> byte[] serializeMPJ(T object) throws Exception {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        out = new ObjectOutputStream(bos);
        out.writeObject(object);
        return bos.toByteArray();
		
	}
	
	public static <T> T unserializeMPJ(byte[] byteArr) throws Exception {
		
		ByteArrayInputStream bis = new ByteArrayInputStream(byteArr);
        ObjectInput in = new ObjectInputStream(bis);
        Object obj = in.readObject();
        return (T) obj;
		
	}
	
}

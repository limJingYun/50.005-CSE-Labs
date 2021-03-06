import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.lang.Math;
import java.io.BufferedReader;



public class MedianThread {
	private static final int DATA_SIZE = 1048576;
	public static void main(String[] args) throws InterruptedException, FileNotFoundException  {
		
		// TODO: read data from external file and store it in an array
	       // Note: you should pass the file as a first command line argument at runtime.

		String FileName = String.valueOf(args[0]);
		ArrayList<Integer> integerArray = new ArrayList<Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(FileName));
			String currentLine = br.readLine(); //everything in the txt file is 1 line OH SHIT WADDUP
			String [] stringArray;
			stringArray = currentLine.substring(1).split("\\s+");
			for (String stringNumb : stringArray){
				integerArray.add(Integer.parseInt(stringNumb));
			}
		}catch(Exception e) {
			e.getStackTrace();
		}

		//Collections.sort(integerArray);
		//System.out.println("original median of list is "+computeMedian(integerArray));
	
	// define number of threads
	int NumOfThread = Integer.valueOf(args[1]);// this way, you can pass number of threads as 	
	     // a second command line argument at runtime.
	
	// TODO: partition the array list into N subArrays, where N is the number of threads
	int subArrayListLength = DATA_SIZE/NumOfThread;
	ArrayList<ArrayList<Integer>> arrayOfSubList = new ArrayList<ArrayList<Integer>>();
	ArrayList<MedianMultiThread> arrayOfThread = new ArrayList<MedianMultiThread>();
	int loopsToMerge = (int) (Math.log(NumOfThread)/Math.log(2));

	for (int i=0; i<DATA_SIZE; i+=subArrayListLength){
			ArrayList<Integer> list = new ArrayList<Integer>(integerArray.subList(i,i+subArrayListLength));
			arrayOfSubList.add(list);
	}

	// TODO: start recording time
	long startTime = System.currentTimeMillis();
	// TODO: create N threads and assign subArrays to the threads so that each thread sorts
	    // its repective subarray. For example,
	
	for (ArrayList<Integer> sublist : arrayOfSubList){
			arrayOfThread.add(new MedianMultiThread(sublist));
	}
	//Tip: you can't create big number of threads in the above way. So, create an array list of threads. 
	arrayOfSubList.clear();
	// TODO: start each thread to execute your sorting algorithm defined under the run() method, for example, 
	for (MedianMultiThread thread : arrayOfThread){
			thread.start();
	}

	for (MedianMultiThread thread : arrayOfThread){
		try{
			thread.join();
			arrayOfSubList.add(thread.getInternal());
		} catch (Exception e){
			e.getStackTrace();
		}
	}
	
	// TODO: use any merge algorithm to merge the sorted subarrays and store it to another array, e.g., sortedFullArray. 
	for (int i=0;i<loopsToMerge;i++){
		//instantiate array of threads to sort the merged lists
		arrayOfThread.clear();
		for (int j=0; j<arrayOfSubList.size(); j+=2){
			ArrayList<Integer> mergedSortedArray = new ArrayList<Integer>(arrayOfSubList.get(j));
			mergedSortedArray.addAll(arrayOfSubList.get(j+1));
			arrayOfThread.add(new MedianMultiThread(mergedSortedArray));
		}
		//clear the array of sublists so you can store the new sublists later
		arrayOfSubList.clear();
		//run all threads
		for (MedianMultiThread thread : arrayOfThread){
			thread.start();
		}
		//joing all threads
		for (MedianMultiThread thread : arrayOfThread){
			try{
				thread.join();
				arrayOfSubList.add(thread.getInternal());
			} catch (Exception e){
				e.getStackTrace();
			}
		}
	}
	

	//TODO: get median from sortedFullArray
	ArrayList<Integer> finalSortedArray = new ArrayList<Integer>(arrayOfSubList.get(0));
	double median = computeMedian(finalSortedArray);
	    //e.g, computeMedian(sortedFullArray

	// TODO: stop recording time and compute the elapsed time 
	long endTime = System.currentTimeMillis();
	long runningTime = endTime-startTime;
	// TODO: printout the final sorted array
	System.out.println(finalSortedArray);
	// TODO: printout median
	System.out.println("The Median value is "+median);
	System.out.println("Running time is " + runningTime + " milliseconds\n");	
	}

	public static double computeMedian(ArrayList<Integer> inputArray) {
		int inputArraySize = inputArray.size();
		if (inputArraySize%2==0){
			return (inputArray.get(inputArraySize/2)+inputArray.get(inputArraySize/2+1))/2;
		}
		else{
			System.out.println(inputArray.get(inputArraySize/2+1));
			return (inputArray.get(inputArraySize/2+1));
		}
		
	}
}

// extend Thread
class MedianMultiThread extends Thread {
	private ArrayList<Integer> list;

	public ArrayList<Integer> getInternal() {
		return list;
	}

	MedianMultiThread(ArrayList<Integer> array) {
		this.list = array;
	}

	public void run() {
		mergeSort(list);
	}
	
	// TODO: implement merge sort here, recursive algorithm
	public void mergeSort(ArrayList<Integer> array) {
		Collections.sort(array); 
	}
}
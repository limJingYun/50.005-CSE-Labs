import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

// package Week3;

public class Banker {
	private int numberOfCustomers;	// the number of customers
	private int numberOfResources=0;	// the number of resources

	private int[] available; 	// the available amount of each resource
	private int[][] maximum; 	// the maximum demand of each customer
	private int[][] allocation;	// the amount currently allocated
	private int[][] need;		// the remaining needs of each customer

	/**
	 * Constructor for the Banker class.
	 * @param resources          An array of the available count for each resource.
	 * @param numberOfCustomers  The number of customers.
	 */
	public Banker (int[] resources, int numberOfCustomers) {
		// TODO: set the number of resources
		this.numberOfResources=resources.length;
		// TODO: set the number of customers
		this.numberOfCustomers = numberOfCustomers;
		// TODO: set the value of bank resources to available
		this.available = Arrays.copyOf(resources, resources.length);

		// TODO: set the array size for maximum, allocation, and need
		this.maximum = new int[numberOfCustomers][resources.length];
		this.allocation = new int[numberOfCustomers][resources.length];
		this.need = new int[numberOfCustomers][resources.length];

	}

	/**
	 * Sets the maximum number of demand of each resource for a customer.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param maximumDemand  An array of the maximum demanded count for each resource.
	 */
	public void setMaximumDemand(int customerIndex, int[] maximumDemand) {
		// TODO: add customer, update maximum and need
		this.maximum[customerIndex] = maximumDemand;
		for (int i=0; i<maximumDemand.length; i++){
			this.need[customerIndex][i]=this.maximum[customerIndex][i]-this.allocation[customerIndex][i];
		}

	}

	/**
	 * Prints the current state of the bank.
	 */
	public void printState() {
		// TODO: print available
		System.out.println("Available:");
		String availState = "";
		for (int i : available){
			availState=availState+i+" ";
		}
		System.out.println(availState);
		// TODO: print maximum
		System.out.println("Maximum:");
		for (int i=0;i<maximum.length;i++){
			String maxRowString="";
			for (int j : maximum[i]){
				maxRowString=maxRowString+j+" ";
			}
			System.out.println(maxRowString);
		}
		// TODO: print allocation
		System.out.println("Allocation:");
		for (int i=0;i<allocation.length;i++){
			String allocRowString="";
			for (int j : allocation[i]){
				allocRowString=allocRowString+j+" ";
			}
			System.out.println(allocRowString);
		}
		// TODO: print need
		System.out.println("Need:");
		for (int i=0;i<need.length;i++){
			String needRowString="";
			for (int j : need[i]){
				needRowString=needRowString+j+" ";
			}
			System.out.println(needRowString);
		}
		
	}

	/**
	 * Requests resources for a customer loan.
	 * If the request leave the bank in a safe state, it is carried out.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param request        An array of the requested count for each resource.
	 * @return true if the requested resources can be loaned, else false.
	 */
	public synchronized boolean requestResources(int customerIndex, int[] request) {
		// TODO: print the request
		
		System.out.println("Customer " + customerIndex + " requesting");
		String custReq="";
		for (int i : request){
			custReq=custReq+i+" ";
		}
		System.out.println(custReq);
		// TODO: check if request larger than need
		for (int i=0; i<request.length;i++){
			if (request[i]>need[customerIndex][i]){
				return false;
			}
		}
		// TODO: check if request larger than available
		for (int i=0; i<request.length;i++){
			if (request[i]>available[i]){
				return false;
			}
		}
		// TODO: check if the state is safe or not
		boolean checkSafestate = checkSafe(customerIndex,request);
		if (checkSafestate){
			// TODO: if it is safe, allocate the resources to customer customerNumber
			for (int i=0; i<request.length;i++){
				allocation[customerIndex][i]+=request[i];
				//update need
				need[customerIndex][i]-=request[i];
				//update available
				available[i]-=request[i];
			}
		}
		return true;
	}

	/**
	 * Releases resources borrowed by a customer. Assume release is valid for simplicity.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param release        An array of the release count for each resource.
	 */
	public synchronized void releaseResources(int customerIndex, int[] release) {
		// TODO: print the release
		System.out.println("Customer "+customerIndex+" releasing");
		String custRel="";
		for (int j : release){
			custRel=custRel+j+" ";
		}
		System.out.println(custRel);
		// TODO: release the resources from customer customerNumber
		for (int i=0; i<release.length;i++){
			this.allocation[customerIndex][i]-=release[i];
			this.available[i]+=release[i];
		}
	}

	/**
	 * Checks if the request will leave the bank in a safe state.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param request        An array of the requested count for each resource.
	 * @return true if the requested resources will leave the bank in a
	 *         safe state, else false
	 */
	private synchronized boolean checkSafe(int customerIndex, int[] request) {
		// TODO: check if the state is safe
		int [] temp_avail = new int[request.length];


		for (int i=0; i<request.length;i++){
			temp_avail[i] = available[i]-request[i];
		}
		//initialise temp need and temp alloc
		int [][] temp_need = new int[numberOfCustomers][];
		int [][] temp_alloc = new int[numberOfCustomers][];
		for (int i=0; i<numberOfCustomers;i++){
			temp_need[i] = Arrays.copyOf(need[i],request.length);
			temp_alloc[i] = Arrays.copyOf(allocation[i],request.length);
		}
		//update need
		for (int i=0; i<request.length;i++){
			temp_need[customerIndex][i] = need[customerIndex][i]-request[i];
			temp_alloc[customerIndex][i] = allocation[customerIndex][i] + request[i];
		}
		int [] work = Arrays.copyOf(temp_avail, temp_avail.length);
		boolean[] finish = new boolean[numberOfCustomers];
		Arrays.fill(finish, false);
		boolean possible = true;
		while (possible){
			possible = false;
			for (int c=0; c<numberOfCustomers;c++){
				boolean temp_needLessThanWork = true;
				for (int k=0; k<request.length; k++){
					if (temp_need[c][k]>work[k]){
						temp_needLessThanWork=false;
					}
				}
				if (finish[c]==false && temp_needLessThanWork){
					possible = true;
					for (int a=0; a<request.length; a++){
						work[a]+=temp_alloc[c][a];
					}
					finish[c] = true;
				}
			}
		}
		boolean[] finsihAllTrue = new boolean[numberOfCustomers];
		Arrays.fill(finsihAllTrue,true);
		return (Arrays.equals(finish,finsihAllTrue));
	}

	/**
	 * Parses and runs the file simulating a series of resource request and releases.
	 * Provided for your convenience.
	 * @param filename  The name of the file.
	 */
	public static void runFile(String filename) {

		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(filename));

			String line = null;
			String [] tokens = null;
			int [] resources = null;

			int n, m;

			try {
				n = Integer.parseInt(fileReader.readLine().split(",")[1]);
			} catch (Exception e) {
				System.out.println("Error parsing n on line 1.");
				fileReader.close();
				return;
			}

			try {
				m = Integer.parseInt(fileReader.readLine().split(",")[1]);
			} catch (Exception e) {
				System.out.println("Error parsing n on line 2.");
				fileReader.close();
				return;
			}

			try {
				tokens = fileReader.readLine().split(",")[1].split(" ");
				resources = new int[tokens.length];
				for (int i = 0; i < tokens.length; i++)
					resources[i] = Integer.parseInt(tokens[i]);
			} catch (Exception e) {
				System.out.println("Error parsing resources on line 3.");
				fileReader.close();
				return;
			}

			Banker theBank = new Banker(resources, n);

			int lineNumber = 4;
			while ((line = fileReader.readLine()) != null) {
				tokens = line.split(",");
				if (tokens[0].equals("c")) {
					try {
						int customerIndex = Integer.parseInt(tokens[1]);
						tokens = tokens[2].split(" ");
						resources = new int[tokens.length];
						for (int i = 0; i < tokens.length; i++)
							resources[i] = Integer.parseInt(tokens[i]);
						theBank.setMaximumDemand(customerIndex, resources);
					} catch (Exception e) {
						System.out.println("Error parsing resources on line "+lineNumber+".");
						fileReader.close();
						return;
					}
				} else if (tokens[0].equals("r")) {
					try {
						int customerIndex = Integer.parseInt(tokens[1]);
						tokens = tokens[2].split(" ");
						resources = new int[tokens.length];
						for (int i = 0; i < tokens.length; i++)
							resources[i] = Integer.parseInt(tokens[i]);
						theBank.requestResources(customerIndex, resources);
					} catch (Exception e) {
						System.out.println("Error parsing resources on line "+lineNumber+".");
						fileReader.close();
						return;
					}
				} else if (tokens[0].equals("f")) {
					try {
						int customerIndex = Integer.parseInt(tokens[1]);
						tokens = tokens[2].split(" ");
						resources = new int[tokens.length];
						for (int i = 0; i < tokens.length; i++)
							resources[i] = Integer.parseInt(tokens[i]);
						theBank.releaseResources(customerIndex, resources);
					} catch (Exception e) {
						System.out.println("Error parsing resources on line "+lineNumber+".");
						fileReader.close();
						return;
					}
				} else if (tokens[0].equals("p")) {
					theBank.printState();
				}
			}
			fileReader.close();
		} catch (IOException e) {
			System.out.println("Error opening: "+filename);
		}

	}

	/**
	 * Main function
	 * @param args  The command line arguments
	 */
	public static void main(String [] args) {
		if (args.length > 0) {
			runFile(args[0]);
		}
	}

}
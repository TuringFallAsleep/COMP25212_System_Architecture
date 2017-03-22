import java.io.*;
import java.util.*;

class MemAccess {
  //obtained from trace file by getAccess
  //atype is coded: Read = 0, Write = 1, Fetch = 2;
    int atype;
    int addr;
}

public class CacheSim {

    static BufferedReader in;
    final static int Read = 0, Write = 1, Fetch = 2;
    static int inst_fetches;
    static int csize,lsize; //total cache size and line size in bytes

    public static void main(String args[]){
        try {
	    in = new BufferedReader(new FileReader(args[0]));
            in.mark(1000000);//needed to repeatedly read data file
	    csize =32; //small cache for initial tests
            lsize =8;
	    System.out.println("Fully Associative Cache");
	    Cache UnifiedCache = new AssocCache(csize,lsize);
	    doSimul(UnifiedCache,UnifiedCache,true);
	    
	    csize =8192; //larger cache for real experiments
            lsize =32;
	    System.out.println("Fully Associative Cache");
	    UnifiedCache = new AssocCache(csize,lsize);
	    doSimul(UnifiedCache,UnifiedCache,true);

	    } catch (FileNotFoundException e) {
            System.out.println("File "+args[0]+" Not Found");
	} catch (IOException ioe) {
            System.out.println("File Mark Failed");
	}
    }

    private static void doSimul(Cache DataCache, Cache InstructionCache, boolean printing) {
	//For Unified cache make DataCache and InstructionCache the same
        //If printing is true statistics are printed
        System.out.println("Cache Size = "+csize+" Line Size = "+lsize);
	inst_fetches = 0;
	try{
	    in.reset(); //read complete data file from start for each call of doSimul
	} catch (IOException ioe) {
            System.out.println("File Reset Failed");
            return;
	}
        try {
	    while(true) {
		MemAccess access = getAccess();
		if (access.atype == Read){
		    //  System.out.println(Int.toHexString(access.addr)+" Read");
		    DataCache.read(access.addr);
		}
		else if (access.atype == Write){
		    //  System.out.println(Int.toHexString(access.addr)+" Write");
		    DataCache.write(access.addr,new Integer(0));
		}
		else if (access.atype == Fetch){
		    inst_fetches++;
		    //  System.out.println(Int.toHexString(access.addr)+" Fetch");
		    InstructionCache.read(access.addr);
		}
		else System.out.println("Unknown Access Type");
	    }
	} catch (NullPointerException e) {
	   //end of trace file and simulation run 
	   if (printing) {  
	    	System.out.println("Trace Input Ended");
	    	if (InstructionCache == DataCache){
			System.out.println("\nUnified Cache Statistics\n");
			InstructionCache.dumpStats();
	    	}
	    	else {
			System.out.println("\nInstruction Cache Statistics\n");
			InstructionCache.dumpStats();
			System.out.println("Data Cache Statistics\n");
			DataCache.dumpStats();
	    	}
		
	      }
	  }
    }

    private static MemAccess getAccess() throws NullPointerException {
	// return values from next line of trace file or null at end of file
       try {
	    MemAccess res = new MemAccess();
	    StringTokenizer st = new StringTokenizer(in.readLine());
	    res.atype = Integer.valueOf(st.nextToken()).intValue();
	    res.addr = Integer.valueOf(st.nextToken(),16).intValue();
	    return res;
	} catch (IOException e) {
	    System.out.println("An unexpected I/O exception occured");
	    return null;
	}
    }

    private static int memAccessCost(int lsize){
        //needed for Ex5 currently does nothing
	return 0;
    }
}

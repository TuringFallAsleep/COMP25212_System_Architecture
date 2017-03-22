

public class AssocCache extends Cache {
	private int lnum;
	private int clnum = 0;
	private int[] tag;
	private boolean[] dirtyBit;
	private Object[] data;
	private int currentIndex = 0;
	
	

	public AssocCache(int csize, int lsize) {
		size = csize;
		line_size = lsize;
		lnum = size/lsize;
		tag = new int[lnum]; // lnum is number of lines
		dirtyBit = new boolean[lnum];
		data = new Object[lnum];
		for (int i =0; i<lnum; i++){
			dirtyBit[i] = false;
			// The valid bit indicates whether or not
			//a cache block has been loaded with valid data.
		}
	}

	@Override
	public Object cacheSearch(int addr) {

//		int i = addr % size / line_size;
//		i = i % lnum;
		for (int i = 0; i < lnum; i++) {
			if ((dirtyBit[i]) && ((tag[i]) == (addr/line_size))){
				currentIndex = i;
				return data[i];
			}
		}		
		return null;
	}

	@Override
	public oldCacheLineInfo cacheNewEntry(int addr) {

		oldCacheLineInfo oldInfo = new oldCacheLineInfo();
	
		if(clnum<lnum){
//			System.out.println("current line num: "+clnum+" lnum: "+ lnum);
			
			oldInfo.old_valid = dirtyBit[clnum];
			oldInfo.data = data[clnum] = new Integer(-1);
			for (int i = 0; i < lnum; i++) {
				if (tag[i]==addr/line_size) {
					System.out.println("hit: "+i);
					clnum++;
					return oldInfo;
				}
			}
			
			tag[clnum] = addr/line_size;
			dirtyBit[clnum] = true;
			
			clnum++;
		}else{
			clnum = 0;
//			System.out.println("current line num: "+clnum+" lnum: "+ lnum);
			oldInfo.old_valid = dirtyBit[clnum];
			oldInfo.data = data[clnum] = new Integer(-1);
			for (int i = 0; i < lnum; i++) {
				if (tag[i]==addr/line_size) {
					clnum++;
					return oldInfo;
				}
			}			
			tag[clnum] = addr/line_size;
			dirtyBit[clnum] = true;			
			clnum++;
		}		
		return oldInfo;
	}

	@Override
	public void cacheWriteData(Object entry) {
		data[currentIndex] = entry;
	}

}

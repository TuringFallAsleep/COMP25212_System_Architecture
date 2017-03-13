

public class DirectMappedCache extends Cache {
	private int lnum;
	private int[] tag;
	private boolean[] dirtyBit;
	private Object[] data;
	private int currentIndex = 0;
	
	

	public DirectMappedCache(int csize, int lsize) {
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
//		for (int i = 0; i<lnum; i++){
//			if(addr == tag[i]){
//				currentIndex = i;
//				return data[i];
//			}
//		}
		int i = addr % size / line_size;
		i = i % lnum;
		if ((dirtyBit[i]) && ((tag[i]) == (addr/line_size))){
			currentIndex = i;
			return data[i];
		}
		return null;
	}

	@Override
	public oldCacheLineInfo cacheNewEntry(int addr) {
		int i = addr % size / line_size;
		i = i % lnum;
		oldCacheLineInfo oldInfo = new oldCacheLineInfo();
		
		oldInfo.old_valid = dirtyBit[i];
		
		tag[i] = addr/line_size;
		dirtyBit[i] = true;
		oldInfo.data = data[i] = new Integer(-1);		
		
		return oldInfo;
	}

	@Override
	public void cacheWriteData(Object entry) {
		data[currentIndex] = entry;
	}

}

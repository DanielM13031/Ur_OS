/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;

/**
 *
 * @author super
 */
public class BestFitMemorySlotManager extends FreeMemorySlotManager{
    
    public BestFitMemorySlotManager(int memSize){
        super(memSize);
    }
    
    
    @Override
    public MemorySlot getSlot(int size) {
        MemorySlot m = null;
        if(list.isEmpty()){
            System.out.println("Error - Memory is empty");
            return null;
        }
        // find the smallest slot that fits
        int bestIndex = -1;
        int bestSize = Integer.MAX_VALUE;
        for(int i=0;i<list.size();i++){
            MemorySlot s = list.get(i);
            if(s.canContain(size)){
                int sSize = s.getSize();
                if(sSize < bestSize){
                    bestSize = sSize;
                    bestIndex = i;
                }
            }
        }
        if(bestIndex == -1){
            System.out.println("Error - Memory cannot allocate a slot big enough for the requested memory");
            return null;
        }
        MemorySlot s = list.get(bestIndex);
        if(s.getSize() == size){
            m = s;
            list.remove(bestIndex);
            return m;
        }else{
            m = s.assignMemory(size);
            return m;
        }
    }

    
}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;

/**
 *
 * @author super
 */
public class WorstFitMemorySlotManager extends FreeMemorySlotManager{
    
    public WorstFitMemorySlotManager(int memSize){
        super(memSize);
    }
    
    
    @Override
    public MemorySlot getSlot(int size) {
        MemorySlot m = null;
        if(list.isEmpty()){
            System.out.println("Error - Memory is empty");
            return null;
        }
        int worstIndex = -1;
        int worstSize = -1;
        for(int i=0;i<list.size();i++){
            MemorySlot s = list.get(i);
            if(s.canContain(size)){
                int sSize = s.getSize();
                if(sSize > worstSize){
                    worstSize = sSize;
                    worstIndex = i;
                }
            }
        }
        if(worstIndex == -1){
            System.out.println("Error - Memory cannot allocate a slot big enough for the requested memory");
            return null;
        }
        MemorySlot s = list.get(worstIndex);
        if(s.getSize() == size){
            m = s;
            list.remove(worstIndex);
            return m;
        }else{
            m = s.assignMemory(size);
            return m;
        }
    }

    
}

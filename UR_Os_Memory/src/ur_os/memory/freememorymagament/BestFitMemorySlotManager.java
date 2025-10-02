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
        MemorySlot chosen = null;
        int bestSize = Integer.MAX_VALUE;

        for (MemorySlot s : list) {
            if (s.getSize() >= size && s.getSize() < bestSize) {
                chosen = s;
                bestSize = s.getSize();
            }
        }

        if (chosen != null) {
            System.out.println("[BEST] request=" + size + " -> chosen " + chosen);
            return chosen.assignMemory(size);
        }

        System.out.println("[BEST] request=" + size + " -> NO FIT");
        return null;
    }
}

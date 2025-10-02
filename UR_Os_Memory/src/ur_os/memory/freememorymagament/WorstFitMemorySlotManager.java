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
        MemorySlot chosen = null;
        int worstSize = Integer.MIN_VALUE;

        for (MemorySlot s : list) {
            if (s.getSize() >= size && s.getSize() > worstSize) {
                chosen = s;
                worstSize = s.getSize();
            }
        }

        if (chosen != null) {
            System.out.println("[WORST] request=" + size + " -> chosen " + chosen);
            return chosen.assignMemory(size);
        }

        System.out.println("[WORST] request=" + size + " -> NO FIT");
        return null;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;

/**
 *
 * @author super
 */
public class WorstFitMemorySlotManager extends FreeMemorySlotManager {

    public WorstFitMemorySlotManager(int memSize){
        super(memSize);
    }

    @Override
    public MemorySlot getSlot(int size) {
        MemorySlot chosen = null;
        int worstSize = Integer.MIN_VALUE;

        // Recorre la lista de huecos y elige el más grande que alcance
        for (MemorySlot s : list) {
            if (s.getSize() >= size && s.getSize() > worstSize) {
                chosen = s;
                worstSize = s.getSize();
            }
        }

        if (chosen != null) {
            // Asigna 'size' desde el slot elegido (divide el hueco)
            return chosen.assignMemory(size);
        }

        // Si no hay hueco suficiente
        System.out.println("Error: Memory cannot allocate a slot big enough for the requested memory");
        return null;
    }
}

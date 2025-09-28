/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;

/**
 *
 * @author super
 */
public class BestFitMemorySlotManager extends FreeMemorySlotManager {

    public BestFitMemorySlotManager(int memSize){
        super(memSize);
    }

    @Override
    public MemorySlot getSlot(int size) {
        MemorySlot chosen = null;
        int bestSize = Integer.MAX_VALUE;

        // Recorre la lista de huecos y elige el más pequeño que alcance
        for (MemorySlot s : list) {
            if (s.getSize() >= size && s.getSize() < bestSize) {
                chosen = s;
                bestSize = s.getSize();
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

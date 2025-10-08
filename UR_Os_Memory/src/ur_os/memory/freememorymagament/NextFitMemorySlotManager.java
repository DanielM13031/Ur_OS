/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;


/**
 *
 * @author super
 */
public class NextFitMemorySlotManager extends FreeMemorySlotManager {

    private int lastIndex;

    public NextFitMemorySlotManager(int memSize) {
        super(memSize);
        this.lastIndex = 0;
    }

    @Override
    public MemorySlot getSlot(int size) {
        if (size <= 0) {
            System.out.println("Error - Requested size must be > 0");
            return null;
        }
        if (list.isEmpty()) {
            System.out.println("Error - Memory is empty");
            return null;
        }

        final int n = list.size();
        int idx = lastIndex % n;
        int visited = 0;

        // LOG opcional:
        //System.out.println("[NextFit] start=" + idx + " freeCount=" + n + " request=" + size);

        while (visited < n) {
            MemorySlot s = list.get(idx);
            if (s.canContain(size)) {

                MemorySlot m;
                if (s.getSize() == size) {
                    m = s;               
                    list.remove(idx);
                    if (list.isEmpty()) {
                        lastIndex = 0;
                    } else {
                        lastIndex = idx % list.size();
                    }
                } else {
                    m = s.assignMemory(size); // parte el hueco
                    lastIndex = idx;          // siguiente búsqueda arranca desde aquí
                }
                // LOG opcional:
                //System.out.println("[NextFit] chosenIdx=" + idx + " slotBase=" + m.getBase() + " slotSize=" + m.getSize());
                return m;
            }
            idx = (idx + 1) % n;
            visited++;
        }

        System.out.println("Error - Memory cannot allocate a slot big enough for the requested memory");
        return null;
    }
}

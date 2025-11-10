/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author user
 */
public class PVMM_FIFO extends ProcessVirtualMemoryManager {

    public PVMM_FIFO() {
        type = ProcessVirtualMemoryManagerType.FIFO;
    }

    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, int loaded) {

        // 1. Determinar qué páginas están actualmente en memoria 
        Set<Integer> residentPages = new HashSet<>();
        int size = memoryAccesses.size() - 1;

        while (size >= 0 && residentPages.size() < loaded) {
            int currentPage = memoryAccesses.get(size);
            if (!residentPages.contains(currentPage)) {
                residentPages.add(currentPage);
            }
            size--;
        }
        
        if (residentPages.isEmpty()) {
            return -1;
        }

        // 2. Encontrar la página que fue cargada primero 
        for (Integer page : memoryAccesses) {
            if (residentPages.contains(page)) {
                return page;
            }
        }

        return -1;
    }
}



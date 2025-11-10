/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;

/**
 *
 * @author user
 */
public class PVMM_LRU extends ProcessVirtualMemoryManager{

    public PVMM_LRU(){
        type = ProcessVirtualMemoryManagerType.LRU;
    }
    
    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, int loaded) {
        if (memoryAccesses == null || memoryAccesses.isEmpty() || loaded <= 0) return -1;

        final int curr = memoryAccesses.size() - 1; 
        final int end  = curr - 1;                   
        if (end < 0) return -1;

        java.util.LinkedList<Integer> distinct = new java.util.LinkedList<>();
        java.util.HashSet<Integer> seen = new java.util.HashSet<>();

        for (int i = end; i >= 0 && distinct.size() < loaded; i--) {
            int p = memoryAccesses.get(i);
            if (seen.add(p)) distinct.add(p);
        }
        if (distinct.size() < loaded) return -1;

        return distinct.get(loaded - 1);
    }
}

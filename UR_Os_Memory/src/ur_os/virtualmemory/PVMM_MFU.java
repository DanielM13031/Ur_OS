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
public class PVMM_MFU extends ProcessVirtualMemoryManager{

    public PVMM_MFU(){
        type = ProcessVirtualMemoryManagerType.MFU;
    }
    
    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, int loaded) {
        if (memoryAccesses == null || memoryAccesses.isEmpty() || loaded <= 0) {
            return -1;
        }

        // Conjunto residente actual
        LinkedList<Integer> resident = new LinkedList<>();
        for (int i = memoryAccesses.size() - 1; i >= 0 && resident.size() < loaded; i--) {
            Integer p = memoryAccesses.get(i);
            if (!resident.contains(p)) {
                resident.add(p);
            }
        }
        if (resident.isEmpty()) return -1;

        // Frecuencias
        java.util.HashMap<Integer, Integer> freq = new java.util.HashMap<>();
        for (Integer p : memoryAccesses) {
            freq.put(p, freq.getOrDefault(p, 0) + 1);
        }

        // Víctima MFU con desempate por LRU (menos reciente entre empatadas)
        Integer victim = null;
        int bestFreq = Integer.MIN_VALUE;
        int bestLastUse = Integer.MAX_VALUE; // preferimos la menos reciente en empate

        for (Integer p : resident) {
            int f = freq.getOrDefault(p, 0);
            int lastUse = memoryAccesses.lastIndexOf(p);
            if (f > bestFreq || (f == bestFreq && lastUse < bestLastUse)) {
                bestFreq = f;
                bestLastUse = lastUse;
                victim = p;
            }
        }
        return victim != null ? victim : -1;
    }
}
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
public class PVMM_LFU extends ProcessVirtualMemoryManager{

    public PVMM_LFU(){
        type = ProcessVirtualMemoryManagerType.LFU;
    }
    
    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, int loaded) {
        if (memoryAccesses == null || memoryAccesses.isEmpty() || loaded <= 0) {
            return -1;
        }

        // Reconstruir conjunto residente: últimas 'loaded' páginas distintas desde el final
        LinkedList<Integer> resident = new LinkedList<>();
        for (int i = memoryAccesses.size() - 1; i >= 0 && resident.size() < loaded; i--) {
            Integer p = memoryAccesses.get(i);
            if (!resident.contains(p)) {
                resident.add(p); // orden: más reciente al menos reciente 
            }
        }
        if (resident.isEmpty()) return -1;

        // 2) Frecuencias
        java.util.HashMap<Integer, Integer> freq = new java.util.HashMap<>();
        for (Integer p : memoryAccesses) {
            freq.put(p, freq.getOrDefault(p, 0) + 1);
        }

        // 3) Elegir víctima LFU con desempate por LRU (menos reciente entre empatadas)
        Integer victim = null;
        int bestFreq = Integer.MAX_VALUE;
        int bestLastUse = Integer.MAX_VALUE; // menor índice = menos reciente

        for (Integer p : resident) {
            int f = freq.getOrDefault(p, 0);
            int lastUse = memoryAccesses.lastIndexOf(p);
            if (f < bestFreq || (f == bestFreq && lastUse < bestLastUse)) {
                bestFreq = f;
                bestLastUse = lastUse;
                victim = p;
            }
        }
        return victim != null ? victim : -1;
    }

}

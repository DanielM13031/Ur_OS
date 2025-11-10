/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;

/**
 *
 * @author kyuuungji
 */
public class PVMM_MRU extends ProcessVirtualMemoryManager {

    public PVMM_MRU() { 
        type = ProcessVirtualMemoryManagerType.MRU; // definimos el tipo de reemplazo como MRU
    }
    
    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, int loaded) {
        
        LinkedList<Integer> pages = new LinkedList(); // guarda las páginas más recientemente usadas
        int size = memoryAccesses.size() - 1; // empieza desde el acceso más reciente
        
        while (size >= 0 && pages.size() < loaded) { // recorre los accesos recientes hasta llenar las cargadas
            if (!pages.contains(memoryAccesses.get(size))) { // si la página no está ya registrada
                pages.add(memoryAccesses.get(size)); // agrega a la lista
            }
            size--; // retrocede en la lista de accesos
        }
        
        return pages.getFirst(); // devuelve la página más recientemente usada (la primera)
    }
    
}

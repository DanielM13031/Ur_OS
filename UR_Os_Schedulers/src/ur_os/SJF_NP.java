/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ur_os;

import java.util.*;

public class SJF_NP extends Scheduler {
    private int currentTime = 0;
    private List<Process> readyQueue = new ArrayList<>();

    SJF_NP(OS os) {
        super(os);
    }

    @Override
    public void getNext(boolean cpuEmpty) {
        if (!cpuEmpty) return; // Si la CPU está ocupada, no hacer nada

        // Mover procesos listos a la Ready Queue
        Iterator<Process> it = processes.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (p.getTime_init() <= currentTime) {
                readyQueue.add(p);
                it.remove();
            }
        }
        // Si no hay procesos listos, la CPU queda inactiva
        if (readyQueue.isEmpty()) {
            currentTime++;
            return;
        }
        // Buscar el proceso con menor tiempo de ráfaga 
        Process min_ciclo = readyQueue.get(0);
        for (Process p : readyQueue) {
            if (p.getRemainingTimeInCurrentBurst() < min_ciclo.getRemainingTimeInCurrentBurst()) {
                min_ciclo = p;
            } else if (p.getRemainingTimeInCurrentBurst() == min_ciclo.getRemainingTimeInCurrentBurst()) {
                min_ciclo = tieBreaker(min_ciclo, p);
            }
        }

        // Ejecutar el proceso seleccionado
        readyQueue.remove(min_ciclo);
        os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, min_ciclo);
        currentTime += min_ciclo.getBurstTime(); // Avanzar el tiempo
    }

    @Override
    public void newProcess(boolean cpuEmpty) {} // No-preemptivo

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} // No-preemptivo
}


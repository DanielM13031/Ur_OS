/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

import java.util.*;

public class HRRN_NP extends Scheduler {

    private int currentTime = 0;
    private final List<Process> readyQueue = new ArrayList<>();

    public HRRN_NP(OS os) {
        super(os);
    }

    @Override
    public void getNext(boolean cpuEmpty) {
        if (!cpuEmpty) return; // No expropiativo: si CPU está ocupada, no hago nada

        // 1) Mover procesos que ya llegaron a la readyQueue
        Iterator<Process> it = processes.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (p.getTime_init() <= currentTime) {
                readyQueue.add(p);
                it.remove();
            }
        }

        // 2) Si no hay listos, avanza el reloj
        if (readyQueue.isEmpty()) {
            currentTime++;
            return;
        }

        // 3) Elegir el proceso con MAYOR Response Ratio
        Process best = null;
        double bestRR = -1.0;

        for (Process p : readyQueue) {
            int burst = nextCpuBurst(p);
            int waiting = currentTime - p.getTime_init();
            double rr = (burst == 0) ? Double.MAX_VALUE
                                     : (waiting + (double)burst) / burst;

            if (rr > bestRR || (rr == bestRR && tieBreak(p, best))) {
                bestRR = rr;
                best = p;
            }
        }

        // 4) Ejecutar el proceso seleccionado (CPU burst completo)
        if (best != null) {
            readyQueue.remove(best);
            int burst = nextCpuBurst(best);
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, best);
            currentTime += burst;  // avanzar el reloj
            
            
            
            /*int burst = nextCpuBurst(best);

            dispatch(best);         // pasa el proceso a CPU
            runFor(best, burst);    // simula su ejecución
            currentTime += burst;

            onProcessCpuBurstFinished(best); // notifica que terminó su burst*/
        }
    }

    @Override
    public void newProcess(boolean cpuEmpty) {
        // HRRN no es preventivo 
    }

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
        // HRRN no es preventivo 
    }

    // ---------- Helpers ----------
    private int nextCpuBurst(Process p) {
        // Usa el tiempo de ráfaga que expone Process
        return p.getBurstTime();
    }


    private void runFor(Process p, int burst) {
        
    }

    private void onProcessCpuBurstFinished(Process p) {
      
    }

    private boolean tieBreak(Process a, Process b) {
        if (b == null) return true;
        return a.getTime_init() < b.getTime_init(); // más antiguo primero
    }
}

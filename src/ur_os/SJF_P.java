/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author prestamour
 */
public class SJF_P extends Scheduler{

    
    SJF_P(OS os){
        super(os);
    }
    
    @Override
    public void newProcess(boolean cpuEmpty){// When a NEW process enters the queue, process in CPU, if any, is extracted to compete with the rest
        
    } 

    @Override
    public void IOReturningProcess(boolean cpuEmpty){// When a process return from IO and enters the queue, process in CPU, if any, is extracted to compete with the rest
        
    } 
    

    @Override
    public void getNext(boolean cpuEmpty) {
        // Selección por menor tiempo(Funcion: select())
        if (this.isEmpty()) return;

        Process best = select();
        if (best == null) return;

        Process running = os.getProcessInCPU();
        //Despachar el mejor si CPU está vacía
        if (running == null) {
            processes.remove(best);
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, best);
            return;
        }

        int runningRem = running.getRemainingTimeInCurrentBurst();
        int bestRem    = best.getRemainingTimeInCurrentBurst();

        if (bestRem < runningRem) {
            // Apropiacion
            os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, running);
            processes.remove(best);
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, best);
        }
    }

    private Process select() {
        Process best = null;
        for (Process p : this.processes) {
            // Solo considerar procesos cuya ráfaga actual sea de CPU
            if (!p.isCurrentBurstCPU()) continue;

            if (best == null) {
                best = p;
            } else {
                int r1 = p.getRemainingTimeInCurrentBurst();
                int r2 = best.getRemainingTimeInCurrentBurst();
                if (r1 < r2) {
                    best = p;
                } else if (r1 == r2) {
                    // Si hay empate aplicar tie-breaker configurado
                    best = tieBreaker(best, p);
                }
            }
        }
        return best;
    }
}




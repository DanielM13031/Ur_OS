/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

import java.util.HashMap;
import java.util.Iterator;

public class HRRN_NP extends Scheduler {

    // HashMap para registrar el tiempo de llegada de cada proceso a la cola de listos.
    // La clave es el PID del proceso (Integer) y el valor es el tiempo (Integer).
    private final HashMap<Integer, Integer> arrivalTimes = new HashMap<>();

    public HRRN_NP(OS os) {
        super(os);
    }

    /**
     * Sobrescribimos addProcess para registrar el tiempo de llegada del proceso
     * a la cola de listos.
     */
    @Override
    public void addProcess(Process p) {
        // Registramos el tiempo actual del sistema como el tiempo de llegada a la cola de listos.
        arrivalTimes.put(p.getPid(), os.system.getTime());
        super.addProcess(p); // Llamamos al método de la clase base para añadir el proceso.
    }

    @Override
    public void getNext(boolean cpuEmpty) {
        // El planificador solo actúa si la CPU está libre y hay procesos en la cola.
        if (!cpuEmpty || processes.isEmpty()) {
            return;
        }

        Process bestProcess = null;
        double highestRR = -1.0;
        int currentTime = os.system.getTime(); // Obtenemos el tiempo actual del reloj del sistema.

        // Iteramos sobre los procesos para encontrar el que tenga el mayor Response Ratio.
        for (Process p : processes) {
            // Obtenemos el tiempo de llegada de nuestro HashMap.
            int arrivalTime = arrivalTimes.getOrDefault(p.getPid(), p.getTime_init());
            
            // Calculamos el tiempo de espera.
            int waitingTime = currentTime - arrivalTime;
            
            // Obtenemos la duración de la siguiente ráfaga de CPU.
            int burstTime = p.getRemainingTimeInCurrentBurst();

            // Evitamos la división por cero si un proceso tiene una ráfaga de 0.
            if (burstTime == 0) continue;

            // Calculamos el Response Ratio (RR).
            double responseRatio = (double) (waitingTime + burstTime) / burstTime;

            if (bestProcess == null || responseRatio > highestRR) {
                highestRR = responseRatio;
                bestProcess = p;
            } else if (responseRatio == highestRR) {
                // Si hay un empate, utilizamos el tie-breaker.
                bestProcess = tieBreaker(bestProcess, p);
            }
        }

        // Si hemos seleccionado un proceso, lo enviamos a la CPU.
        if (bestProcess != null) {
            // Lo eliminamos de la lista de procesos y de nuestro registro de tiempos.
            processes.remove(bestProcess);
            arrivalTimes.remove(bestProcess.getPid());
            
            // Enviamos la interrupción al sistema operativo para que lo despache a la CPU.
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, bestProcess);
        }
    }

    @Override
    public void newProcess(boolean cpuEmpty) {
        // HRRN no es pre-emptivo, así que no se necesita ninguna acción aquí.
    }

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
        // HRRN no es pre-emptivo, no se necesita ninguna acción aquí.
    }
}
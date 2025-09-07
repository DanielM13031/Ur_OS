/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

/**
 *
 * @author prestamour
 */
public class RoundRobin extends Scheduler{

    int q;
    int cont;
    boolean multiqueue;
    
    RoundRobin(OS os){
        super(os);
        q = 5;
        cont=0;
    }
    
    RoundRobin(OS os, int q){
        this(os);
        this.q = q;
    }

    RoundRobin(OS os, int q, boolean multiqueue){
        this(os);
        this.q = q;
        this.multiqueue = multiqueue;
    }
    
    void resetCounter(){
        cont=0;
    }
   
    @Override
    public void getNext(boolean cpuEmpty) {
        // Si la CPU no está vacía, verificamos si el quantum ha expirado.
        if (!cpuEmpty) {
            cont++;
            if (cont >= q) {
                resetCounter();
                os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);
                // La interrupción dejó la CPU vacía, así que actualizamos el estado para la siguiente comprobación.
                cpuEmpty = true;
            }
        }

        // Si la CPU está vacía (ya sea porque lo estaba antes o porque la acabamos de vaciar)
        // y hay procesos en espera, cargamos el siguiente.
        if (cpuEmpty && !processes.isEmpty()) {
            Process p = processes.pop();
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, p);
            // Reiniciamos el contador para el nuevo proceso que acaba de entrar.
            resetCounter();
        }
    }
    
    
    @Override
    public void newProcess(boolean cpuEmpty) {} //Non-preemtive in this event

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //Non-preemtive in this event
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

import java.util.ArrayList;
import java.util.Arrays;


/**
 *
 * @author prestamour
 */
public class PriorityQueue extends Scheduler{

    int currentScheduler;
    
    private ArrayList<Scheduler> schedulers;
    
    PriorityQueue(OS os){
        super(os);
        currentScheduler = -1;
        schedulers = new ArrayList();
    }
    
    PriorityQueue(OS os, Scheduler... s){ //Received multiple arrays
        this(os);
        schedulers.addAll(Arrays.asList(s));
        if(s.length > 0)
            currentScheduler = 0;
    }
    
    
    @Override
    public void addProcess(Process p){
        int priority = p.getPriority();
        
        if (priority >= 0 && priority < schedulers.size()) {
            schedulers.get(priority).addProcess(p);
        }
    }

    void defineCurrentScheduler(){
        for (int i = 0; i < schedulers.size(); i++) {
            if (!schedulers.get(i).isEmpty()) {
                currentScheduler = i;
                break;
            }
        }
    }

    @Override
    public void getNext(boolean cpuEmpty) {
        defineCurrentScheduler();
        if(currentScheduler == -1){
            return;
        }else{
            if (cpuEmpty) {
                schedulers.get(currentScheduler).update();
                return;
            }else{
                int runningPriority = os.getProcessInCPU().getPriority();
                if(runningPriority > currentScheduler){
                    os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);
                    schedulers.get(currentScheduler).update();
                }else{
                    schedulers.get(runningPriority).update();
                }
            }
        }
    }
    
    @Override
    public void newProcess(boolean cpuEmpty) {} //Non-preemtive in this event

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //Non-preemtive in this event
    
}

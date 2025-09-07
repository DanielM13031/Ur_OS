package ur_os;

import java.util.*;
import java.util.ArrayDeque;

public class MFQ extends Scheduler {

    private ArrayList<Scheduler> queues;
    private ArrayList<ArrayDeque<Process>> levels;
    private HashMap<Integer,Integer> levelByPid;
    private int[] quanta;
    private int currentScheduler;
    private int tickCounter;
    private Integer runningPid;
    private boolean ready;

    public MFQ(OS os) {
        super(os);
        queues = new ArrayList<>();
    }

    public MFQ(OS os, Scheduler... schedulers) {
        this(os);
        queues.addAll(Arrays.asList(schedulers));
    }

    private void ensureInit(){
        if(ready) return;
        int n = queues.size();
        levels = new ArrayList<>();
        for(int i=0;i<n;i++) levels.add(new ArrayDeque<>());
        quanta = new int[n];
        for(int i=0;i<n;i++){
            Scheduler s = queues.get(i);
            quanta[i] = (s instanceof RoundRobin) ? ((RoundRobin)s).q : Integer.MAX_VALUE;
        }
        levelByPid = new HashMap<>();
        currentScheduler = -1;
        tickCounter = 0;
        runningPid = null;
        ready = true;
    }

    @Override
    public void addProcess(Process p){
        ensureInit();
        int pid = p.getPid();
        Integer known = levelByPid.get(pid);
        if(runningPid != null && pid == runningPid){
            int from = (known == null ? 0 : known);
            int to = Math.min(from + 1, levels.size() - 1);
            levelByPid.put(pid, to);
            levels.get(to).offerLast(p);
        }else{
            levelByPid.put(pid, 0);
            levels.get(0).offerLast(p);
        }
    }

    void defineCurrentScheduler(){
        ensureInit();
        currentScheduler = -1;
        for(int i=0;i<levels.size();i++){
            if(!levels.get(i).isEmpty()){ currentScheduler = i; break; }
        }
    }

    @Override
    public void getNext(boolean cpuEmpty) {
        ensureInit();
        if(!cpuEmpty){
            Process inCPU = os.getProcessInCPU();
            if(inCPU == null){
                runningPid = null;
                tickCounter = 0;
                defineCurrentScheduler();
                if(currentScheduler == -1) return;
                ArrayDeque<Process> rq = levels.get(currentScheduler);
                if(!rq.isEmpty()){
                    Process p = rq.pollFirst();
                    runningPid = p.getPid();
                    tickCounter = 0;
                    os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, p);
                }
                return;
            }
            Integer lvl = levelByPid.get(inCPU.getPid());
            if(lvl == null){
                if(currentScheduler < 0) currentScheduler = 0;
                levelByPid.put(inCPU.getPid(), currentScheduler);
            }else currentScheduler = lvl;
            runningPid = inCPU.getPid();
            tickCounter++;
            int q = quanta[currentScheduler];
            if(q != Integer.MAX_VALUE && tickCounter >= q){
                os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);
                runningPid = null;
                tickCounter = 0;
                defineCurrentScheduler();
                if(currentScheduler == -1) return;
                ArrayDeque<Process> rq = levels.get(currentScheduler);
                if(!rq.isEmpty()){
                    Process np = rq.pollFirst();
                    runningPid = np.getPid();
                    tickCounter = 0;
                    os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, np);
                }
            }
            return;
        }
        defineCurrentScheduler();
        if(currentScheduler == -1){ runningPid = null; tickCounter = 0; return; }
        ArrayDeque<Process> q = levels.get(currentScheduler);
        if(!q.isEmpty()){
            Process p = q.pollFirst();
            runningPid = p.getPid();
            tickCounter = 0;
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, p);
        }
    }

    @Override public void newProcess(boolean cpuEmpty) {}
    @Override public void IOReturningProcess(boolean cpuEmpty) {}
}

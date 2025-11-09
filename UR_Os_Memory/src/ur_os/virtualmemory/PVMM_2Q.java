package ur_os.virtualmemory;

import java.util.*;

/**
 * PVMM_2Q
 * Victim Rule:
 * - If A1 is not empty -> Victim = A1 head (FIFO).
 * - If A1 empty -> Victim = LRU.
 */
public class PVMM_2Q extends ProcessVirtualMemoryManager {

    private final double a1Ratio;

    public PVMM_2Q() {
        this(0.25);
        type = ProcessVirtualMemoryManagerType.valueOf("TWO_Q");
    }

    public PVMM_2Q(double a1Ratio) {
        this.a1Ratio = Math.max(0.05, Math.min(0.8, a1Ratio));
        type = ProcessVirtualMemoryManagerType.valueOf("TWO_Q");
    }

    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, int loaded) {
        if (loaded <= 0 || memoryAccesses == null || memoryAccesses.isEmpty()) return -1;

        final int capA1 = Math.max(1, Math.min(loaded - 1, (int) Math.floor(loaded * a1Ratio)));
        final int capAm = Math.max(1, loaded - capA1);

        final ArrayDeque<Integer> A1 = new ArrayDeque<>();
        final HashSet<Integer> inA1 = new HashSet<>();

        final LinkedHashSet<Integer> Am = new LinkedHashSet<>();
        final HashSet<Integer> inAm = new HashSet<>();

        final HashSet<Integer> frames = new HashSet<>(loaded);

        for (int i = 0; i < memoryAccesses.size() - 1; i++) {
            int p = memoryAccesses.get(i);

            if (frames.contains(p)) {
                if (inA1.contains(p)) {
                    removeFromA1(p, A1, inA1);
                    addToAm(p, Am, inAm, capAm);
                } else if (inAm.contains(p)) {
                    Am.remove(p);
                    Am.add(p);
                } else {
                    addToA1(p, A1, inA1, capA1);
                }
            } else {
                if (frames.size() < loaded) {
                    frames.add(p);
                    addToA1(p, A1, inA1, capA1);
                } else {
                    int victim = chooseVictim(A1, Am);
                    if (victim != -1) {
                        if (inA1.remove(victim)) removeFromA1(victim, A1, new HashSet<>(Collections.singleton(victim)));
                        if (inAm.remove(victim)) Am.remove(victim);
                        frames.remove(victim);
                    }
                    frames.add(p);
                    addToA1(p, A1, inA1, capA1);
                }
            }
        }

        if (frames.size() < loaded) return -1;
        return chooseVictim(A1, Am);
    }

    private static void addToA1(int p, ArrayDeque<Integer> A1, HashSet<Integer> inA1, int capA1) {
        if (inA1.contains(p)) return;
        if (A1.size() >= capA1) {
            Integer old = A1.pollFirst();
            if (old != null) inA1.remove(old);
        }
        A1.addLast(p);
        inA1.add(p);
    }

    private static void addToAm(int p, LinkedHashSet<Integer> Am, HashSet<Integer> inAm, int capAm) {
        if (inAm.contains(p)) {
            Am.remove(p);
            Am.add(p);
            return;
        }
        if (Am.size() >= capAm) {
            Iterator<Integer> it = Am.iterator();
            if (it.hasNext()) {
                Integer lru = it.next();
                it.remove();
                inAm.remove(lru);
            }
        }
        Am.add(p);
        inAm.add(p);
    }

    private static void removeFromA1(int p, ArrayDeque<Integer> A1, HashSet<Integer> inA1) {
        if (!inA1.contains(p)) return;
        ArrayDeque<Integer> tmp = new ArrayDeque<>();
        while (!A1.isEmpty()) {
            int q = A1.pollFirst();
            if (q != p) tmp.addLast(q);
        }
        A1.addAll(tmp);
        inA1.remove(p);
    }

    private static int chooseVictim(ArrayDeque<Integer> A1, LinkedHashSet<Integer> Am) {
        if (!A1.isEmpty()) {
            Integer v = A1.peekFirst();
            return v == null ? -1 : v;
        }
        if (!Am.isEmpty()) {
            Iterator<Integer> it = Am.iterator();
            return it.hasNext() ? it.next() : -1;
        }
        return -1;
    }
}

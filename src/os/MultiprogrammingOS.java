
package os;

import os.job.*;
import os.process.Process;
import java.util.Random;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.ListIterator;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author mnprtpsingh
 */
public class MultiprogrammingOS {
    
    public static Burst createCPUBurst(Random random, int memReq) {
        int bstTime = 10 + random.nextInt(91);
        return new CPU_Burst(bstTime, BurstType.CPU_BURST, memReq);
    }
    
    public static Burst createIOBurst(Random random) {
        int bstTime = 20 + random.nextInt(41);
        return new IO_Burst(bstTime, BurstType.IO_BURST);
    }

    public static LinkedList<Job> createJobs(ComputerSystem system) {
        LinkedList<Job> jobs = new LinkedList<>();
        Random random = new Random();
        int numerOfJobs = 5 + random.nextInt(10);
        for (int i = 0; i < numerOfJobs; i++) {
            int numberOfBurst = 1 + random.nextInt(10);
            ArrayList<Burst> bursts = new ArrayList<>();
            int memReq = 5 + random.nextInt(196);
            bursts.add(createCPUBurst(random, memReq));
            int memAlloc = memReq;
            for (int j = 1; j < numberOfBurst; j++) {
                if (j % 2 == 0) {
                    if (random.nextBoolean()) {
                        memReq = - random.nextInt(memAlloc);
                    } else {
                        memReq = random.nextInt(41);
                    }
                    bursts.add(createCPUBurst(random, memReq));
                    memAlloc += memReq;
                } else {
                    bursts.add(createIOBurst(random));
                }
            }
            String name = "Program " + Integer.toString(random.nextInt(numerOfJobs));
            jobs.add(new Job(name, bursts));
        }
        return jobs;
    }
    
    
    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) {
        ComputerSystem system = new ComputerSystem(1024, 320);
        LongTermScheduler lts = system.getLongTermScheduler();
        LinkedList<Job> jobs = createJobs(system);
        lts.addNewJobs(jobs);

        while (lts.loadJob());
        system.sleep(100);
        system.dispatch();
        while (system.run());

        FileWriter writer;
        try {
            writer = new FileWriter("output.txt");
            writer.write("Number of Process: " + Integer.toString(jobs.size()));

            ShortTermScheduler sts = system.getShortTermScheduler();
            LinkedList<Process> killed = sts.getKilledQueue();
            ListIterator<Process> itr = killed.listIterator();
            while (itr.hasNext()) {
                Process proc = itr.next();
                proc.displayStatistics(writer);
            }

            LinkedList<Process> terminated = sts.getTerminatedQueue();
            itr = terminated.listIterator();
            while (itr.hasNext()) {
                Process proc = itr.next();
                proc.displayStatistics(writer);
            }
            String s = System.lineSeparator();
            writer.write(s + s + "CPU Utilization: " + Double.toString(system.getCPUUtilization()));
            writer.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}

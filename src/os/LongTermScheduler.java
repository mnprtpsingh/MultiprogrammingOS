
package os;

import os.job.Job;
import java.util.LinkedList;

/**
 *
 * @author mnprtpsingh
 */
public class LongTermScheduler {
    private final ComputerSystem system;
    private final LinkedList<Job> jobsQueue;

    LongTermScheduler(ComputerSystem system) {
        this.system = system;
        this.jobsQueue = new LinkedList<>();
    }

    public void addNewJobs(LinkedList<Job> jobs) {
        if (jobs.isEmpty()) return;
        this.jobsQueue.addAll(jobs);
    }

    public boolean hasJob() {
        return !this.jobsQueue.isEmpty();
    }

    public boolean loadJob() {
        if (this.hasJob()) {
            int availableMemory = this.system.getAvailableMemorySize();
            int ram = this.system.getRAM();
            Job job = this.jobsQueue.element();
            int memoryRequired = job.memoryRequired;
            if (availableMemory - memoryRequired >= 0.15 * ram) {
                ShortTermScheduler sts = this.system.getShortTermScheduler();
                sts.createProcess(job);
                this.system.allocateMemory(memoryRequired);
                this.jobsQueue.remove();
                return true;
            }
        }
        return false;
    }
}

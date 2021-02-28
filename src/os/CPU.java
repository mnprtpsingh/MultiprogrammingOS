
package os;

import os.process.Process;

/**
 *
 * @author mnprtpsingh
 */
public class CPU {
    private Process runningProcess;
    
    public Process getRunningProcess() {
        return this.runningProcess;
    }
    
    public void setRunningProcess(Process proc) {
        this.runningProcess = proc;
    }
}

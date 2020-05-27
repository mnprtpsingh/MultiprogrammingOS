
package os.job;

/**
 *
 * @author mnprtpsingh
 */
public class CPU_Burst extends Burst {
    private final int memoryRequired;
       
    public CPU_Burst(int time, BurstType type, int memory) {
        super(time, type);
        this.memoryRequired = memory;
    }
    
    public int getMemoryRequired() {
        return this.memoryRequired;
    }
}

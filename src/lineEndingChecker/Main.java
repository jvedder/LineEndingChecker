/**
 * 
 */
package lineEndingChecker;

/**
 * @author John
 *
 */
public class Main
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        {
            try
            {
                Worker w = new Worker();
                w.Run();
            }
            catch (Exception ex) 
            {
                    System.err.format("Error: Main: %s\n", ex);
            }
        }

    }

}

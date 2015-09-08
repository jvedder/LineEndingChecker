package lineEndingChecker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;

/**
 *
 * @author John
 */
public class Worker
{
    private SimpleDateFormat DateFormat = null;
    private String hostname = "";
    public PriorityQueue<Path> DirectoryQueue = null;
    
    public Worker()
    {
        try 
        {
            DirectoryQueue = new PriorityQueue<>(); 
            DateFormat = new SimpleDateFormat(Common.DateFormatString);
        } 
        catch (Exception ex) 
        {
            System.err.format("Error: Worker Constructor: %s\r\n", ex);
        }
    }
    
    public void Run()
    {
        Charset charset = Charset.forName("UTF-8");
        Path filePath = Paths.get(Common.ChecksumFilename);
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, charset)) 
        {
            System.out.println("Start: " + DateFormat.format(new Date())); 
            WalkTree(writer);
            System.out.println("Finish: " + DateFormat.format(new Date()) + "\r\n"); 
        } 
        catch (Exception ex) 
        {
            System.err.format("Error: Worker.Run: %s\r\n", ex);
        }
    }
    
    public void WalkTree(BufferedWriter writer) throws IOException 
    {
        getHostname();       
        
        Path rootPath = Paths.get(Common.RootFolder);
        DirectoryQueue.clear();
        DirectoryQueue.add(rootPath);
        
        writer.write("Host: " + hostname + "\r\n");
        writer.write("Date: " + DateFormat.format(new Date()) + "\r\n"); 
        writer.write("Root: " + Common.RootFolder + "\r\n");

        //Process until every directory is done
        while (DirectoryQueue.size() > 0)
        {      
            //grab the next directory in the work queue
            Path workingDir = DirectoryQueue.remove();
            
            //show progress
            System.out.println(workingDir.toString());
                        
            //Add this to the output files
            writer.write("\r\n");
            writer.write("Dir: " + rootPath.relativize(workingDir) + "\r\n");

            //walk through all items in this directory
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(workingDir)) 
            {
                for (Path entry: stream) 
                {
                    
                    if (Files.isDirectory(entry))
                    {
                        //Add directories to the work queue
                        DirectoryQueue.add(entry);
                    }
                    else if (Files.isRegularFile(entry))
                    {                          
                        //process files
                        FileChecker checker = new FileChecker();
                        checker.Load(entry);
                        writer.write(checker.toString() + "\r\n");
                    }
                }
            }
            catch (Exception ex) 
            {
                System.err.format("Error: Worker DirectoryStream: %s\r\n", ex);
            } 
            
            //Flush the file to disk at the end of every directory
            writer.flush();                   
        }
        
        System.out.println("Done.");
    }   
    
    private void getHostname()
    {
        try
        {
            hostname = java.net.InetAddress.getLocalHost().getHostName();
        }
        catch (Exception ex)
        {
            System.err.format("Error: Worker HostName: %s\r\n", ex);
        }
    }
        
    
}

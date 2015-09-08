package lineEndingChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class FileChecker
{
    public int lines = 0;
    public int CRlines = 0;
    public int CRLFlines = 0;
    public int LFlines = 0;
    public String name = "";

    public FileChecker()
    {
        // Empty constructor
    }

    public void Load(Path filePath)
    {
        // First, confirm this file exists
        if (!Files.exists(filePath, LinkOption.NOFOLLOW_LINKS))
        {
            System.err.format("Error: File Not Found: %s\n", filePath);
            return;
        }

        // Get the file's name
        name = filePath.getFileName().toString();

        lines = 0;
        CRlines = 0;
        CRLFlines = 0;
        LFlines = 0;

        try
        {
            BufferedReader in = Files.newBufferedReader(filePath,
                    StandardCharsets.UTF_8);
            int ch;
            boolean carriageReturnSeen = false;
            while ((ch = in.read()) != -1)
            {
                if (ch == '\r')
                {
                    carriageReturnSeen = true;
                }
                else if (ch == '\n')
                {
                    if (carriageReturnSeen)
                    {
                        // line ending is CR/LF
                        carriageReturnSeen = false;
                        lines++;
                        CRLFlines++;
                    }
                    else
                    {
                        // line ending is LF alone
                        lines++;
                        LFlines++;
                    }
                }
                else if (carriageReturnSeen)
                {
                    // line ending is CR alone
                    carriageReturnSeen = false;
                    lines++;
                    CRlines++;
                }
            }

            in.close();
        }
        catch (IOException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

    }

    public boolean isWindows()
    {
        return (CRLFlines > 0) && (CRlines == 0) && (LFlines == 0);
    }

    public boolean isLinux()
    {
        return (LFlines > 0) && (CRLFlines == 0) && (LFlines == 0);
    }

    public String getType()
    {
        if (isWindows())
        {
            return "isWindows";
        }
        else if (isLinux())
        {
            return "isLinux";
        }
        else
        {
            return "isMixed";
        }
    }

    @Override
    public String toString()
    {
        return name + Common.Separator +
                "CR:" + CRlines + Common.Separator +
                "CRLF:" + CRLFlines + Common.Separator +
                "LF:" + LFlines;
    }

}
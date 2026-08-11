package org.vnu.sme.tocl;

import org.tzi.use.main.Session;
import java.io.PrintWriter;

public class TOCLLoader {
    private Session session;
    private String filename;
    private PrintWriter logWriter;
    
    /**
     * TOCLLoader constructor
     * @param session the current session of USE tool
     * @param filename the filename of the TOCL file
     * @param logWriter log writer to write log messages to the console of USE tool
     */
    public TOCLLoader(Session session, String filename, PrintWriter logWriter) {
        this.session = session;
        this.filename = filename;
        this.logWriter = logWriter;
    }
    
    public boolean run() {
        logWriter.println("Compiling TOCL file");
        
        return true;
    }
}

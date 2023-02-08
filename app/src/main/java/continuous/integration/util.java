package continuous.integration;

import com.fasterxml.jackson.databind.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import continuous.Models.Payload;
import continuous.Models.TestInfo;

public class util {

    public static Payload JSONConverter(String JSON){
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Payload payload = mapper.readValue(JSON, Payload.class);
            return payload;
        }catch(Exception e){
            System.err.print(e);
        }
        return new Payload();
    }

    /**
     * Method for cloning the specific branch from GitHub.
     * @params Takes the URI of the repo and the name of the branch to be cloned.
     * @returns Returns a string giving a success or fail message.
     */
    public static void cloneRepo(String URI, String branch) throws TransportException, InvalidRemoteException, GitAPIException {
        Git r = Git.cloneRepository()
                .setURI(URI)
                //.setDirectory(new File("")) return status info, "clone failed because uri is not valid"
                .setBranchesToClone(Arrays.asList("refs/heads/" + branch))
                .setBranch("refs/heads/" + branch)
                .call();
    }
    /**
     * Method for deleting the GitHub repo after it has been used.
     * @param folderPath The path to folder which is to be deleted. Created by Oguz
     */
    public static void deleteRepo(String folderPath){
        String command = null;
        if (System.getProperty("os.name").startsWith("Windows")) {
            command = "cmd /c rmdir /s /q " + folderPath;
        } else {
            command = "rm -rf " + folderPath;
        }
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TestInfo runTests(String folderPath){
        TestInfo testInfo = null;
        try {
        	ProcessBuilder pb;
        	if (System.getProperty("os.name").startsWith("Windows")) {
           
        	pb = new ProcessBuilder(
        			"cmd",
        			"/c",
        	        "gradlew",
        	        "test"
        	        );
        	}else {
        		pb = new ProcessBuilder(
        				"/bin/bash",
            			"-c",
            	        "gradlew",
            	        "test"
        				);
        	}
        	pb.directory(new File("Test"));
        	Process process = pb.start();       testInfo = new TestInfo();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
            	if(line.endsWith("FAILED") || line.endsWith("PASSED") || line.endsWith("SKIPPED")) {
            		testInfo.details += line;
            		testInfo.details += "\n";	
            	}
                
            }
            int exitValue = process.waitFor();
            if (exitValue == 0) {
            	testInfo.status = "SUCCESSFUL";
            } else {
            	testInfo.status = "FAILURE";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return testInfo;
    }
    
}

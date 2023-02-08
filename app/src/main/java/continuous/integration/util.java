package continuous.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import java.io.IOException;
import java.util.Arrays;

import continuous.Models.Payload;
import continuous.Models.BuildInfo;


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
    
    /**
     *  Method that build a cloned repo
     * @return Returns a string that shows that the build was successful or failed. 
     */
    public static BuildInfo buildRepo(String repoPath){
        BuildInfo buildInfo = null;
        try {
        	ProcessBuilder pb;
        	if (System.getProperty("os.name").startsWith("Windows")) {
           
        	pb = new ProcessBuilder(
        			"cmd",
        			"/c",
        	        "gradlew",
        	        "build",
        	        "-x",
        	        "test"
        	        );
        	}else {
        		pb = new ProcessBuilder(
        				"/bin/bash",
            			"-c",
            	        "gradlew",
            	        "build",
            	        "-x",
            	        "test"
        				);
        	}
        	pb.directory(new File(repoPath));
        	Process process = pb.start();
//            Process process = Runtime.getRuntime().exec(new String[]{"cmd", "/c","cd Test && gradlew build -x test"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
             buildInfo = new BuildInfo();
            String line = null;
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                buildInfo.status = "SUCCESSFUL";
            } else {
            	buildInfo.status = "FAILURE";
            	while ((line = error.readLine()) != null) {
            		if(line.startsWith("FAILURE"))
                    	break;
            		buildInfo.details += line;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildInfo;
    }
}

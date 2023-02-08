package continuous.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.*;

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

package continuous.integration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.mail.MessagingException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.Git;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import continuous.Models.*;;

/** 
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/
public class ContinuousIntegrationServer extends AbstractHandler
{
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);
        response.getWriter().println("CI job done");
        String JSON = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println(JSON);
        Payload repoInfo = util.JSONConverter(JSON);

        BuildInfo buildInfo;
        TestInfo testInfo = new TestInfo();
        String URL = "https://github.com/"+ repoInfo.repository.full_name;
        String[] branchList = repoInfo.ref.split("/");
        String branch = branchList[branchList.length-1];
        System.out.println("Branch Name: " + branch);
        String[] names = repoInfo.repository.full_name.split("/");
        String path = names[names.length-1];
        System.out.println("Path: " + path);
        Git r = null;
        try{
            r = util.cloneRepo(URL, branch);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        

        buildInfo = util.buildRepo(path);

        if(buildInfo.status == "SUCCESSFUL"){
            testInfo = util.runTests(path);
        }
        else{
            testInfo.status = "Compilation Unsuccessful - > can not be tested";
            testInfo.details = "";
        }
        //To check the url and branch name uncomment the print statement below
        //System.out.println("URL -> " + URL + "Branch ->" + branch);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        Mail mail = new Mail();

        String recipient = "aoengin@kth.se";
        // String invalidRecipient = "abcbystam@kth.se";
        String title = "Testmail";
        String content = "Compilation: " + buildInfo.status + "\n" 
        + "Details: " + "\n" +
        buildInfo.details + "\n";
        content += "Test: " + testInfo.status + "\n" 
        + "Details:" + "\n" +
        testInfo.details;

        try {
            util.sendEmail(recipient, title, content, mail);
        } catch ( MessagingException exc) {
            exc.printStackTrace();
        }
        if(r != null)
            r.getRepository().close();
        util.deleteRepo(new File(path));
    }
 
    // used to start the CI server in command line
    public static void startServer(int portNumber) throws Exception
    {
        Server server = new Server(portNumber);
        server.setHandler(new ContinuousIntegrationServer()); 
        server.start();
        server.join();
    }
}
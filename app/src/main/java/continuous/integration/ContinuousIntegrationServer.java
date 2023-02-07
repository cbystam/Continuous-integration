package continuous.integration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
 
import java.io.IOException;
import java.util.stream.Collectors;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import continuous.Models.Payload;

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

        String JSON = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        Payload repoInfo = util.JSONConverter(JSON);

        String URL = "https://github.com/"+ repoInfo.repository.full_name;
        String branch = repoInfo.ref;

        //To check the url and branch name uncomment the print statement below
        //System.out.println("URL -> " + URL + "Branch ->" + branch);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        response.getWriter().println("CI job done");
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
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package continuous.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.util.Formatter.BigDecimalLayoutForm;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import continuous.Models.BuildInfo;
import continuous.Models.Mail;

import java.util.Properties;
import java.net.PasswordAuthentication;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;


class AppTest {
    
    /**
     * This tests to check if the URI is wrong. Meaning it is not available or does not exist.
     * The method clone repo takes an URI and branch name as parameters.
     */
    @Test void wrongURI() throws GitAPIException {
        Exception ex = assertThrows(GitAPIException.class, () -> {
            util.cloneRepo("https://github.com/arnbaeck/assig2222", "testing1");
        });
    }
    /**
     * This tests to check if the Branch is wrong. Meaning it does not exist or is mispelled.
     * The method clone repo takes an URI and branch name as parameters.
     */
    @Test void wrongBranch() throws GitAPIException {
        Exception ex = assertThrows(GitAPIException.class, () -> {
            util.cloneRepo("https://github.com/arnbaeck/assig2", "testing111");
        });
    }


     /**
     *Test if a successful clone is made by checking a key word in a txt file. If the clone is successful,
     * the key word will be found.
     */
    @Test void cloneTest() throws GitAPIException, IOException {
        String realString = "testString123";
        Git git = util.cloneRepo("https://github.com/arnbaeck/assig2", "testing1");
        File file = new File("assig2\\app\\src\\test\\java\\assig2\\test.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = br.readLine();
        br.close();
        git.getRepository().close();
        util.deleteRepo(new File("assig2"));
        assertEquals(s, realString);
    }

       /**
        * A successful test for "buildRepo" function
        * This test checks that the function "buildRepo" is successfully building a repo 
        */
    @Test void buildSuccess() throws GitAPIException {
            Git git = util.cloneRepo("https://github.com/AhmetOguzEngin/Test", "test1");
            BuildInfo buildInfo = util.buildRepo("Test");
            git.getRepository().close();
            util.deleteRepo(new File("Test"));
            assertEquals("SUCCESSFUL", buildInfo.status);
            
    }


        /**
        * A failure test to for "buildRepo" function
        * This test checks that the function "buildRepo" is failing while building a repo
        */    
    @Test void buildFailure() throws GitAPIException {
            Git git = util.cloneRepo("https://github.com/AhmetOguzEngin/Test", "test2");
            BuildInfo buildInfo = util.buildRepo("Test");
            git.getRepository().close();
            System.out.println("a");
            util.deleteRepo(new File("Test"));
            assertEquals("FAILURE", buildInfo.status);
    }

    /**
     * Test that the sendMail function works correctly.
     */
    @Test void testMail() {
        Mail mail = new Mail();
        String recipient = "dd2480group23@gmail.com";
        String title = "Testmail";
        String content = "Testcontent";

        try {
            util.sendEmail(recipient, title, content, mail);
        } catch ( MessagingException exc) {
            exc.printStackTrace();
        }

        // Now check the inbox and see if the mail has been retrieved.

        String host = "pop.gmail.com";
        String mailStoreType = "pop3";

        Properties props = new Properties();

        props.put("mail.pop3.host", host);
        props.put("mail.pop3.port", 995);
        props.put("mail.pop3.starttls.enable", "true");

        Session session = Session.getInstance(props);
        try {
            Store store = session.getStore("pop3s");
            store.connect(host, mail.username, mail.password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.getMessages();
            Message latestMessage = messages[messages.length];

            assertEquals(latestMessage.getSubject(), "Testmail");
            assertEquals(latestMessage.getContent(), "Testcontent");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}


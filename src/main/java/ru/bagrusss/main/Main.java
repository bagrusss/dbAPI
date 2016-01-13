package ru.bagrusss.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;
import ru.bagrusss.servlets.forum.ListPosts;
import ru.bagrusss.servlets.forum.ListThreads;
import ru.bagrusss.servlets.forum.ListUsers;
import ru.bagrusss.servlets.root.Clear;
import ru.bagrusss.servlets.root.Status;
import ru.bagrusss.servlets.thread.*;
import ru.bagrusss.servlets.thread.Create;
import ru.bagrusss.servlets.thread.Details;
import ru.bagrusss.servlets.user.*;
import ru.bagrusss.servlets.forum.ListPosts;


/**
 * Created by vladislav on 19.10.15.
 */

public class Main {

    public static final int PORT = 5000;

    public static void main(String[] args) {
        int port = PORT;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        Server server = null;
        try {
            Resource conf = Resource.newResource("jetty.xml");
            XmlConfiguration configuration = new XmlConfiguration(conf.getInputStream());
            server = (Server) configuration.configure();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }


        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        contextHandler.addServlet(new ServletHolder(new Clear()), Clear.URL);
        contextHandler.addServlet(new ServletHolder(new Status()), Status.URL + '*');

        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.user.Create()), ru.bagrusss.servlets.user.Create.URL);
        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.user.Details()), ru.bagrusss.servlets.user.Details.URL);
        contextHandler.addServlet(new ServletHolder(new Follow()), Follow.URL);
        contextHandler.addServlet(new ServletHolder(new ListFollowers()), ListFollowers.URL);
        contextHandler.addServlet(new ServletHolder(new ListFollowing()), ListFollowing.URL);
        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.user.ListPosts()), ru.bagrusss.servlets.user.ListPosts.URL);
        contextHandler.addServlet(new ServletHolder(new Unfollow()), Unfollow.URL);
        contextHandler.addServlet(new ServletHolder(new UpdateProfile()), UpdateProfile.URL);

        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.forum.Create()), ru.bagrusss.servlets.forum.Create.URL);
        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.forum.Details()), ru.bagrusss.servlets.forum.Details.URL);
        contextHandler.addServlet(new ServletHolder(new ListPosts()), ListPosts.URL);
        contextHandler.addServlet(new ServletHolder(new ListThreads()), ListThreads.URL);
        contextHandler.addServlet(new ServletHolder(new ListUsers()), ListUsers.URL);

        contextHandler.addServlet(new ServletHolder(new Close()), Close.URL);
        contextHandler.addServlet(new ServletHolder(new Create()), Create.URL);
        contextHandler.addServlet(new ServletHolder(new Details()), Details.URL);
        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.thread.ListPosts()), ru.bagrusss.servlets.thread.ListPosts.URL);
        contextHandler.addServlet(new ServletHolder(new List()), List.URL);
        contextHandler.addServlet(new ServletHolder(new Open()), Open.URL);
        contextHandler.addServlet(new ServletHolder(new Remove()), Remove.URL);
        contextHandler.addServlet(new ServletHolder(new Restore()), Restore.URL);
        contextHandler.addServlet(new ServletHolder(new Subscrabe()), Subscrabe.URL);
        contextHandler.addServlet(new ServletHolder(new Unsubscrabe()), Unsubscrabe.URL);
        contextHandler.addServlet(new ServletHolder(new Update()), Update.URL);
        contextHandler.addServlet(new ServletHolder(new Vote()), Vote.URL);

        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.post.Create()), ru.bagrusss.servlets.post.Create.URL);
        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.post.Details()), ru.bagrusss.servlets.post.Details.URL);
        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.post.List()), ru.bagrusss.servlets.post.List.URL);
        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.post.Remove()), ru.bagrusss.servlets.post.Remove.URL);
        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.post.Restore()), ru.bagrusss.servlets.post.Restore.URL);
        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.post.Update()), ru.bagrusss.servlets.post.Update.URL);
        contextHandler.addServlet(new ServletHolder(new ru.bagrusss.servlets.post.Vote()), ru.bagrusss.servlets.post.Vote.URL);

        server.setHandler(contextHandler);
        try {
            server.start();
            try {
                server.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package ru.bagrusss.main;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.bagrusss.servlets.forum.*;
import ru.bagrusss.servlets.forum.ListPosts;
import ru.bagrusss.servlets.root.Clear;
import ru.bagrusss.servlets.root.Status;
import ru.bagrusss.servlets.thread.*;
import ru.bagrusss.servlets.thread.Create;
import ru.bagrusss.servlets.thread.Details;
import ru.bagrusss.servlets.thread.List;
import ru.bagrusss.servlets.thread.Remove;
import ru.bagrusss.servlets.thread.Restore;
import ru.bagrusss.servlets.thread.Update;
import ru.bagrusss.servlets.thread.Vote;
import ru.bagrusss.servlets.user.*;

/**
 * Created by vladislav on 19.10.15.
 */

public class Main {

    public static final int PORT = 5000; //28087

    public static void main(String[] args) {
        Server server = new Server(PORT);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        contextHandler.addServlet(new ServletHolder(new Clear()), Clear.URL);
        contextHandler.addServlet(new ServletHolder(new Status()), Status.URL+ '*');

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
        contextHandler.addServlet(new ServletHolder(new ListUser()), ListUser.URL);

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
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[]{contextHandler});
        server.setHandler(handlerList);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /*
    public static class UrlParserHelper {

        @NotNull
        private static final String REGEXP_STR = "park.mail.ru/blog/topic/([^/]+)(/)?";

        @NotNull
        private final Pattern pattern = Pattern.compile(REGEXP_STR);

        @Nullable
        public String parse(@NotNull String url) {
            final Matcher matcher = pattern.matcher(url);
            if (matcher.find())
                return matcher.group(1);
            else
                return null;
        }
    }*/
}

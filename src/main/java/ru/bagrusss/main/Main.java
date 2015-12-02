package ru.bagrusss.main;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.bagrusss.servlets.forum.*;
import ru.bagrusss.servlets.post.*;
import ru.bagrusss.servlets.root.ClearServlet;
import ru.bagrusss.servlets.root.StatusServlet;
import ru.bagrusss.servlets.thread.*;
import ru.bagrusss.servlets.user.*;

/**
 * Created by vladislav on 19.10.15.
 */

public class Main {

    public static final int PORT = 5000; //28087

    public static void main(String[] args) {
        Server server = new Server(PORT);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        contextHandler.addServlet(new ServletHolder(new ClearServlet()), ClearServlet.URL);
        contextHandler.addServlet(new ServletHolder(new StatusServlet()), StatusServlet.URL+ '*');

        contextHandler.addServlet(new ServletHolder(new UserCreate()), UserCreate.URL);
        contextHandler.addServlet(new ServletHolder(new UserDetails()), UserDetails.URL);
        contextHandler.addServlet(new ServletHolder(new UserFollow()), UserFollow.URL);
        contextHandler.addServlet(new ServletHolder(new UserListFollowers()), UserListFollowers.URL);
        contextHandler.addServlet(new ServletHolder(new UserListFollowing()), UserListFollowing.URL);
        contextHandler.addServlet(new ServletHolder(new UserListPosts()), UserListPosts.URL);
        contextHandler.addServlet(new ServletHolder(new UserUnFollow()), UserUnFollow.URL);
        contextHandler.addServlet(new ServletHolder(new UserUpdateProfile()), UserUpdateProfile.URL);

        contextHandler.addServlet(new ServletHolder(new ForumCreate()), ForumCreate.URL);
        contextHandler.addServlet(new ServletHolder(new ForumDetailsServlet()), ForumDetailsServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ForumListPost()), ForumListPost.URL);
        contextHandler.addServlet(new ServletHolder(new ForumListThreads()), ForumListThreads.URL);
        contextHandler.addServlet(new ServletHolder(new ForumListUser()), ForumListUser.URL);

        contextHandler.addServlet(new ServletHolder(new ThreadClose()), ThreadClose.URL);
        contextHandler.addServlet(new ServletHolder(new ThreadCreate()), ThreadCreate.URL);
        contextHandler.addServlet(new ServletHolder(new ThreadDetails()), ThreadDetails.URL);
        contextHandler.addServlet(new ServletHolder(new ThreadListPosts()), ThreadListPosts.URL);
        contextHandler.addServlet(new ServletHolder(new ThreadList()), ThreadList.URL);
        contextHandler.addServlet(new ServletHolder(new ThreadOpenServlet()), ThreadOpenServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThreadRemove()), ThreadRemove.URL);
        contextHandler.addServlet(new ServletHolder(new ThreadRestor()), ThreadRestor.URL);
        contextHandler.addServlet(new ServletHolder(new ThreadSubscrabe()), ThreadSubscrabe.URL);
        contextHandler.addServlet(new ServletHolder(new ThreadUnSubscrabe()), ThreadUnSubscrabe.URL);
        contextHandler.addServlet(new ServletHolder(new ThreadUpdate()), ThreadUpdate.URL);
        contextHandler.addServlet(new ServletHolder(new ThreadVote()), ThreadVote.URL);

        contextHandler.addServlet(new ServletHolder(new PostCreate()), PostCreate.URL);
        contextHandler.addServlet(new ServletHolder(new PostDetails()), PostDetails.URL);
        contextHandler.addServlet(new ServletHolder(new PostList()), PostList.URL);
        contextHandler.addServlet(new ServletHolder(new PostRemove()), PostRemove.URL);
        contextHandler.addServlet(new ServletHolder(new PostRestore()), PostRestore.URL);
        contextHandler.addServlet(new ServletHolder(new PostUpdate()), PostUpdate.URL);
        contextHandler.addServlet(new ServletHolder(new PostVote()), PostVote.URL);
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

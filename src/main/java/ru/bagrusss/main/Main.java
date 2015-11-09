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

    public static final int PORT = 28087; //28087

    public static void main(String[] args) {
        Server server = new Server(PORT);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        contextHandler.addServlet(new ServletHolder(new ClearServlet()), ClearServlet.URL);
        contextHandler.addServlet(new ServletHolder(new StatusServlet()), StatusServlet.URL);

        contextHandler.addServlet(new ServletHolder(new UCreateServlet()), UCreateServlet.URL);
        contextHandler.addServlet(new ServletHolder(new UDetailsServlet()), UDetailsServlet.URL);
        contextHandler.addServlet(new ServletHolder(new UFollow()), UFollow.URL);
        contextHandler.addServlet(new ServletHolder(new UListFollowers()), UListFollowers.URL);
        contextHandler.addServlet(new ServletHolder(new UListFollowing()), UListFollowing.URL);
        contextHandler.addServlet(new ServletHolder(new UListPosts()), UListPosts.URL);
        contextHandler.addServlet(new ServletHolder(new UUnfollow()), UUnfollow.URL);
        contextHandler.addServlet(new ServletHolder(new UUpdateProfile()), UUpdateProfile.URL);

        contextHandler.addServlet(new ServletHolder(new FCreateServlet()), FCreateServlet.URL);
        contextHandler.addServlet(new ServletHolder(new FDetailsServlet()), FDetailsServlet.URL);
        contextHandler.addServlet(new ServletHolder(new FListPostServlet()), FListPostServlet.URL);
        contextHandler.addServlet(new ServletHolder(new FListThreadsServlet()), FListThreadsServlet.URL);
        contextHandler.addServlet(new ServletHolder(new FListUserServlet()), FListUserServlet.URL);

        contextHandler.addServlet(new ServletHolder(new ThCloseServlet()), ThCloseServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThCreateServlet()), ThCreateServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThDetailsServlet()), ThDetailsServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThListPostsServlet()), ThListPostsServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThListServlet()), ThListServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThOpenServlet()), ThOpenServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThRemoveServlet()), ThRemoveServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThRestorServlet()), ThRestorServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThSubscrabeServlet()), ThSubscrabeServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThUnSubscrabeServlet()), ThUnSubscrabeServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThUpdateServlet()), ThUpdateServlet.URL);
        contextHandler.addServlet(new ServletHolder(new ThVoteServlet()), ThVoteServlet.URL);

        contextHandler.addServlet(new ServletHolder(new PCreateServlet()), PCreateServlet.URL);
        contextHandler.addServlet(new ServletHolder(new PDetailsServlet()), PDetailsServlet.URL);
        contextHandler.addServlet(new ServletHolder(new PListServlet()), PListServlet.URL);
        contextHandler.addServlet(new ServletHolder(new PRemoveServlet()), PRemoveServlet.URL);
        contextHandler.addServlet(new ServletHolder(new PRestoreServlet()), PRestoreServlet.URL);
        contextHandler.addServlet(new ServletHolder(new PUpdateServlet()), PUpdateServlet.URL);
        contextHandler.addServlet(new ServletHolder(new PVoteServlet()), PVoteServlet.URL);

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

}

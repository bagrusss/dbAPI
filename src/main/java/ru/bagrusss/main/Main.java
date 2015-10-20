package ru.bagrusss.main;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.bagrusss.servlets.root.ClearServlet;
import ru.bagrusss.servlets.root.StatusServlet;

/**
 * Created by vladislav on 19.10.15.
 */

public class Main {

    public static final int PORT = 5000;

    public static void main(String[] args) {
        Server server = new Server(PORT);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        //root
        contextHandler.addServlet(new ServletHolder(new ClearServlet()), ClearServlet.URL);
        contextHandler.addServlet(new ServletHolder(new StatusServlet()), StatusServlet.URL);

        //Forum



        HandlerList handlerList=new HandlerList();
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

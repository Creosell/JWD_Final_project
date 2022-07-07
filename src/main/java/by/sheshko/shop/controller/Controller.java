package by.sheshko.shop.controller;

import by.sheshko.shop.controller.command.Command;
import by.sheshko.shop.controller.command.CommandName;
import by.sheshko.shop.dao.pool.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;

public final class Controller extends HttpServlet {
    private static final long serialVersionUID = 4296569594467128804L;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final CommandProvider provider = new CommandProvider();

    @Override
    public void init() throws ServletException {
        try {
            ConnectionPool.getInstance().initPoolData();
        } catch (ClassNotFoundException e) {
            log.error("Error while trying to find driver class for connection pool", e);
            throw new ServletException("Error initializing connection pool", e);
        } catch (SQLException e) {
            log.error("Error while connection pool working with database", e);
            throw new ServletException("Error initializing connection pool", e);
        }
        super.init();
    }

    @Override
    public void destroy() {
        ConnectionPool.getInstance().dispose();
        super.destroy();
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        CommandName commandName;
        Command command;
        String page = null;

        try {
            commandName = CommandName.valueOf(request.getParameter("command").toUpperCase());
            command = provider.getCommand(String.valueOf(commandName));
            page = command.execute(request, response);
        } catch (Exception e) { //TODO Нормальный эксепшн
            log.error("Exception while processing request", e);
            dispatch(request, response, page);
        }
        dispatch(request, response, page);
    }

    private void dispatch(HttpServletRequest request, HttpServletResponse response, String page) throws javax.servlet.ServletException, java.io.IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(page);
        dispatcher.forward(request, response);
    }
}


 /*     PrintWriter out = response.getWriter();
        CommandName commandName = CommandName.valueOf(request.getParameter("command").toUpperCase());
        Command command = provider.getCommand(String.valueOf(commandName));

        HttpSession session = request.getSession();
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        session.setAttribute(userSessionInfo.getClass().getName(), userSessionInfo);

        if (userSessionInfo.getName() == null) {
            userSessionInfo.setName("Anonymous");
        }
*/
        /*switch (commandName) {
            case SIGN_IN:
            case REGISTRATION:
                try {
                    out.println(command.execute(
                            request.getParameter("login")
                                    + " " + request.getParameter("password")));
                } catch (ControllerException e) {
                    out.println(e.getMessage());
                }
                break;
            case SIGN_OUT:
                session.removeAttribute(userSessionInfo.getClass().getName());
                System.out.println(userSessionInfo.getClass().getName());
            default:
                try {
                    provider.getCommand(String.valueOf(CommandName.WRONG_REQUEST)).execute("");
                } catch (ControllerException e) {
                    log.error("Request error", e);
                }
        }*/
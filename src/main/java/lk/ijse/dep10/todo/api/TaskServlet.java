package lk.ijse.dep10.todo.api;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(name = "TaskServlet", value = "/task/*")
public class TaskServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            super.doGet(request, response);
        } else if (action.equalsIgnoreCase("delete")) {
            doDelete(request,response);
        } else if (action.equals("update")) {
            doPatch(request,response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() != null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid uri");
            return;
        }

        String description = request.getParameter("description");
        if (description == null || description.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        BasicDataSource pool = (BasicDataSource) getServletContext().getAttribute("dbcp");
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Task (description, status) VALUES (?,DEFAULT)");
            stm.setString(1, description);
            stm.executeUpdate();
            getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo() == null || !req.getPathInfo().matches("/\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URI");
            return;
        }
        String status = req.getParameter("status");
        if (status == null || !(status.equalsIgnoreCase("COMPLETED") || status.equalsIgnoreCase("NOT_COMPLETED"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Status");
            return;
        }
        BasicDataSource pool = (BasicDataSource) getServletContext().getAttribute("dbcp");
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("UPDATE Task SET status=? WHERE id=?");
            var taskId = Integer.parseInt(req.getPathInfo().substring(1)); // remove "/" and get id
            stm.setInt(2, taskId);
            stm.setString(1, req.getParameter("status"));
            int affectedRow = stm.executeUpdate();
            if (affectedRow == 1) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                //getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid Task ID");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getPathInfo() == null || !req.getPathInfo().matches("/\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid uri");
            return;
        }
        resp.getWriter().println("delete method is working");

        BasicDataSource pool = (BasicDataSource) getServletContext().getAttribute("dbcp");
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("DELETE FROM Task WHERE id=?");
            var taskId = Integer.parseInt(req.getPathInfo().substring(1)); // remove "/" and get id
            stm.setInt(1, taskId);
            int affectedRow = stm.executeUpdate();
            if (affectedRow == 1) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                //getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid Task ID");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

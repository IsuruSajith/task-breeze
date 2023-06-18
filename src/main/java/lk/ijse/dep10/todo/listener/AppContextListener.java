package lk.ijse.dep10.todo.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        BasicDataSource dbcp = new BasicDataSource();
        dbcp.setUrl("jdbc:mysql://localhost:3306/dep10_task");
        dbcp.setUsername("root");
        dbcp.setPassword("1234");
        dbcp.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dbcp.setInitialSize(20);
        dbcp.setMaxTotal(100);
        sce.getServletContext().setAttribute("dbcp",dbcp);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        BasicDataSource dbcp = (BasicDataSource) sce.getServletContext().getAttribute("dbcp");
        try {
            dbcp.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}

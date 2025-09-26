package com.ajarastay.servlets;

import com.ajarastay.utils.DBUtil;
import com.ajarastay.utils.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet(name = "ReportServlet", urlPatterns = { "/report" })
public class ReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || !"admin".equals(s.getAttribute("role"))) { resp.sendError(401); return; }
        String type = req.getParameter("type");
        String sql;
        switch (type) {
            case "students":
                sql = "SELECT st.student_id, u.name, u.email, st.fee_status FROM students st JOIN users u ON st.user_id=u.id ORDER BY st.student_id";
                break;
            case "rooms":
                sql = "SELECT room_no, capacity, available_beds FROM rooms ORDER BY room_no";
                break;
            case "fees":
                sql = "SELECT st.student_id, u.name, f.amount, f.status FROM fees f JOIN students st ON f.student_id=st.student_id JOIN users u ON st.user_id=u.id ORDER BY f.fee_id DESC";
                break;
            default:
                resp.sendError(400);
                return;
        }
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<Map<String, Object>> list = new ArrayList<>();
            ResultSetMetaData md = rs.getMetaData();
            int col = md.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= col; i++) {
                    row.put(md.getColumnLabel(i), rs.getObject(i));
                }
                list.add(row);
            }
            JsonUtil.writeJson(resp, list);
        } catch (SQLException e) { resp.sendError(500, e.getMessage()); }
    }
}


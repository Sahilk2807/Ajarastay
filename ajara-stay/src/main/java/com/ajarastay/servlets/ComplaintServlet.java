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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ComplaintServlet", urlPatterns = { "/complaint" })
public class ComplaintServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        if (!"list".equalsIgnoreCase(action)) { resp.sendError(400); return; }
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) { resp.sendError(401); return; }
        boolean isAdmin = "admin".equals(session.getAttribute("role"));
        int userId = (Integer) session.getAttribute("userId");
        String sql = isAdmin ?
                "SELECT c.complaint_id, c.complaint_text, c.status FROM complaints c ORDER BY c.complaint_id DESC" :
                "SELECT c.complaint_id, c.complaint_text, c.status FROM complaints c JOIN students s ON c.student_id=s.student_id WHERE s.user_id=? ORDER BY c.complaint_id DESC";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (!isAdmin) ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("complaint_id", rs.getInt("complaint_id"));
                    m.put("complaint_text", rs.getString("complaint_text"));
                    m.put("status", rs.getString("status"));
                    list.add(m);
                }
                JsonUtil.writeJson(resp, list);
            }
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) { resp.sendError(401); return; }
        int userId = (Integer) session.getAttribute("userId");
        if ("add".equalsIgnoreCase(action)) {
            String text = req.getParameter("complaint_text");
            String sql = "INSERT INTO complaints(student_id, complaint_text, status) VALUES((SELECT student_id FROM students WHERE user_id=?), ?, 'open')";
            try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setString(2, text);
                ps.executeUpdate();
                resp.setStatus(204);
            } catch (SQLException e) { resp.sendError(500, e.getMessage()); }
        } else {
            resp.sendError(400);
        }
    }
}


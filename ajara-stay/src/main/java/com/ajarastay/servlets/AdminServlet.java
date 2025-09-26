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

@WebServlet(name = "AdminServlet", urlPatterns = { "/admin" })
public class AdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || !"admin".equals(s.getAttribute("role"))) { resp.sendError(401); return; }
        String entity = req.getParameter("entity");
        String action = req.getParameter("action");
        try (Connection c = DBUtil.getConnection()) {
            switch (entity) {
                case "student": {
                    String sql = "SELECT st.student_id, u.name, st.fee_status FROM students st JOIN users u ON st.user_id=u.id ORDER BY st.student_id DESC";
                    try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        while (rs.next()) {
                            Map<String, Object> m = new HashMap<>();
                            m.put("student_id", rs.getInt("student_id"));
                            m.put("name", rs.getString("name"));
                            m.put("fee_status", rs.getString("fee_status"));
                            list.add(m);
                        }
                        JsonUtil.writeJson(resp, list);
                    }
                    break;
                }
                case "room": {
                    String sql = "SELECT room_id, room_no, capacity, available_beds FROM rooms ORDER BY room_id DESC";
                    try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        while (rs.next()) {
                            Map<String, Object> m = new HashMap<>();
                            m.put("room_id", rs.getInt("room_id"));
                            m.put("room_no", rs.getString("room_no"));
                            m.put("capacity", rs.getInt("capacity"));
                            m.put("available_beds", rs.getInt("available_beds"));
                            list.add(m);
                        }
                        JsonUtil.writeJson(resp, list);
                    }
                    break;
                }
                case "complaint": {
                    String sql = "SELECT complaint_id, complaint_text, status FROM complaints ORDER BY complaint_id DESC";
                    try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
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
                    break;
                }
                default:
                    resp.sendError(400);
            }
        } catch (SQLException e) { resp.sendError(500, e.getMessage()); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || !"admin".equals(s.getAttribute("role"))) { resp.sendError(401); return; }
        String entity = req.getParameter("entity");
        String action = req.getParameter("action");
        try (Connection c = DBUtil.getConnection()) {
            switch (entity) {
                case "room": {
                    if ("create".equalsIgnoreCase(action)) {
                        try (PreparedStatement ps = c.prepareStatement("INSERT INTO rooms(room_no, capacity, available_beds) VALUES(?,?,?)")) {
                            ps.setString(1, req.getParameter("room_no"));
                            int cap = Integer.parseInt(req.getParameter("capacity"));
                            ps.setInt(2, cap);
                            ps.setInt(3, cap);
                            ps.executeUpdate();
                            resp.setStatus(204);
                        }
                    }
                    break;
                }
                case "fee": {
                    if ("update".equalsIgnoreCase(action)) {
                        try (PreparedStatement ps = c.prepareStatement("UPDATE fees f JOIN students s ON f.student_id = s.student_id SET f.status=? WHERE s.student_id=?")) {
                            ps.setString(1, req.getParameter("status"));
                            ps.setInt(2, Integer.parseInt(req.getParameter("student_id")));
                            ps.executeUpdate();
                            resp.setStatus(204);
                        }
                    }
                    break;
                }
                case "student": {
                    if ("delete".equalsIgnoreCase(action)) {
                        try (PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE student_id=?")) {
                            ps.setInt(1, Integer.parseInt(req.getParameter("student_id")));
                            ps.executeUpdate();
                            resp.setStatus(204);
                        }
                    }
                    break;
                }
                case "complaint": {
                    if ("resolve".equalsIgnoreCase(action)) {
                        try (PreparedStatement ps = c.prepareStatement("UPDATE complaints SET status='resolved' WHERE complaint_id=?")) {
                            ps.setInt(1, Integer.parseInt(req.getParameter("complaint_id")));
                            ps.executeUpdate();
                            resp.setStatus(204);
                        }
                    }
                    break;
                }
                default:
                    resp.sendError(400);
            }
        } catch (SQLException e) { resp.sendError(500, e.getMessage()); }
    }
}


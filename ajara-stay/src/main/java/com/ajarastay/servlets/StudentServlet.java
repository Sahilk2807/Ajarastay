package com.ajarastay.servlets;

import com.ajarastay.utils.DBUtil;
import com.ajarastay.utils.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "StudentServlet", urlPatterns = { "/student" })
public class StudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"student".equals(session.getAttribute("role"))) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String action = req.getParameter("action");
        if ("profile".equalsIgnoreCase(action)) {
            handleProfile(resp, (Integer) session.getAttribute("userId"));
        } else if ("room".equalsIgnoreCase(action)) {
            handleRoom(resp, (Integer) session.getAttribute("userId"));
        } else if ("fees".equalsIgnoreCase(action)) {
            handleFees(resp, (Integer) session.getAttribute("userId"));
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleProfile(HttpServletResponse resp, int userId) throws IOException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT name, email, phone FROM users WHERE id=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", rs.getString("name"));
                    m.put("email", rs.getString("email"));
                    m.put("phone", rs.getString("phone"));
                    JsonUtil.writeJson(resp, m);
                    return;
                }
            }
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        }
    }

    private void handleRoom(HttpServletResponse resp, int userId) throws IOException {
        String sql = "SELECT r.room_no, r.capacity, r.available_beds FROM students s LEFT JOIN rooms r ON s.room_id = r.room_id WHERE s.user_id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Object> m = new HashMap<>();
                if (rs.next()) {
                    m.put("room_no", rs.getString("room_no"));
                    m.put("capacity", rs.getInt("capacity"));
                    m.put("available_beds", rs.getInt("available_beds"));
                }
                JsonUtil.writeJson(resp, m);
            }
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        }
    }

    private void handleFees(HttpServletResponse resp, int userId) throws IOException {
        String sql = "SELECT f.amount, f.status FROM fees f JOIN students s ON f.student_id = s.student_id WHERE s.user_id = ? ORDER BY f.fee_id DESC LIMIT 1";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Object> m = new HashMap<>();
                if (rs.next()) {
                    m.put("amount", rs.getBigDecimal("amount"));
                    m.put("status", rs.getString("status"));
                }
                JsonUtil.writeJson(resp, m);
            }
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        }
    }
}


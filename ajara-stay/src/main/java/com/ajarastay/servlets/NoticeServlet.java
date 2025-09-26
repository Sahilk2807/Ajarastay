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

@WebServlet(name = "NoticeServlet", urlPatterns = { "/notice" })
public class NoticeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sql = "SELECT notice_id, title, description, date_posted FROM notices ORDER BY notice_id DESC LIMIT 50";
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("notice_id", rs.getInt("notice_id"));
                m.put("title", rs.getString("title"));
                m.put("description", rs.getString("description"));
                m.put("date_posted", rs.getTimestamp("date_posted"));
                list.add(m);
            }
            JsonUtil.writeJson(resp, list);
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || !"admin".equals(s.getAttribute("role"))) { resp.sendError(401); return; }
        String title = req.getParameter("title");
        String desc = req.getParameter("description");
        String sql = "INSERT INTO notices(title, description) VALUES(?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, desc);
            ps.executeUpdate();
            resp.setStatus(204);
        } catch (SQLException e) { resp.sendError(500, e.getMessage()); }
    }
}


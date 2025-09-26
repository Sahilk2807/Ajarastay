package com.ajarastay.servlets;

import com.ajarastay.utils.DBUtil;
import com.ajarastay.utils.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;

@WebServlet(name = "AuthServlet", urlPatterns = { "/auth" })
public class AuthServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("signup".equalsIgnoreCase(action)) {
            handleSignup(req, resp);
        } else if ("login".equalsIgnoreCase(action)) {
            handleLogin(req, resp);
        } else if ("logout".equalsIgnoreCase(action)) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            resp.sendRedirect("index.html");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
        }
    }

    private void handleSignup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String prn = req.getParameter("prn");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String role = "student";

        if (name == null || email == null || password == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing fields");
            return;
        }

        String hash = PasswordUtil.hash(password);
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users(name, prn, email, phone, role, password_hash) VALUES(?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, prn);
                ps.setString(3, email);
                ps.setString(4, phone);
                ps.setString(5, role);
                ps.setString(6, hash);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                int userId = -1;
                if (rs.next()) userId = rs.getInt(1);

                try (PreparedStatement ps2 = conn.prepareStatement(
                        "INSERT INTO students(user_id, room_id, fee_status, complaint_status) VALUES(?, NULL, 'pending', 'none')")) {
                    ps2.setInt(1, userId);
                    ps2.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                resp.sendError(500, e.getMessage());
                return;
            }
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
            return;
        }
        resp.sendRedirect("student.html");
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, name, role, password_hash FROM users WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password_hash");
                    if (PasswordUtil.verify(password, hash) && rs.getString("role").equals(role)) {
                        HttpSession session = req.getSession(true);
                        session.setAttribute("userId", rs.getInt("id"));
                        session.setAttribute("name", rs.getString("name"));
                        session.setAttribute("role", rs.getString("role"));
                        if ("admin".equals(role)) {
                            resp.sendRedirect("admin.html");
                        } else {
                            resp.sendRedirect("student.html");
                        }
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
            return;
        }
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
    }
}


package com.se.demorestdb.repo;

import com.se.demorestdb.model.DanhMuc;
import jakarta.enterprise.context.ApplicationScoped;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DanhMucRepo {
    private DataSource dataSource;

    private DataSource getDataSource() {
        if (dataSource == null) {
            try {
                dataSource = (DataSource) new InitialContext()
                        .lookup("java:comp/env/jdbc/quanlytintuc");
            } catch (NamingException e) {
                throw new RuntimeException("Khong tim thay DataSource jdbc/quanlytintuc", e);
            }
        }
        return dataSource;
    }

    public List<DanhMuc> findAll() {
        String sql = "SELECT MADM, TENDANHMUC, NGUOIQUANLY, GHICHU FROM DANHMUC ORDER BY MADM";
        List<DanhMuc> result = new ArrayList<>();
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DanhMuc findById(int id) {
        String sql = "SELECT MADM, TENDANHMUC, NGUOIQUANLY, GHICHU FROM DANHMUC WHERE MADM = ?";
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DanhMuc insert(DanhMuc danhMuc) {
        String sql = "INSERT INTO DANHMUC(TENDANHMUC, NGUOIQUANLY, GHICHU) VALUES (?, ?, ?)";
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setValues(ps, danhMuc);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    danhMuc.setMaDM(keys.getInt(1));
                }
            }
            return danhMuc;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(int id, DanhMuc danhMuc) {
        String sql = "UPDATE DANHMUC SET TENDANHMUC = ?, NGUOIQUANLY = ?, GHICHU = ? WHERE MADM = ?";
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setValues(ps, danhMuc);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id) {
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM DANHMUC WHERE MADM = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setValues(PreparedStatement ps, DanhMuc danhMuc) throws SQLException {
        ps.setString(1, danhMuc.getTenDanhMuc());
        ps.setString(2, danhMuc.getNguoiQuanLy());
        ps.setString(3, danhMuc.getGhiChu());
    }

    private DanhMuc map(ResultSet rs) throws SQLException {
        return new DanhMuc(
                rs.getInt("MADM"),
                rs.getString("TENDANHMUC"),
                rs.getString("NGUOIQUANLY"),
                rs.getString("GHICHU")
        );
    }
}

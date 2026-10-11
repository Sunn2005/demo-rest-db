package com.se.demorestdb.repo;

import com.se.demorestdb.dto.ThongKeDanhMucDto;
import com.se.demorestdb.dto.TinTucDanhMucDto;
import com.se.demorestdb.model.TinTuc;
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
public class TinTucRepo {
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

    public List<TinTuc> findAll() {
        String sql = "SELECT MATT, TIEUDE, NOIDUNGTT, LIENKET, MADM FROM TINTUC ORDER BY MATT";
        List<TinTuc> result = new ArrayList<>();
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapTinTuc(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public TinTuc findById(int id) {
        String sql = "SELECT MATT, TIEUDE, NOIDUNGTT, LIENKET, MADM FROM TINTUC WHERE MATT = ?";
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapTinTuc(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public TinTuc insert(TinTuc tinTuc) {
        String sql = "INSERT INTO TINTUC(TIEUDE, NOIDUNGTT, LIENKET, MADM) VALUES (?, ?, ?, ?)";
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setValues(ps, tinTuc);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    tinTuc.setMaTT(keys.getInt(1));
                }
            }
            return tinTuc;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(int id, TinTuc tinTuc) {
        String sql = "UPDATE TINTUC SET TIEUDE = ?, NOIDUNGTT = ?, LIENKET = ?, MADM = ? WHERE MATT = ?";
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setValues(ps, tinTuc);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id) {
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TINTUC WHERE MATT = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TinTucDanhMucDto> findAllWithCategory() {
        String sql = "SELECT t.MATT, t.TIEUDE, t.NOIDUNGTT, d.TENDANHMUC " +
                "FROM TINTUC t INNER JOIN DANHMUC d ON t.MADM = d.MADM ORDER BY t.MATT";
        return queryNewsWithCategory(sql, null);
    }

    public List<TinTucDanhMucDto> findByCategoryName(String tenDanhMuc) {
        String sql = "SELECT t.MATT, t.TIEUDE, t.NOIDUNGTT, d.TENDANHMUC " +
                "FROM TINTUC t INNER JOIN DANHMUC d ON t.MADM = d.MADM " +
                "WHERE d.TENDANHMUC = ? ORDER BY t.MATT";
        return queryNewsWithCategory(sql, tenDanhMuc);
    }

    public List<ThongKeDanhMucDto> countByCategory() {
        String sql = "SELECT d.MADM, d.TENDANHMUC, COUNT(t.MATT) AS SOLUONG_TINTUC " +
                "FROM DANHMUC d LEFT JOIN TINTUC t ON d.MADM = t.MADM " +
                "GROUP BY d.MADM, d.TENDANHMUC ORDER BY d.MADM";
        return queryCategoryStatistics(sql, null);
    }

    public List<ThongKeDanhMucDto> categoriesWithAtLeast(int minimum) {
        String sql = "SELECT d.MADM, d.TENDANHMUC, COUNT(t.MATT) AS SOLUONG_TINTUC " +
                "FROM DANHMUC d INNER JOIN TINTUC t ON d.MADM = t.MADM " +
                "GROUP BY d.MADM, d.TENDANHMUC HAVING COUNT(t.MATT) >= ? ORDER BY d.MADM";
        return queryCategoryStatistics(sql, minimum);
    }

    public List<ThongKeDanhMucDto> categoriesWithMostNews() {
        String sql = "SELECT d.MADM, d.TENDANHMUC, COUNT(t.MATT) AS SOLUONG_TINTUC " +
                "FROM DANHMUC d LEFT JOIN TINTUC t ON d.MADM = t.MADM " +
                "GROUP BY d.MADM, d.TENDANHMUC HAVING COUNT(t.MATT) = (" +
                "SELECT MAX(SoLuong) FROM (SELECT COUNT(t2.MATT) AS SoLuong " +
                "FROM DANHMUC d2 LEFT JOIN TINTUC t2 ON d2.MADM = t2.MADM " +
                "GROUP BY d2.MADM) AS ThongKeMax) ORDER BY d.MADM";
        return queryCategoryStatistics(sql, null);
    }

    private List<TinTucDanhMucDto> queryNewsWithCategory(String sql, String categoryName) {
        List<TinTucDanhMucDto> result = new ArrayList<>();
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (categoryName != null) {
                ps.setString(1, categoryName);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new TinTucDanhMucDto(
                            rs.getInt("MATT"),
                            rs.getString("TIEUDE"),
                            rs.getString("NOIDUNGTT"),
                            rs.getString("TENDANHMUC")
                    ));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ThongKeDanhMucDto> queryCategoryStatistics(String sql, Integer minimum) {
        List<ThongKeDanhMucDto> result = new ArrayList<>();
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (minimum != null) {
                ps.setInt(1, minimum);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new ThongKeDanhMucDto(
                            rs.getInt("MADM"),
                            rs.getString("TENDANHMUC"),
                            rs.getLong("SOLUONG_TINTUC")
                    ));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setValues(PreparedStatement ps, TinTuc tinTuc) throws SQLException {
        ps.setString(1, tinTuc.getTieuDe());
        ps.setString(2, tinTuc.getNoiDungTT());
        ps.setString(3, tinTuc.getLienKet());
        ps.setInt(4, tinTuc.getMaDM());
    }

    private TinTuc mapTinTuc(ResultSet rs) throws SQLException {
        return new TinTuc(
                rs.getInt("MATT"),
                rs.getString("TIEUDE"),
                rs.getString("NOIDUNGTT"),
                rs.getString("LIENKET"),
                rs.getInt("MADM")
        );
    }
}

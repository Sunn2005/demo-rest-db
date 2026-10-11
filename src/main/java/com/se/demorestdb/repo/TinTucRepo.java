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

/**
 * Tầng truy cập dữ liệu cho tin tức và các truy vấn kết hợp với danh mục.
 * {@code TinTucService} gọi các hàm public của lớp này để xử lý CRUD, lọc tin và thống kê.
 */
@ApplicationScoped
public class TinTucRepo {
    private DataSource dataSource;

    /**
     * Lấy DataSource {@code quanlytintuc} từ JNDI và lưu lại để các hàm truy vấn tái sử dụng.
     *
     * @return nguồn kết nối tới database quản lý tin tức
     */
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

    /**
     * Lấy toàn bộ tin tức, sắp xếp tăng dần theo {@code MATT}.
     * Hàm phục vụ {@code TinTucService.findAll()} và dùng {@link #mapTinTuc(ResultSet)}
     * để chuyển từng dòng kết quả thành model.
     *
     * @return danh sách tin tức; danh sách rỗng nếu chưa có dữ liệu
     */
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

    /**
     * Tìm một tin tức theo khóa chính.
     *
     * @param id mã tin tức cần tìm
     * @return tin tức tương ứng, hoặc {@code null} nếu không tồn tại
     */
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

    /**
     * Thêm tin tức mới và gán mã tự tăng do database sinh vào đối tượng đầu vào.
     * {@code TinTucService.insert()} sẽ kiểm tra danh mục tồn tại trước khi gọi hàm này.
     *
     * @param tinTuc dữ liệu tin tức cần thêm
     * @return đối tượng đã thêm, bao gồm {@code maTT} vừa được sinh
     */
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

    /**
     * Cập nhật tin tức theo mã. Hàm dùng {@link #setValues(PreparedStatement, TinTuc)}
     * để gán bốn trường dữ liệu chung với thao tác thêm mới.
     *
     * @param id mã tin tức cần cập nhật
     * @param tinTuc dữ liệu mới của tin tức
     * @return {@code true} nếu có bản ghi được cập nhật, {@code false} nếu mã không tồn tại
     */
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

    /**
     * Xóa một tin tức theo khóa chính; được {@code TinTucService.delete()} gọi trực tiếp.
     *
     * @param id mã tin tức cần xóa
     * @return {@code true} nếu xóa thành công, {@code false} nếu không tìm thấy bản ghi
     */
    public boolean delete(int id) {
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TINTUC WHERE MATT = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lấy tất cả tin tức kèm tên danh mục bằng phép INNER JOIN.
     * Hàm ủy quyền phần chạy SQL và ánh xạ DTO cho {@link #queryNewsWithCategory(String, String)}.
     *
     * @return danh sách dữ liệu tin tức - danh mục để trả về API
     */
    public List<TinTucDanhMucDto> findAllWithCategory() {
        String sql = "SELECT t.MATT, t.TIEUDE, t.NOIDUNGTT, d.TENDANHMUC " +
                "FROM TINTUC t INNER JOIN DANHMUC d ON t.MADM = d.MADM ORDER BY t.MATT";
        return queryNewsWithCategory(sql, null);
    }

    /**
     * Lọc các tin tức theo đúng tên danh mục.
     * Tên danh mục được truyền vào PreparedStatement thông qua helper
     * {@link #queryNewsWithCategory(String, String)} để tránh nối chuỗi SQL.
     *
     * @param tenDanhMuc tên danh mục cần lọc
     * @return các tin tức thuộc danh mục đó
     */
    public List<TinTucDanhMucDto> findByCategoryName(String tenDanhMuc) {
        String sql = "SELECT t.MATT, t.TIEUDE, t.NOIDUNGTT, d.TENDANHMUC " +
                "FROM TINTUC t INNER JOIN DANHMUC d ON t.MADM = d.MADM " +
                "WHERE d.TENDANHMUC = ? ORDER BY t.MATT";
        return queryNewsWithCategory(sql, tenDanhMuc);
    }

    /**
     * Thống kê số tin của từng danh mục. LEFT JOIN đảm bảo danh mục chưa có tin vẫn xuất hiện với số lượng 0.
     *
     * @return danh sách số lượng tin theo từng danh mục
     */
    public List<ThongKeDanhMucDto> countByCategory() {
        String sql = "SELECT d.MADM, d.TENDANHMUC, COUNT(t.MATT) AS SOLUONG_TINTUC " +
                "FROM DANHMUC d LEFT JOIN TINTUC t ON d.MADM = t.MADM " +
                "GROUP BY d.MADM, d.TENDANHMUC ORDER BY d.MADM";
        return queryCategoryStatistics(sql, null);
    }

    /**
     * Lấy những danh mục có số tin lớn hơn hoặc bằng ngưỡng yêu cầu.
     * Hàm dùng {@link #queryCategoryStatistics(String, Integer)} để gán tham số cho mệnh đề HAVING.
     *
     * @param minimum số tin tối thiểu
     * @return các danh mục đạt ngưỡng cùng số lượng tin tương ứng
     */
    public List<ThongKeDanhMucDto> categoriesWithAtLeast(int minimum) {
        String sql = "SELECT d.MADM, d.TENDANHMUC, COUNT(t.MATT) AS SOLUONG_TINTUC " +
                "FROM DANHMUC d INNER JOIN TINTUC t ON d.MADM = t.MADM " +
                "GROUP BY d.MADM, d.TENDANHMUC HAVING COUNT(t.MATT) >= ? ORDER BY d.MADM";
        return queryCategoryStatistics(sql, minimum);
    }

    /**
     * Tìm danh mục có nhiều tin nhất. Nếu nhiều danh mục đồng hạng, hàm trả về tất cả các danh mục đó.
     *
     * @return danh sách danh mục có số tin lớn nhất
     */
    public List<ThongKeDanhMucDto> categoriesWithMostNews() {
        String sql = "SELECT d.MADM, d.TENDANHMUC, COUNT(t.MATT) AS SOLUONG_TINTUC " +
                "FROM DANHMUC d LEFT JOIN TINTUC t ON d.MADM = t.MADM " +
                "GROUP BY d.MADM, d.TENDANHMUC HAVING COUNT(t.MATT) = (" +
                "SELECT MAX(SoLuong) FROM (SELECT COUNT(t2.MATT) AS SoLuong " +
                "FROM DANHMUC d2 LEFT JOIN TINTUC t2 ON d2.MADM = t2.MADM " +
                "GROUP BY d2.MADM) AS ThongKeMax) ORDER BY d.MADM";
        return queryCategoryStatistics(sql, null);
    }

    /**
     * Helper chạy các truy vấn trả về tin tức kèm tên danh mục.
     * Được {@link #findAllWithCategory()} và {@link #findByCategoryName(String)} dùng chung;
     * chỉ gán tham số lọc khi {@code categoryName} khác {@code null}.
     */
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

    /**
     * Helper chạy các truy vấn thống kê và ánh xạ kết quả sang {@link ThongKeDanhMucDto}.
     * Hỗ trợ {@link #countByCategory()}, {@link #categoriesWithAtLeast(int)} và
     * {@link #categoriesWithMostNews()}.
     */
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

    /**
     * Gán bốn thuộc tính có thể ghi của {@link TinTuc} vào PreparedStatement.
     * Helper này được {@link #insert(TinTuc)} và {@link #update(int, TinTuc)} dùng chung.
     */
    private void setValues(PreparedStatement ps, TinTuc tinTuc) throws SQLException {
        ps.setString(1, tinTuc.getTieuDe());
        ps.setString(2, tinTuc.getNoiDungTT());
        ps.setString(3, tinTuc.getLienKet());
        ps.setInt(4, tinTuc.getMaDM());
    }

    /**
     * Chuyển dòng hiện tại của ResultSet thành model {@link TinTuc}.
     * Helper này hỗ trợ {@link #findAll()} và {@link #findById(int)}.
     */
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

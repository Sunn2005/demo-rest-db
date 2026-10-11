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

/**
 * Tầng truy cập dữ liệu cho bảng {@code DANHMUC}.
 * Các hàm public của lớp này chủ yếu được {@code DanhMucService} gọi để thực hiện CRUD danh mục;
 * riêng {@link #findById(int)} còn hỗ trợ {@code TinTucService} kiểm tra danh mục có tồn tại
 * trước khi thêm hoặc cập nhật tin tức.
 */
@ApplicationScoped
public class DanhMucRepo {
    private DataSource dataSource;

    /**
     * Lấy DataSource đã cấu hình trên application server qua JNDI.
     * DataSource được lưu lại sau lần tra cứu đầu tiên để tất cả các hàm truy vấn bên dưới tái sử dụng.
     *
     * @return nguồn kết nối tới database {@code quanlytintuc}
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
     * Lấy toàn bộ danh mục, sắp xếp tăng dần theo mã danh mục.
     * Hàm này phục vụ {@code DanhMucService.findAll()} và dùng {@link #map(ResultSet)}
     * để chuyển từng dòng dữ liệu thành đối tượng {@link DanhMuc}.
     *
     * @return danh sách danh mục; trả về danh sách rỗng nếu bảng chưa có dữ liệu
     */
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

    /**
     * Tìm một danh mục theo khóa chính {@code MADM}.
     * Ngoài màn hình xem/sửa danh mục, hàm này còn giúp {@code TinTucService} xác thực khóa ngoại
     * trước khi ghi tin tức vào bảng {@code TINTUC}.
     *
     * @param id mã danh mục cần tìm
     * @return danh mục tương ứng, hoặc {@code null} nếu không tồn tại
     */
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

    /**
     * Thêm một danh mục mới và gán khóa chính do database sinh ra vào chính đối tượng đầu vào.
     * Hàm dùng {@link #setValues(PreparedStatement, DanhMuc)} để truyền các giá trị chung vào câu SQL.
     *
     * @param danhMuc dữ liệu danh mục cần thêm
     * @return đối tượng đã thêm, bao gồm {@code maDM} vừa được sinh
     */
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

    /**
     * Cập nhật thông tin danh mục có mã được chỉ định.
     * Giá trị boolean giúp {@code DanhMucService.update()} phân biệt cập nhật thành công với trường hợp
     * không tìm thấy bản ghi.
     *
     * @param id mã danh mục cần cập nhật
     * @param danhMuc dữ liệu mới của danh mục
     * @return {@code true} nếu có ít nhất một dòng được cập nhật, ngược lại là {@code false}
     */
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

    /**
     * Xóa danh mục theo khóa chính. Hàm được {@code DanhMucService.delete()} gọi trực tiếp.
     *
     * @param id mã danh mục cần xóa
     * @return {@code true} nếu xóa được một bản ghi, {@code false} nếu mã không tồn tại
     */
    public boolean delete(int id) {
        try (Connection con = getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM DANHMUC WHERE MADM = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gán các thuộc tính của {@link DanhMuc} vào ba tham số đầu của PreparedStatement.
     * Helper này được cả {@link #insert(DanhMuc)} và {@link #update(int, DanhMuc)} dùng chung
     * để tránh lặp thứ tự ánh xạ cột.
     */
    private void setValues(PreparedStatement ps, DanhMuc danhMuc) throws SQLException {
        ps.setString(1, danhMuc.getTenDanhMuc());
        ps.setString(2, danhMuc.getNguoiQuanLy());
        ps.setString(3, danhMuc.getGhiChu());
    }

    /**
     * Chuyển dòng hiện tại của ResultSet thành model {@link DanhMuc}.
     * Helper này hỗ trợ {@link #findAll()} và {@link #findById(int)}.
     */
    private DanhMuc map(ResultSet rs) throws SQLException {
        return new DanhMuc(
                rs.getInt("MADM"),
                rs.getString("TENDANHMUC"),
                rs.getString("NGUOIQUANLY"),
                rs.getString("GHICHU")
        );
    }
}

# REST API quản lý tin tức

## Luồng xử lý

`Postman -> Resource -> Service -> Repo -> DataSource -> MariaDB`

- `model`: ánh xạ bảng `DANHMUC`, `TINTUC`.
- `dto`: kết quả JOIN và thống kê, không phải bảng trong database.
- `repo`: câu SQL và ánh xạ `ResultSet`.
- `service`: gọi repo và kiểm tra `MADM` khi thêm/sửa tin tức.
- `resource`: URL, HTTP method, status code và JSON.

## Chạy dự án

1. Chạy file `data/quanlytintuc.sql` trong MariaDB.
2. Sửa username/password trong `src/main/webapp/META-INF/context.xml` nếu cần.
3. Deploy dự án lên Tomcat 10.1+.
4. Base URL mặc định:

```text
http://localhost:8080/demo-rest-db-1.0-SNAPSHOT/api
```

Nếu artifact/context path trong Tomcat khác thì thay phần
`demo-rest-db-1.0-SNAPSHOT`.

## CRUD DanhMuc

| Method | URL | Kết quả |
|---|---|---|
| GET | `/danhmuc` | Danh sách |
| GET | `/danhmuc/{id}` | Chi tiết hoặc 404 |
| POST | `/danhmuc` | Thêm mới, trả 201 |
| PUT | `/danhmuc/{id}` | Cập nhật hoặc 404 |
| DELETE | `/danhmuc/{id}` | Xóa, trả 204 hoặc 404 |

JSON dùng cho POST/PUT:

```json
{
  "tenDanhMuc": "Giáo dục",
  "nguoiQuanLy": "Nguyễn Văn A",
  "ghiChu": "Tin giáo dục"
}
```

Không cần gửi `maDM` khi POST; ID được database tự tạo.

## CRUD TinTuc

| Method | URL | Kết quả |
|---|---|---|
| GET | `/tintuc` | Danh sách |
| GET | `/tintuc/{id}` | Chi tiết hoặc 404 |
| POST | `/tintuc` | Thêm mới, trả 201 |
| PUT | `/tintuc/{id}` | Cập nhật hoặc 404 |
| DELETE | `/tintuc/{id}` | Xóa, trả 204 hoặc 404 |

JSON dùng cho POST/PUT:

```json
{
  "tieuDe": "Tiêu đề mới",
  "noiDungTT": "Nội dung tin tức",
  "lienKet": "https://example.com/tin-moi",
  "maDM": 1
}
```

`maDM` phải tồn tại trong bảng `DANHMUC`.

## Năm truy vấn trong PDF

| Câu | Method | URL |
|---|---|---|
| Tin tức kèm tên danh mục | GET | `/tintuc/queries/danh-muc` |
| Tin theo tên danh mục | GET | `/tintuc/queries/theo-danh-muc?ten=Thời%20sự` |
| Đếm tin theo danh mục | GET | `/tintuc/queries/thong-ke` |
| Danh mục có từ N tin | GET | `/tintuc/queries/tu-n-tin?min=3` |
| Danh mục có nhiều tin nhất | GET | `/tintuc/queries/nhieu-tin-nhat` |

## Thứ tự test nhanh trong Postman

1. `GET /danhmuc` để kiểm tra DataSource.
2. `POST /danhmuc`, lưu lại `maDM` mới.
3. `POST /tintuc` với `maDM` đó, lưu lại `maTT`.
4. `GET`, `PUT`, `GET` lại để kiểm tra cập nhật.
5. Gọi 5 API `/tintuc/queries`.
6. Xóa tin tức trước, sau đó mới xóa danh mục vì có khóa ngoại.

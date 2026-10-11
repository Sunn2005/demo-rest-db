package com.se.demorestdb.dto;

public class ThongKeDanhMucDto {
    private int maDM;
    private String tenDanhMuc;
    private long soLuongTinTuc;

    public ThongKeDanhMucDto() {
    }

    public ThongKeDanhMucDto(int maDM, String tenDanhMuc, long soLuongTinTuc) {
        this.maDM = maDM;
        this.tenDanhMuc = tenDanhMuc;
        this.soLuongTinTuc = soLuongTinTuc;
    }

    public int getMaDM() {
        return maDM;
    }

    public void setMaDM(int maDM) {
        this.maDM = maDM;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public long getSoLuongTinTuc() {
        return soLuongTinTuc;
    }

    public void setSoLuongTinTuc(long soLuongTinTuc) {
        this.soLuongTinTuc = soLuongTinTuc;
    }
}

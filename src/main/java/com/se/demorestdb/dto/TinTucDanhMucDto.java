package com.se.demorestdb.dto;

public class TinTucDanhMucDto {
    private int maTT;
    private String tieuDe;
    private String noiDungTT;
    private String tenDanhMuc;

    public TinTucDanhMucDto() {
    }

    public TinTucDanhMucDto(int maTT, String tieuDe, String noiDungTT, String tenDanhMuc) {
        this.maTT = maTT;
        this.tieuDe = tieuDe;
        this.noiDungTT = noiDungTT;
        this.tenDanhMuc = tenDanhMuc;
    }

    public int getMaTT() {
        return maTT;
    }

    public void setMaTT(int maTT) {
        this.maTT = maTT;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDungTT() {
        return noiDungTT;
    }

    public void setNoiDungTT(String noiDungTT) {
        this.noiDungTT = noiDungTT;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }
}

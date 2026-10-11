package com.se.demorestdb.service;

import com.se.demorestdb.model.DanhMuc;
import com.se.demorestdb.repo.DanhMucRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class DanhMucService {
    @Inject
    private DanhMucRepo danhMucRepo;

    public List<DanhMuc> findAll() {
        return danhMucRepo.findAll();
    }

    public DanhMuc findById(int id) {
        return danhMucRepo.findById(id);
    }

    public DanhMuc insert(DanhMuc danhMuc) {
        return danhMucRepo.insert(danhMuc);
    }

    public DanhMuc update(int id, DanhMuc danhMuc) {
        if (!danhMucRepo.update(id, danhMuc)) {
            return null;
        }
        danhMuc.setMaDM(id);
        return danhMuc;
    }

    public boolean delete(int id) {
        return danhMucRepo.delete(id);
    }
}

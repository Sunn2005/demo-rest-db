package com.se.demorestdb.service;

import com.se.demorestdb.dto.ThongKeDanhMucDto;
import com.se.demorestdb.dto.TinTucDanhMucDto;
import com.se.demorestdb.model.TinTuc;
import com.se.demorestdb.repo.DanhMucRepo;
import com.se.demorestdb.repo.TinTucRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class TinTucService {
    @Inject
    private TinTucRepo tinTucRepo;

    @Inject
    private DanhMucRepo danhMucRepo;

    public List<TinTuc> findAll() {
        return tinTucRepo.findAll();
    }

    public TinTuc findById(int id) {
        return tinTucRepo.findById(id);
    }

    public TinTuc insert(TinTuc tinTuc) {
        if (danhMucRepo.findById(tinTuc.getMaDM()) == null) {
            return null;
        }
        return tinTucRepo.insert(tinTuc);
    }

    public TinTuc update(int id, TinTuc tinTuc) {
        if (danhMucRepo.findById(tinTuc.getMaDM()) == null || !tinTucRepo.update(id, tinTuc)) {
            return null;
        }
        tinTuc.setMaTT(id);
        return tinTuc;
    }

    public boolean delete(int id) {
        return tinTucRepo.delete(id);
    }

    public List<TinTucDanhMucDto> findAllWithCategory() {
        return tinTucRepo.findAllWithCategory();
    }

    public List<TinTucDanhMucDto> findByCategoryName(String tenDanhMuc) {
        return tinTucRepo.findByCategoryName(tenDanhMuc);
    }

    public List<ThongKeDanhMucDto> countByCategory() {
        return tinTucRepo.countByCategory();
    }

    public List<ThongKeDanhMucDto> categoriesWithAtLeast(int minimum) {
        return tinTucRepo.categoriesWithAtLeast(minimum);
    }

    public List<ThongKeDanhMucDto> categoriesWithMostNews() {
        return tinTucRepo.categoriesWithMostNews();
    }
}

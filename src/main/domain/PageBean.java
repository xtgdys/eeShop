package main.domain;

import java.util.List;

public class PageBean<T> {
    private int currentPage;
    private int currentCount;
    private int totalCount;
    private int totalPage;
    private List<T> list;

    public PageBean() {
    }

    public PageBean(int currentPage, int currentCount, int totalCount, int totalPage, List<T> list) {
        this.currentPage = currentPage;
        this.currentCount = currentCount;
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.list = list;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}

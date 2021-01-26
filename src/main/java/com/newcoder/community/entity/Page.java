package com.newcoder.community.entity;

import lombok.Data;

/**
 * 封装分页
 */

public class Page {

    // 当前页码
    private int current = 1;

    // 显示上限
    private int limit = 10;

    // 数据总数(用于计算总页数)
    private int rows;

    // 查询路径(用于复用分页链接),点击页面按钮实质是链接
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >=1 && limit <=100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0 ){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页面的在数据库的起始行
     */
    public int getOffset(){
        return (current-1)*limit;
    }

    /**
     * 获取总的页面数,因为页面底下要显示页码，必须控制在数据库所有记录条数的范围内
     */
    public int getTotal(){
            if(rows % limit == 0){
                return rows/limit;
            }else{
                return rows/limit + 1;
        }
    }

    /**
     * 获取页面下方的起始页码和末尾页码，因为数据库可能有1000条，不可能显示1000个页码
     */
    public int getFrom(){

        int from = current - 2;
        return from < 1 ? 1 : from;
    }
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}

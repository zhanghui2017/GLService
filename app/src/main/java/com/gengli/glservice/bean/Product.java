package com.gengli.glservice.bean;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    /**
     * 产品id
     */
    private int id;
    /**
     * 产品标题
     */
    private String name;
    /**
     * 产品简介
     */
    private String desc;
    /**
     * 产品缩略图
     */
    private String imgUrl;
    /**
     * 产品是否是新品
     */
    private boolean isNew;
    /**
     * 产品是否是热销
     */
    private boolean isHot;
    /**
     * 产品用途
     */
    private String scope;
    /**
     * 产品尺寸
     */
    private String size;
    /**
     * 产品重量
     */
    private String weight;
    /**
     * 产品型号
     */
    private String model;
    /**
     * 产品详情介绍
     */
    private String content;
    /**
     * 产品技术指标
     */
    private String params;

    /**
     * 产品大图
     */
    List<String> imgList;

    /**
     * 产品相关案例
     */
    List<Article> articles;

    /**
     * 相关配件
     */
    List<Fitting> fittings;

    private String techImg;


    public String getTechImg() {
        return techImg;
    }

    public void setTechImg(String techImg) {
        this.techImg = techImg;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public List<Fitting> getFittings() {
        return fittings;
    }

    public void setFittings(List<Fitting> fittings) {
        this.fittings = fittings;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}

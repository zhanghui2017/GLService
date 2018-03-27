package com.gengli.glservice.bean;

import java.io.Serializable;

public class Order implements Serializable {

    /**
     * 订单ID
     */
    private String Id;
    /**
     * 客户地址
     */
    private String Address;
    /**
     * 客户公司
     */
    private String Company;
    /**
     * 维修设备型号
     */
    private String Machine;

    /**
     * 问题描述，设备故障
     */
    private String Trouble;
    /**
     * 客户姓名
     */
    private String Name;
    /**
     * 客户联系电话
     */
    private String Phone;
    /**
     * 订单状态：
     * 1，待接单
     * 2，维修中
     * 3，已完成
     */
    private int type;
    /**
     * 产品编号
     */
    private String model;
    /**
     * 工单类型
     * 1，
     * 2，
     * 3，
     * 4，
     */
    private String level;
    /**
     * 下单时间
     */
    private String time;
    /**
     * 维修责任人姓名
     */
    private String chargeName;
    /**
     * 维修责任人电话
     */
    private String chargePhone;
    /**
     * 快件地址
     */
    private String expressAddress;
    /**
     * 接单时间
     */
    private String startTime;
    /**
     * 订单结束时间
     */
    private String endTime;
    /**
     * 维修描述
     */
    private String desc;
    /**
     * 维修需要配件
     */
    private String fitting;

    private String buyTime;

    private boolean is_comment;

    private boolean is_emerg;


    public boolean is_emerg() {
        return is_emerg;
    }

    public void setIs_emerg(boolean is_emerg) {
        this.is_emerg = is_emerg;
    }

    public boolean is_comment() {
        return is_comment;
    }

    public void setIs_comment(boolean is_comment) {
        this.is_comment = is_comment;
    }

    public String getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(String buyTime) {
        this.buyTime = buyTime;
    }

    public String getFitting() {
        return fitting;
    }

    public void setFitting(String fitting) {
        this.fitting = fitting;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public String getChargePhone() {
        return chargePhone;
    }

    public void setChargePhone(String chargePhone) {
        this.chargePhone = chargePhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getMachine() {
        return Machine;
    }

    public void setMachine(String machine) {
        Machine = machine;
    }

    public String getTrouble() {
        return Trouble;
    }

    public void setTrouble(String trouble) {
        Trouble = trouble;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getExpressAddress() {
        return expressAddress;
    }

    public void setExpressAddress(String expressAddress) {
        this.expressAddress = expressAddress;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

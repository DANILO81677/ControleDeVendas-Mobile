package com.example.myapplication.entity;

public class Products {
    private Integer productid;
    private String productname;

    public Integer getProductid() {
        return productid;
    }

    public void setProductid(Integer productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public void setProductdescription(String productdescription) {
        this.productdescription = productdescription;
    }

    public String getProducttype() {
        return producttype;
    }

    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    public String getProductvalue() {
        return productvalue;
    }

    public void setProductvalue(String productvalue) {
        this.productvalue = productvalue;
    }

    public String getProductmanufacturer() {
        return productmanufacturer;
    }

    public void setProductmanufacturer(String productmanufacturer) {
        this.productmanufacturer = productmanufacturer;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(String updatedate) {
        this.updatedate = updatedate;
    }

    public String getCanceldate() {
        return canceldate;
    }

    public void setCanceldate(String canceldate) {
        this.canceldate = canceldate;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    private String productdescription;
    private String producttype;
    private String productvalue;
    private String productmanufacturer;
    private String createdate;
    private String updatedate;
    private String canceldate;
    private String active;
}

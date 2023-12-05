package com.example.myapplication.entity;

public class Services {

    private Integer serviceid;

    public Integer getServiceid() {
        return serviceid;
    }

    public void setServiceid(Integer serviceid) {
        this.serviceid = serviceid;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getServicedescription() {
        return servicedescription;
    }

    public void setServicedescription(String servicedescription) {
        this.servicedescription = servicedescription;
    }

    public String getServicetype() {
        return servicetype;
    }

    public void setServicetype(String servicetype) {
        this.servicetype = servicetype;
    }

    public String getServicevalue() {
        return servicevalue;
    }

    public void setServicevalue(String servicevalue) {
        this.servicevalue = servicevalue;
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

    private String servicename;
    private String servicedescription;
    private String servicetype;
    private String servicevalue;
    private String createdate;
    private String updatedate;
    private String canceldate;
    private String active;

}

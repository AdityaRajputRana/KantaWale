package com.guru.kantewala.rest.response;

import com.guru.kantewala.Models.Company;

public class MyCompanyRP {
    boolean isCompanyLinked;
    boolean isPendingReq;
    PendingReq pendingReq;
    Company company;
    UserRP user;

    public MyCompanyRP() {
    }

    public boolean isPendingReq() {
        return isPendingReq;
    }

    public PendingReq getPendingReq() {
        return pendingReq;
    }

    public boolean isCompanyLinked() {
        return isCompanyLinked;
    }

    public Company getCompany() {
        return company;
    }

    public UserRP getUser() {
        return user;
    }

    public class PendingReq{
        int id;
        int companyId;
        String name;
        String uid;
        String city;
        String location;
        int stateCode;
        String gst;
        int status;
        String remarks;

        public String getRemarks() {
            return remarks;
        }

        public PendingReq() {
        }

        public int getId() {
            return id;
        }

        public int getCompanyId() {
            return companyId;
        }

        public String getName() {
            return name;
        }

        public String getUid() {
            return uid;
        }

        public String getCity() {
            return city;
        }

        public String getLocation() {
            return location;
        }

        public int getStateCode() {
            return stateCode;
        }

        public String getGst() {
            return gst;
        }

        public int getStatus() {
            return status;
        }
    }
}

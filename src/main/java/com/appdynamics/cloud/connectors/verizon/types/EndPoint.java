package com.appdynamics.cloud.connectors.verizon.types;

public enum EndPoint {
    IPADDRESS(IPAddress.class, "api/compute/ip-address"),
    VM(VerizonVM.class, "api/compute/vm");

    private Class<? extends Entity> refClass;
    private String endPoint;

    EndPoint(Class<? extends Entity> refClass, String endPoint) {
        this.refClass = refClass;
        this.endPoint = endPoint;
    }

    public Class<? extends Entity> getRefClass() {
        return refClass;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public static String getEndPoint(Class<? extends Entity> refClass) {
        EndPoint[] values = EndPoint.values();
        for (EndPoint endPoint : values) {
            if (endPoint.getRefClass().equals(refClass)) {
                return endPoint.getEndPoint();
            }
        }
        throw new IllegalArgumentException("EndPoint not defined for type :" + refClass);
    }
}

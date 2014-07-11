package com.appdynamics.cloud.connectors.verizon.types;

public enum MimeType {

    IPADDRESS(IPAddress.class, "application/vnd.terremark.ecloud.ip-address.v1+json"),
    JOB(Job.class, "application/vnd.terremark.ecloud.job.v1+json"),
    VDISK(Vdisk.class, "application/vnd.terremark.ecloud.vdisk.v1+json"),
    VDISKMOUNT(VdiskMount.class, "application/vnd.terremark.ecloud.vdisk-mount.v1+json"),
    VDISKTEMPLATE(VdiskTemplate.class, "application/vnd.terremark.ecloud.vdisk-template.v1+json"),
    VM(VerizonVM.class, "application/vnd.terremark.ecloud.vm.v1+json"),
    Vnic(Vnic.class, "application/vnd.terremark.ecloud.vnic.v1+json"),
    CONTROLLER(Controller.class, "application/vnd.terremark.ecloud.controller.v1+json");

    private Class<? extends Entity> refClass;
    private String type;

    MimeType(Class<? extends Entity> refClass, String type) {
        this.refClass = refClass;
        this.type = type;
    }

    public Class<? extends Entity> getRefClass() {
        return refClass;
    }

    public String getType() {
        return type;
    }

    public static String getType(Class<? extends Entity> refClass) {
        MimeType[] values = MimeType.values();
        for (MimeType mimeType : values) {
            if (mimeType.getRefClass().equals(refClass)) {
                return mimeType.getType();
            }
        }
        throw new IllegalArgumentException("MimeType not defined for type :" + refClass);
    }
}
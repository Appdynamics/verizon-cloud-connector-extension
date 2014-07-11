package com.appdynamics.cloud.connectors.verizon.types;

public class Job extends Entity {
    private String name;
    private String status;
    private String errorMessage;
    private Target target;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public class Target extends Entity {
        public Target() {
        }
    }
}

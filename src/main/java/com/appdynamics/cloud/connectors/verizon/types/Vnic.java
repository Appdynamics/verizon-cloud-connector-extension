package com.appdynamics.cloud.connectors.verizon.types;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class Vnic extends Entity {
    private int number;
    private IPAddress publicIpv4;
    private int bandwidth;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public IPAddress getPublicIpv4() {
        return publicIpv4;
    }

    public void setPublicIpv4(IPAddress publicIpv4) {
        this.publicIpv4 = publicIpv4;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public static void main(String[] args) {
        String json = "{\n" +
                "  \"href\" : \"https://iadg2.cloud.verizon.com/api/compute/vm/96f135b9-84ea-4ebf-a14c-0f91f0a51232/vnics/\",\n" +
                "  \"type\" : \"application/vnd.terremark.ecloud.vnic.v1+json; type=group\",\n" +
                "  \"items\" : [ {\n" +
                "    \"href\" : \"https://iadg2.cloud.verizon.com/api/compute/vnic/4b513293-fc18-4024-baf9-dc1b33eb2c1f\",\n" +
                "    \"type\" : \"application/vnd.terremark.ecloud.vnic.v1+json\",\n" +
                "    \"id\" : \"4b513293-fc18-4024-baf9-dc1b33eb2c1f\",\n" +
                "    \"number\" : 1,\n" +
                "    \"mac\" : \"60:9F:9D:B7:93:ED\",\n" +
                "    \"bandwidth\" : 100,\n" +
                "    \"ipv4Address\" : \"204.151.15.129\",\n" +
                "    \"vm\" : {\n" +
                "      \"href\" : \"https://iadg2.cloud.verizon.com/api/compute/vm/96f135b9-84ea-4ebf-a14c-0f91f0a51232\",\n" +
                "      \"type\" : \"application/vnd.terremark.ecloud.vm.v1+json\"\n" +
                "    },\n" +
                "    \"vnet\" : {\n" +
                "      \"href\" : \"https://iadg2.cloud.verizon.com/api/compute/vnet/e2eff4ca-b51e-4e62-ad4d-4af1df201c06\",\n" +
                "      \"type\" : \"application/vnd.terremark.ecloud.vnet.v1+json\",\n" +
                "      \"id\" : \"e2eff4ca-b51e-4e62-ad4d-4af1df201c06\",\n" +
                "      \"tags\" : [ ],\n" +
                "      \"vnics\" : {\n" +
                "        \"href\" : \"https://iadg2.cloud.verizon.com/api/compute/vnet/e2eff4ca-b51e-4e62-ad4d-4af1df201c06/vnics/\",\n" +
                "        \"type\" : \"application/vnd.terremark.ecloud.vnic.v1+json; type=group\"\n" +
                "      },\n" +
                "      \"subnet\" : {\n" +
                "        \"href\" : \"https://iadg2.cloud.verizon.com/api/compute/vnet/e2eff4ca-b51e-4e62-ad4d-4af1df201c06/subnet/\",\n" +
                "        \"type\" : \"application/vnd.terremark.ecloud.subnet.v1+json\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"publicIpv4\" : {\n" +
                "      \"href\" : \"https://iadg2.cloud.verizon.com/api/compute/ip-address/37a07a8f-e8fb-46f6-a5ca-548f7b5c9273\",\n" +
                "      \"type\" : \"application/vnd.terremark.ecloud.ip-address.v1+json\",\n" +
                "      \"id\" : \"37a07a8f-e8fb-46f6-a5ca-548f7b5c9273\",\n" +
                "      \"address\" : \"204.151.15.129\",\n" +
                "      \"v\" : \"V4\",\n" +
                "      \"vnic\" : {\n" +
                "        \"href\" : \"https://iadg2.cloud.verizon.com/api/compute/vnic/4b513293-fc18-4024-baf9-dc1b33eb2c1f\",\n" +
                "        \"type\" : \"application/vnd.terremark.ecloud.vnic.v1+json\"\n" +
                "      },\n" +
                "      \"networkBoundaryInterfaces\" : {\n" +
                "        \"href\" : \"https://iadg2.cloud.verizon.com/api/compute/ip-address/37a07a8f-e8fb-46f6-a5ca-548f7b5c9273/network-boundary-interfaces/\",\n" +
                "        \"type\" : \"application/vnd.terremark.ecloud.network-boundary-interface.v1+json; type=group\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"status\" : \"ATTACHED\"\n" +
                "  } ]\n" +
                "}";

        Gson gson = new Gson();
        Type listType = new TypeToken<Group<Vnic>>() { }.getType();
        Group<Vnic> vnics = gson.fromJson(json, listType);
        System.out.println(vnics);
    }
}

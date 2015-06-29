package com.appdynamics.cloud.connectors.verizon.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OSImageTemplateMapper {

    private static final Map<String, String> VDISK_TEMPLATES;

    static {
        Map<String, String> templates = new HashMap<String, String>();
        templates.put("pfSense 2.1.2 x64", "/api/compute/vdisk-template/246410e9-8a6a-440e-9729-7c8bf2c61ab7");
        templates.put("Big IP F5 11.5.2", "/api/compute/vdisk-template/45c02396-95dd-48b0-a839-98299402e407");
        templates.put("CentOS 7 x64", "/api/compute/vdisk-template/f2de015a-5616-48d2-bc21-94bb3fb134b8");
        templates.put("Ubuntu 12.04 LTS x64", "/api/compute/vdisk-template/a6be9c36-3fe3-4a71-85e3-6a8a5d66005f");
        templates.put("Ubuntu 14.04 LTS x64", "/api/compute/vdisk-template/f3f42a22-2abb-492a-9c0a-268df2c287a9");
        templates.put("FreeBSD 10.0 x64", "/api/compute/vdisk-template/5e33a6aa-b1be-46a6-bf66-012a33d795a1");
        templates.put("Windows Server 2008 Standard SQL 2008 R2 Web 64-Bit 40GB", "/api/compute/vdisk-template/6819bed4-ffcf-4aee-8bcd-2f6e0a066a67");
        templates.put("Windows Server 2008 Standard 64-Bit 40GB", "/api/compute/vdisk-template/773fb6a3-1ed6-4791-8e45-b3671ffb193b");
        templates.put("Windows Server 2008 Enterprise 64-Bit 40GB", "/api/compute/vdisk-template/df354ece-92fa-4361-9b13-c25ad9cbb0a7");
        templates.put("Windows Server 2008 Standard SQL 2008 R2 Standard 64-Bit 40GB", "/api/compute/vdisk-template/1ab44a02-a972-4183-a892-ad2690ce37fe");
        templates.put("Windows Server 2012 SQL 2012 Web 64-Bit 50GB", "/api/compute/vdisk-template/fe321b3d-ffd6-435b-9d96-aa2c2970e634");
        templates.put("Windows Server 2012 SQL 2012 Standard 64-Bit 50GB", "/api/compute/vdisk-template/ab4d0e55-f8b6-4a8b-b240-db7b0347950e");
        templates.put("Windows Server 2012 64-Bit 50GB", "/api/compute/vdisk-template/ea6f49fe-95f9-4017-b161-895d35a3e1bf");
        templates.put("Juniper Firefly 12.1x47", "/api/compute/vdisk-template/227C7213-C5DB-48A1-BC74-B00032BE1522");
        templates.put("Debian 7 (Wheezy) x86_64", "/api/compute/vdisk-template/a8b1bd69-8c59-457a-861b-2a3e038de605");
        templates.put("centos 6.6 x86_64", "/api/compute/vdisk-template/33d7cb7a-1297-47c4-9b26-d600ef7d8e49");
        templates.put("CentOS 5.11 x64", "/api/compute/vdisk-template/50174403-53a2-4cac-8abc-93db6e5fce5d");
        templates.put("Red Hat Enterprise Linux 5.11 x86_64", "/api/compute/vdisk-template/3dfd6545-4085-49d2-a6fe-76b77a09cd02");
        templates.put("Red Hat Enterprise Linux 6.6 x86_64", "/api/compute/vdisk-template/a78fd3ec-b8bc-4ad5-b0f3-6f254a92d6c7");
        templates.put("CentOS 6.6 x64 with Docker", "/api/compute/vdisk-template/fdad6299-7568-4143-bc2b-f93f49279565");

        VDISK_TEMPLATES = Collections.unmodifiableMap(templates);
    }

    public static String getDiskTemplateEndpoint(String osName, String accessURL) {
        String diskTemplateEndpoint = VDISK_TEMPLATES.get(osName);
        if (diskTemplateEndpoint == null || diskTemplateEndpoint.length() <= 0) {
            throw new IllegalArgumentException("No disk template found for OS [" + osName + "]");
        }

        if(accessURL.endsWith("/")) {
            accessURL = accessURL.substring(0, accessURL.length()-1);
        }

        return accessURL+diskTemplateEndpoint;
    }

}

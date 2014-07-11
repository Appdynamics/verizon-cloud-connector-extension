package com.appdynamics.cloud.connectors.verizon.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OSImageTemplateMapper {

    private static final Map<String, String> VDISK_TEMPLATES;

    static {
        Map<String, String> templates = new HashMap<String, String>();
        templates.put("CentOS 6.4 x64 **DEPRECATED**", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/e4ee60ec-6e6a-4335-b6c7-20238bd41691");
        templates.put("Red Hat Enterprise Linux 5.9 x64", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/809befa9-c41f-4bd8-90d3-098fc14c3c69");
        templates.put("Red Hat Enterprise Linux 5.10 i386", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/91778c33-8176-4e67-861d-0b04fa697974");
        templates.put("Red Hat Enterprise Linux 5.10 x86_64", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/0357813c-6352-4e2d-9ad3-fb9b228d1f91");
        templates.put("Ubuntu 12.04.4 Server LTS x32", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/39ef5720-96cc-4e35-af31-0d8ed74ec504");
        templates.put("CentOS 5.10 i386", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/06f25dcf-c882-43b9-9b8c-a1048381d6d5");
        templates.put("CentOS 6.5 i386", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/93ee4136-3ef5-4ee6-918f-815e1b40b31b");
        templates.put("FreeBSD 10.0 x64", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/5e33a6aa-b1be-46a6-bf66-012a33d795a1");
        templates.put("pfSense 2.1.2", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/0771e074-890e-490e-b2b6-77ca7828162e");
        templates.put("Red Hat Enterprise Linux 6.5 i386", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/443daedd-7a34-4ab5-8db1-50c322e970f6");
        templates.put("CentOS 5.10 x86_64", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/4829a20b-2568-4f8c-948b-e81e0e5df597");
        templates.put("CentOS 6.5 x64", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/d2b32b2a-d280-475d-ae90-a0b4316f250c");
        templates.put("Debian 7.5.0 x64", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/2d9ed485-ea96-406d-bcdc-b2f0514eace1");
        templates.put("Ubuntu 10.04.4 Server LTS x64", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/b8d65ac2-aea1-4e64-99f0-ad7fb67c4930");
        templates.put("Windows Server 2008 Standard SQL 2008 R2 Web 64-Bit 40GB", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/6819bed4-ffcf-4aee-8bcd-2f6e0a066a67");
        templates.put("Windows Server 2008 Standard 64-Bit 40GB", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/773fb6a3-1ed6-4791-8e45-b3671ffb193b");
        templates.put("Debian 7.2.0 x64", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/1f88ab9b-295c-4477-bd71-702a86ac00a8");
        templates.put("Red Hat Enterprise Linux 5.9 x32", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/48585e62-893c-46a4-8333-b6fbf9916249");
        templates.put("Red Hat Enterprise Linux 6.5 x86_64", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/05333d16-45ba-495f-9b51-4bd4c8746958");
        templates.put("Ubuntu 12.04.4 Server LTS x64", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/e9c796ec-aaab-49cf-8994-277431fbbc8d");
        templates.put("Windows Server 2008 Enterprise 64-Bit 40GB", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/df354ece-92fa-4361-9b13-c25ad9cbb0a7");
        templates.put("Windows Server 2008 Standard SQL 2008 R2 Standard 64-Bit 40GB", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/1ab44a02-a972-4183-a892-ad2690ce37fe");
        templates.put("Windows Server 2012 SQL 2012 Web 64-Bit 50GB", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/fe321b3d-ffd6-435b-9d96-aa2c2970e634");
        templates.put("Red Hat Enterprise Linux 6.4 i386", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/b8b606d1-a322-49a1-abd1-147f5b9b21ba");
        templates.put("Red Hat Enterprise Linux 6.4 x64", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/dc83cc96-473d-40f8-8ff0-4c954ed30087");
        templates.put("Windows Server 2012 SQL 2012 Standard 64-Bit 50GB", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/ab4d0e55-f8b6-4a8b-b240-db7b0347950e");
        templates.put("Windows Server 2012 64-Bit 50GB", "https://iadg2.cloud.verizon.com/api/compute/vdisk-template/ea6f49fe-95f9-4017-b161-895d35a3e1bf");

        VDISK_TEMPLATES = Collections.unmodifiableMap(templates);
    }

    public static String getDiskTemplateEndpoint(String osName) {
        String diskTemplateEndpoint = VDISK_TEMPLATES.get(osName);
        if (diskTemplateEndpoint == null || diskTemplateEndpoint.length() <= 0) {
            throw new IllegalArgumentException("No disk template found for OS [" + osName + "]");
        }
        return diskTemplateEndpoint;
    }

}

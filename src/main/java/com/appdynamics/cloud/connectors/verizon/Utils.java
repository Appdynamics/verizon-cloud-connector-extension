/**
 * Copyright 2013 AppDynamics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appdynamics.cloud.connectors.verizon;

import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.entity.api.IProperty;

/**
 * Utilities that are shared across the EC2 connector.
 */
class Utils {
    public static final String SECRET_KEY_PROP = "Secret Access Key";

    public static final String ACCESS_KEY_PROP = "Access Key";
    
    public static final String ACCESS_URL = "Access URL";

    public static final String VM_NAME_PROP = "VM Name";

    public static final String VM_DESCRIPTION_PROP = "Description";

    public static final String PROCESSOR_CORES_PROP = "Processor Cores";

    public static final String PROCESSOR_SPEED_PROP = "Processor Speed";

    public static final String MEMORY_PROP = "Memory in GB";

    public static final String MACHINE_TYPE_PROP = "Machine Type";

    public static final String OS_IMAGE_PROP = "OS Image";

    public static final String ACCOUNT = "Account";

    public static final String CLOUDSPACE = "Cloudspace";

    private Utils() {
    }


    public static String getAccessKey(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, ACCESS_KEY_PROP);
    }

    public static String getSecretKey(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, SECRET_KEY_PROP);
    }

    public static String getAccessURL(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, ACCESS_URL);
    }

    public static String getVmName(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, VM_NAME_PROP);
    }

    public static String getVmDescription(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, VM_DESCRIPTION_PROP);
    }

    public static String getProcessorCores(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, PROCESSOR_CORES_PROP);
    }

    public static String getProcessorSpeed(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, PROCESSOR_SPEED_PROP);
    }

    public static String getMemory(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, MEMORY_PROP);
    }

    public static String getMachineType(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, MACHINE_TYPE_PROP);
    }

    public static String getOsImage(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, OS_IMAGE_PROP);
    }

    public static String getAccount(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, ACCOUNT);
    }

    public static String getCloudspace(IProperty[] properties, IControllerServices controllerServices) {
        return controllerServices.getStringPropertyValueByName(properties, CLOUDSPACE);
    }
}

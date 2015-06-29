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

import com.appdynamics.cloud.connectors.verizon.exception.NoAlgoException;
import com.appdynamics.cloud.connectors.verizon.rest.RestClient;
import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.entity.api.IComputeCenter;
import com.singularity.ee.connectors.entity.api.IProperty;
import com.sun.jersey.core.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;


class ConnectorLocator {
    private static final ConnectorLocator INSTANCE = new ConnectorLocator();

    private final Map<String, RestClient> serviceIdVsClient = new HashMap<String, RestClient>();

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private static final Logger LOG = Logger.getLogger(ConnectorLocator.class.getName());

    /**
     * Private constructor on singleton.
     */
    private ConnectorLocator() {
    }

    public static ConnectorLocator getInstance() {
        return INSTANCE;
    }

    public RestClient getConnector(IComputeCenter computeCenter, IControllerServices controllerServices) {
        return getConnector(computeCenter.getProperties(), controllerServices);
    }

    public RestClient getConnector(IProperty[] computeCenterProperties, IControllerServices controllerServices) {
        String accessKey = Utils.getAccessKey(computeCenterProperties, controllerServices);
        String secretKey = Utils.getSecretKey(computeCenterProperties, controllerServices);

        RestClient restClient = getRestClient(accessKey, secretKey);

        if (restClient == null) {
            restClient = setRestClient(accessKey, secretKey);
        }

        return restClient;
    }

    private RestClient setRestClient(String accessKey, String secretKey) {
        rwLock.writeLock().lock();
        try {
            RestClient restClient = new RestClient(accessKey, secretKey);
            serviceIdVsClient.put(getUniqueId(accessKey, secretKey), restClient);
            return restClient;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private RestClient getRestClient(String accessKey, String secretKey) {
        rwLock.readLock().lock();
        try {
            RestClient compute = serviceIdVsClient.get(getUniqueId(accessKey, secretKey));
            if (compute != null) {
                return compute;
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return null;
    }

    private String getUniqueId(String accessKey, String secretKey) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            LOG.log(Level.WARNING, "Algorithm not found", e);
            throw new NoAlgoException("Algorithm not found", e);
        }
        digest.update(secretKey.getBytes());
        byte[] hash = digest.digest("Secret Key".getBytes());
        byte[] bytes = accessKey.getBytes();
        byte[] finalBytes = new byte[bytes.length + hash.length];

        for (int i = 0; i < bytes.length; i++) {
            finalBytes[i] = bytes[i];
        }

        for (int i = 0; i < hash.length; i++) {
            finalBytes[bytes.length + i] = hash[i];
        }
        return new String(Base64.encode(finalBytes));
    }
}

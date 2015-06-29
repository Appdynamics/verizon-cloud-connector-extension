package com.appdynamics.cloud.connectors.verizon.rest;

import com.appdynamics.cloud.connectors.verizon.exception.TaskExecutionException;
import com.appdynamics.cloud.connectors.verizon.types.Cloudspace;
import com.appdynamics.cloud.connectors.verizon.types.CloudspaceItem;
import com.appdynamics.cloud.connectors.verizon.types.EndPoint;
import com.appdynamics.cloud.connectors.verizon.types.Group;
import com.appdynamics.cloud.connectors.verizon.types.IPAddress;
import com.appdynamics.cloud.connectors.verizon.types.Identity;
import com.appdynamics.cloud.connectors.verizon.types.Job;
import com.appdynamics.cloud.connectors.verizon.types.MimeType;
import com.appdynamics.cloud.connectors.verizon.types.VerizonVM;
import com.appdynamics.cloud.connectors.verizon.types.Vnic;
import com.google.gson.reflect.TypeToken;
import com.singularity.ee.connectors.api.ConnectorException;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestClientOperations {

    private static final Logger LOG = Logger.getLogger(RestClientOperations.class.getName());

    private static final String GET_IDENTITY_URI = "https://identity.cloud.verizon.com/ams/identity/current";
    private static final String GET_CLOUDSPACE_URI = "https://identity.cloud.verizon.com/ams/cloudspace/account/{ACCOUNT}";

    private static final String VM_POST_DATA_TEMPLATE = "{" +
            "\"type\":\"application/vnd.terremark.ecloud.vm.v1+json\"," +
            "\"name\":\"$(vmName)\"," +
            "\"description\":\"$(vmDesc)\"," +
            "\"processorCores\":$(processorCores)," +
            "\"processorSpeed\":$(processorSpeed)," +
            "\"memory\":$(memory)," +
            "\"vdiskMounts\":{\"items\":[{\"vdisk\":{\"fromVdiskTemplate\":{\"href\":\"$(vDiskTemplate)\",\"type\":\"application/vnd.terremark.ecloud.vdisk-template.v1+json\"},\"name\":\"$(vmName): Boot Disk\"},\"index\":0,\"diskOps\":100}]}," +
            "\"vnics\":{\"items\":[{\"number\":1,\"publicIpv4\":{\"href\":\"$(publicIpRef)\"},\"bandwidth\":100}]}}";

    private RestClient restClient;
    private String baseURI;

    public RestClientOperations(RestClient restClient, String baseURI) {
        this.restClient = restClient;
        this.baseURI = baseURI;
    }

    public void validateCredentials() throws ConnectorException {
        LOG.log(Level.INFO, "Validating credentials");
        restClient.get(GET_IDENTITY_URI, Identity.class);
    }

    public void setCloudspace(String account, String cloudspaceName) throws ConnectorException {
        String cloudSpace = getCloudSpace(account, cloudspaceName);
        restClient.setCloudSpace(cloudSpace);
    }

    private String getCloudSpace(String account, String cloudspaceName) throws ConnectorException {
        String cloudspaceUri = GET_CLOUDSPACE_URI;
        cloudspaceUri = cloudspaceUri.replace("{ACCOUNT}", account);
        Cloudspace cloudspace = restClient.get(cloudspaceUri, Cloudspace.class);
        if (cloudspace != null) {
            List<CloudspaceItem> items = cloudspace.getItems();
            if (items != null) {
                for (CloudspaceItem cloudspaceItem : items) {
                    if (cloudspaceName.equals(cloudspaceItem.getName())) {
                        return cloudspaceItem.getId();
                    }
                }
            }
        }
        throw new ConnectorException("Cloudspace with name [" + cloudspaceName + "] not found in account [" + account + "]");
    }

    public Job createPublicIP() throws ConnectorException {
        LOG.log(Level.INFO, "Creating public IP");
        String endPoint = EndPoint.IPADDRESS.getEndPoint();
        String createIpUri = buildEndPointURI(endPoint);
        return restClient.post(createIpUri, MimeType.IPADDRESS.getType(), MimeType.JOB.getType(), "{}", Job.class);
    }

    public Job createVM(String vmName, String vmDescription, String publicIpHref, String diskTemplateEndpoint, String memory, String processorCores, String processorSpeed) throws ConnectorException {
        LOG.log(Level.INFO, "Creating VM");
        String endPoint = EndPoint.VM.getEndPoint();
        String createIpUri = buildEndPointURI(endPoint);
        int memoryInGB = Integer.parseInt(memory);
        int memoryInMB = (memoryInGB * 1024);
        String vmPostData = getVmPostData(vmName, vmDescription, publicIpHref, diskTemplateEndpoint, String.valueOf(memoryInMB), processorCores, processorSpeed);
        return restClient.post(createIpUri, MimeType.VM.getType(), MimeType.JOB.getType(), vmPostData, Job.class);
    }

    public String getPublicIp(String publicIpRef) throws ConnectorException {
        LOG.log(Level.INFO, "Getting public IP with Href");
        IPAddress ipAddress = restClient.get(publicIpRef, IPAddress.class);
        return ipAddress.getAddress();
    }

    public Job deletePublicIp(String publicIpRef) throws ConnectorException {
        LOG.log(Level.INFO, "Deleting public IP");
        return restClient.delete(publicIpRef, Job.class);
    }

    public VerizonVM getVm(String vmHref) throws ConnectorException {
        LOG.log(Level.INFO, "Getting VM with Href");
        VerizonVM vm = restClient.get(vmHref, VerizonVM.class);
        return vm;
    }

    public VerizonVM getVmById(String machineId) throws ConnectorException {
        LOG.log(Level.INFO, "Getting VM by Id");
        String machineByIdEndPoint = EndPoint.VM.getEndPoint() + "/" + machineId;
        String getVmURI = buildEndPointURI(machineByIdEndPoint);
        VerizonVM vm = restClient.get(getVmURI, VerizonVM.class);
        return vm;
    }

    public void restartInstance(String machineId) throws ConnectorException {
        LOG.log(Level.INFO, "Restarting the Vm");
        String machineRestartEndPoint = EndPoint.VM.getEndPoint() + "/" + machineId + "/power-on";
        String vmRestartURI = buildEndPointURI(machineRestartEndPoint);
        restClient.post(vmRestartURI, MimeType.CONTROLLER.getType(), MimeType.JOB.getType(), "{}", Job.class);
    }

    public Job terminateInstance(String machineId) throws ConnectorException {
        LOG.log(Level.INFO, "Started terminating instance");
        String machineByIdEndPoint = EndPoint.VM.getEndPoint() + "/" + machineId;
        String machineVnicEndPoint = EndPoint.VM.getEndPoint() + "/" + machineId + "/vnics";

        String publicIpv4Href = getPublicIpv4Href(machineVnicEndPoint);

        //Delete the Machine
        String getVmURI = buildEndPointURI(machineByIdEndPoint);
        LOG.log(Level.INFO, "Terminate instance called");
        Job terminateJob = restClient.delete(getVmURI, Job.class);

        //Wait for machine to delete
        LOG.log(Level.INFO, "Waiting for instance to terminate");
        CountDownLatch doneSignal = new CountDownLatch(1);
        try {
            waitForJobToComplete(terminateJob.getHref(), doneSignal);
        } catch (ExecutionException e) {
            LOG.log(Level.INFO, "Unable to get the delete vm job details");
        } catch (InterruptedException e) {
            LOG.log(Level.INFO, "Unable to get the delete vm job details");
        }
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "Waiting for delete vm interrupted", e);
        }

        //Wait for Verizon to update the VM status
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            LOG.log(Level.INFO, "Waiting for VM update interrupted", e);
        }
        //Delete the assigned public ip
        LOG.log(Level.INFO, "Deleting the instance public IP");
        CountDownLatch doneSignalForIp = new CountDownLatch(1);
        Job job = deletePublicIp(publicIpv4Href);
        try {
            waitForJobToComplete(job.getHref(), doneSignalForIp);
        } catch (Exception e) {
            LOG.log(Level.INFO, "Delete public IP failed with message " + e.getMessage() + ". Retrying one more time.", e);
            try {
                Job retryJob = deletePublicIp(publicIpv4Href);
                waitForJobToComplete(retryJob.getHref(), doneSignalForIp);
            } catch (Exception ex) {
                LOG.log(Level.INFO, "Unable to delete public IP with reference [" + job.getTarget().getHref() + "]. Delete it manually", ex);
            }
            doneSignalForIp.countDown();
        }

        try {
            doneSignalForIp.await();
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "Waiting for delete public ip interrupted", e);
        }

        LOG.log(Level.INFO, "Instance termination completed");
        return terminateJob;
    }

    private void waitForJobToComplete(final String jobHref, final CountDownLatch doneSignal) throws ConnectorException, ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        int noOfRetries = 0;
        int maxRetries = 5;
        Future<Boolean> booleanFuture;
        do {
            if (noOfRetries > maxRetries) {
                LOG.log(Level.INFO, "Max no of retries reached");
                doneSignal.countDown();
                executorService.shutdown();
                return;
            }
            noOfRetries++;
            Thread.sleep(5 * 1000);
            booleanFuture = executeTask(jobHref, executorService);

        } while (!booleanFuture.get());
        doneSignal.countDown();
        executorService.shutdown();
    }

    private Future<Boolean> executeTask(final String jobHref, ExecutorService executorService) {
        return executorService.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                try {
                    Job job = restClient.get(jobHref, Job.class);
                    if ("COMPLETE".equalsIgnoreCase(job.getStatus())) {
                        return true;
                    } else if ("FAILED".equalsIgnoreCase(job.getStatus())) {
                        throw new TaskExecutionException(job.getErrorMessage());
                    }
                } catch (ConnectorException e) {
                    LOG.log(Level.WARNING, "Unable to get Job details", e);
                }
                return false;
            }
        });
    }

    private String getPublicIpv4Href(String machineVnicEndPoint) throws ConnectorException {
        Type listType = new TypeToken<Group<Vnic>>() {
        }.getType();
        String vnicURI = buildEndPointURI(machineVnicEndPoint);
        Group<Vnic> vnics = restClient.get(vnicURI, listType);

        Vnic vnic = vnics.getItems().get(0);
        return vnic.getPublicIpv4().getHref();
    }

    private String getVmPostData(String vmName, String vmDescription, String publicIpHref, String diskTemplateEndpoint, String memory, String processorCores, String processorSpeed) {

        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("vmName", vmName);
        valuesMap.put("vmDesc", vmDescription);
        valuesMap.put("processorCores", processorCores);
        valuesMap.put("processorSpeed", processorSpeed);
        valuesMap.put("memory", memory);
        valuesMap.put("vDiskTemplate", diskTemplateEndpoint);
        valuesMap.put("publicIpRef", publicIpHref);

        StrSubstitutor strSubstitutor = new StrSubstitutor(valuesMap, "$(", ")");
        String vmPostData = strSubstitutor.replace(VM_POST_DATA_TEMPLATE);
        return vmPostData;
    }

    private String buildEndPointURI(String resourceURL) {
        if (baseURI.startsWith("http://")) {
            baseURI = baseURI.replace("http://", "https://");
        }

        if (!baseURI.startsWith("https://")) {
            baseURI = "https://" + baseURI;
        }

        StringBuilder sb = new StringBuilder(baseURI);
        if (!baseURI.endsWith("/")) {
            sb.append("/");
        }
        sb.append(resourceURL);
        return sb.toString();
    }
}

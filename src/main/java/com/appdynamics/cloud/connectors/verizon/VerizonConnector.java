package com.appdynamics.cloud.connectors.verizon;

import com.appdynamics.cloud.connectors.verizon.rest.RestClient;
import com.appdynamics.cloud.connectors.verizon.rest.RestClientOperations;
import com.appdynamics.cloud.connectors.verizon.types.Job;
import com.appdynamics.cloud.connectors.verizon.types.OSImageTemplateMapper;
import com.appdynamics.cloud.connectors.verizon.types.VerizonVM;
import com.singularity.ee.agent.resolver.AgentResolutionEncoder;
import com.singularity.ee.connectors.api.ConnectorException;
import com.singularity.ee.connectors.api.IConnector;
import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.api.InvalidObjectException;
import com.singularity.ee.connectors.entity.api.IAccount;
import com.singularity.ee.connectors.entity.api.IComputeCenter;
import com.singularity.ee.connectors.entity.api.IImage;
import com.singularity.ee.connectors.entity.api.IImageStore;
import com.singularity.ee.connectors.entity.api.IMachine;
import com.singularity.ee.connectors.entity.api.IMachineDescriptor;
import com.singularity.ee.connectors.entity.api.IProperty;
import com.singularity.ee.connectors.entity.api.MachineState;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.singularity.ee.controller.KAppServerConstants.CONTROLLER_SERVICES_HOST_NAME_PROPERTY_KEY;
import static com.singularity.ee.controller.KAppServerConstants.CONTROLLER_SERVICES_PORT_PROPERTY_KEY;
import static com.singularity.ee.controller.KAppServerConstants.DEFAULT_CONTROLLER_PORT_VALUE;

public class VerizonConnector implements IConnector {

    private static final Logger LOG = Logger.getLogger(VerizonConnector.class.getName());

    private IControllerServices controllerServices;

    @Override
    public void setControllerServices(IControllerServices iControllerServices) {
        this.controllerServices = iControllerServices;
    }

    @Override
    public int getAgentPort() {
        return controllerServices.getDefaultAgentPort();
    }

    @Override
    public IMachine createMachine(IComputeCenter iComputeCenter, IImage iImage, IMachineDescriptor iMachineDescriptor) throws InvalidObjectException, ConnectorException {


        String accessURL = Utils.getAccessURL(iComputeCenter.getProperties(), controllerServices);
        //String machineType = Utils.getMachineType(iComputeCenter.getProperties(), controllerServices);
        String memory = Utils.getMemory(iMachineDescriptor.getProperties(), controllerServices);

        String osImage = Utils.getOsImage(iImage.getProperties(), controllerServices);
        String account = Utils.getAccount(iImage.getProperties(), controllerServices);
        String cloudSpaceName = Utils.getCloudspace(iImage.getProperties(), controllerServices);

        String processorCores = Utils.getProcessorCores(iMachineDescriptor.getProperties(), controllerServices);
        String processorSpeed = Utils.getProcessorSpeed(iMachineDescriptor.getProperties(), controllerServices);
        String vmDescription = Utils.getVmDescription(iMachineDescriptor.getProperties(), controllerServices);
        String vmName = Utils.getVmName(iMachineDescriptor.getProperties(), controllerServices);

        final RestClient connector = ConnectorLocator.getInstance().getConnector(iComputeCenter.getProperties(), controllerServices);
        AgentResolutionEncoder agentResolutionEncoder = getAgentResolutionEncoder(iComputeCenter);

        RestClientOperations restClientOperations = new RestClientOperations(connector, accessURL);

        restClientOperations.setCloudspace(account, cloudSpaceName);

        //create public ip address                                                                
        Job publicIPJob = restClientOperations.createPublicIP();
        String publicIpHref = publicIPJob.getTarget().getHref();

        boolean instanceCreated = false;
        String publicIp = null;
        try {
            //Create VM
            String diskTemplateEndpoint = OSImageTemplateMapper.getDiskTemplateEndpoint(osImage, accessURL);
            Job vmJob = restClientOperations.createVM(vmName, vmDescription, publicIpHref, diskTemplateEndpoint, memory, processorCores, processorSpeed);
            VerizonVM vm = restClientOperations.getVm(vmJob.getTarget().getHref());
            instanceCreated = true;

            //Get the created public ip address
            publicIp = restClientOperations.getPublicIp(publicIpHref);

            IMachine machineInstance = controllerServices.createMachineInstance(vm.getId(),
                    agentResolutionEncoder.getUniqueHostIdentifier(), publicIp, iComputeCenter, iMachineDescriptor, iImage,
                    getAgentPort());
            return machineInstance;

        } catch (ConnectorException e) {
            LOG.log(Level.WARNING, "Unable to create instance", e);
            throw e;
        } finally {
            if (!instanceCreated) {
                try {
                    restClientOperations.deletePublicIp(publicIpHref);
                } catch (ConnectorException e) {
                    String message = "Machine create failed and unable to delete the assigned IP address! " +
                            "We have a public IP " + publicIp + " which is not used by any instance." +
                            " Please remove the public IP manually.";
                    LOG.log(Level.WARNING, message, e);
                    throw new ConnectorException(message, e);
                }
            }
        }
    }

    private AgentResolutionEncoder getAgentResolutionEncoder(IComputeCenter iComputeCenter) throws ConnectorException {
        IAccount account = iComputeCenter.getAccount();

        String controllerHost = null;
        try {
            controllerHost = System.getProperty(CONTROLLER_SERVICES_HOST_NAME_PROPERTY_KEY, InetAddress
                    .getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
            throw new ConnectorException(e);
        }

        int controllerPort = Integer.getInteger(CONTROLLER_SERVICES_PORT_PROPERTY_KEY, DEFAULT_CONTROLLER_PORT_VALUE);

        return new AgentResolutionEncoder(controllerHost, controllerPort,
                account.getName(), account.getAccessKey());
    }

    @Override
    public void refreshMachineState(IMachine iMachine) throws InvalidObjectException, ConnectorException {
        IComputeCenter computeCenter = iMachine.getComputeCenter();
        final RestClient connector = ConnectorLocator.getInstance().getConnector(computeCenter.getProperties(), controllerServices);
        String accessURL = Utils.getAccessURL(computeCenter.getProperties(), controllerServices);
        MachineState currentState = iMachine.getState();
        try {
            RestClientOperations restClientOperations = new RestClientOperations(connector, accessURL);
            VerizonVM vm = restClientOperations.getVmById(iMachine.getName());

            if (vm == null) {
                if (currentState != MachineState.STOPPED) {
                    iMachine.setState(MachineState.STOPPED);
                }
            } else {
                if ("ON".equalsIgnoreCase(vm.getStatus())) {
                    iMachine.setState(MachineState.STARTED);
                } else if ("INITIALIZING".equalsIgnoreCase(vm.getStatus()) || "STARTING".equalsIgnoreCase(vm.getStatus()) || "REBOOTING".equalsIgnoreCase(vm.getStatus())) {
                    iMachine.setState(MachineState.STARTING);
                } else {
                    iMachine.setState(MachineState.STOPPED);
                }
            }
        } catch (ConnectorException e) {
            //If the machine instance is not found, set the state as STOPPED
            if (e.getMessage().contains("Resource not found")) {
                iMachine.setState(MachineState.STOPPED);
            }
            LOG.log(Level.WARNING, "Error in refresh instance", e);
        }

    }


    @Override
    public void terminateMachine(IMachine iMachine) throws InvalidObjectException, ConnectorException {
        IComputeCenter computeCenter = iMachine.getComputeCenter();
        String accessURL = Utils.getAccessURL(computeCenter.getProperties(), controllerServices);
        final RestClient connector = ConnectorLocator.getInstance().getConnector(computeCenter.getProperties(), controllerServices);

        try {
            //Delete instance
            RestClientOperations restClientOperations = new RestClientOperations(connector, accessURL);
            restClientOperations.terminateInstance(iMachine.getName());
            iMachine.setState(MachineState.STOPPED);
        } catch (ConnectorException e) {
            if (e.getMessage().contains("Resource not found")) {
                iMachine.setState(MachineState.STOPPED);
            }
            LOG.log(Level.WARNING, "Unable to terminate the instance", e);
            throw new ConnectorException("Unable to terminate the instance", e);

        }
    }

    @Override
    public void restartMachine(IMachine iMachine) throws InvalidObjectException, ConnectorException {
        IComputeCenter computeCenter = iMachine.getComputeCenter();
        String accessURL = Utils.getAccessURL(computeCenter.getProperties(), controllerServices);
        final RestClient connector = ConnectorLocator.getInstance().getConnector(computeCenter.getProperties(), controllerServices);

        try {
            //Restart instance
            RestClientOperations restClientOperations = new RestClientOperations(connector, accessURL);
            restClientOperations.restartInstance(iMachine.getName());
        } catch (ConnectorException e) {
            LOG.log(Level.WARNING, "Unable to restart the instance", e);
            throw new ConnectorException("Unable to restart the instance", e);

        }
    }

    @Override
    public void deleteImage(IImage iImage) throws InvalidObjectException, ConnectorException {

    }

    @Override
    public void refreshImageState(IImage iImage) throws InvalidObjectException, ConnectorException {

    }

    @Override
    public void validate(IComputeCenter iComputeCenter) throws InvalidObjectException, ConnectorException {

        validate(iComputeCenter.getProperties());
    }

    @Override
    public void configure(IComputeCenter iComputeCenter) throws InvalidObjectException, ConnectorException {

    }

    @Override
    public void unconfigure(IComputeCenter iComputeCenter) throws InvalidObjectException, ConnectorException {

    }

    @Override
    public void validate(IImageStore iImageStore) throws InvalidObjectException, ConnectorException {
    }

    @Override
    public void configure(IImageStore iImageStore) throws InvalidObjectException, ConnectorException {

    }

    @Override
    public void unconfigure(IImageStore iImageStore) throws InvalidObjectException, ConnectorException {

    }

    @Override
    public void validate(IImage iImage) throws InvalidObjectException, ConnectorException {

    }

    @Override
    public void configure(IImage iImage) throws InvalidObjectException, ConnectorException {

    }

    @Override
    public void unconfigure(IImage iImage) throws InvalidObjectException, ConnectorException {

    }

    private void validate(IProperty[] properties) throws InvalidObjectException {
        RestClient connector = ConnectorLocator.getInstance().getConnector(properties, controllerServices);
        String accessURL = Utils.getAccessURL(properties, controllerServices);
        RestClientOperations restClientOperations = new RestClientOperations(connector, accessURL);

        // this will validate the access and secret keys
        try {
            restClientOperations.validateCredentials();
        } catch (ConnectorException e) {
            LOG.log(Level.INFO, e.getMessage(), e);
            throw new InvalidObjectException("The specified " + Utils.ACCESS_KEY_PROP +
                    " and/or " + Utils.SECRET_KEY_PROP + " is not valid.", e);
        }
    }
}
         
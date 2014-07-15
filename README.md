Verizon Cloud Connector Extension
=================================

##Use Case

Elastically grow/shrink instances into cloud/virtualized environments. There are four use cases for the connector. 

First, if the Controller detects that the load on the machine instances hosting an application is too high, the verizon-cloud-connector-extension may be used to automate creation of new virtual machines to host that application. The end goal is to reduce the load across the application by horizontally scaling up application machine instances.

Second, if the Controller detects that the load on the machine instances hosting an application is below some minimum threshold, the verizon-cloud-connector-extension may be used to terminate virtual machines running that application. The end goal is to save power/usage costs without sacrificing application performance by horizontally scaling down application machine instances.

Third, if the Controller detects that a machine instance has terminated unexpectedly when the connector refreshes an application machine state, the verizon-cloud-connector-extension may be used to create a replacement virtual machine to replace the terminated application machine instance. This is known as our failover feature.

Lastly, the verizon-cloud-connector-extension may be used to stage migration of an application from a physical to virtual infrastructure. Or the verizon-cloud-connector-extension may be used to add additional virtual capacity to an application to augment a preexisting physical infrastructure hosting the application.   

##Directory Structure

<table><tbody>
<tr>
<th align="left"> File/Folder </th>
<th align="left"> Description </th>
</tr>
<tr>
<td class='confluenceTd'> src </td>
<td class='confluenceTd'> Contains source code to the verizon connector extension </td>
</tr>
<tr>
<td class='confluenceTd'> target </td>
<td class='confluenceTd'> Only obtained when using maven. Run 'maven clean install' to get distributable .zip file </td>
</tr>
<tr>
<td class='confluenceTd'> pom.xml </td>
<td class='confluenceTd'> maven script file (required only if changing Java code) </td>
</tr>
</tbody>
</table>

##Prerequisite
Create a key pair to use this connector extension. Follow the link to create a key pair
http://cloud.verizon.com/documentation/CreatingTheAPIKeyPair.htm


##Installation

1. Clone the verizon-cloud-connector-extension from GitHub
2. Run 'maven clean install' from the cloned verizon-cloud-connector-extension directory
3. Download the file verizon-connector.zip located in the 'dist' directory into \<controller install dir\>/lib/connectors
4. Unzip the downloaded file
5. Restart the Controller
6. Go to the controller dashboard on the browser. Under Setup->My Preferences->Advanced Features enable "Show Cloud Auto-Scaling features" if it is not enabled. 
7. On the controller dashboard click "Cloud Auto-Scaling" and configure the compute cloud and the image.

Click Compute Cloud->Register Compute Cloud. Refer to the image below

<b>Note:</b> Access URL should be the URL which you see after login to the Verizon cloud. In my case it is https://iadg2.cloud.verizon.com. Please do not use https://api.cloud.verizon.com.

![alt tag](https://github.com/Appdynamics/verizon-cloud-connector-extension/raw/master/verizon_compute-cloud.png)

Click Image->Register Image. Refer to the image below

![alt tag](https://github.com/Appdynamics/verizon-cloud-connector-extension/raw/master/verizon_image.png)

To launch an instance click the image created in the above step and click on Launch Instance. Refer to the image below

![alt tag](https://github.com/Appdynamics/verizon-cloud-connector-extension/raw/master/verizon_launch_instance.png)


##Contributing

Always feel free to fork and contribute any changes directly here on GitHub.

##Community

Find out more in the [AppSphere]() community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).


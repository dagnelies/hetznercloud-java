package me.tomsdevsn.hetznercloud;

import me.tomsdevsn.hetznercloud.objects.general.Server;
import me.tomsdevsn.hetznercloud.objects.request.*;
import me.tomsdevsn.hetznercloud.objects.response.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HetznerCloudAPI {

    private static final String API_URL = "https://api.hetzner.cloud/v1";

    private final String token;

    private HttpEntity<String> httpEntity;
    private HttpHeaders httpHeaders;
    private final RestTemplate restTemplate;
    private List<HttpMessageConverter<?>> messageConverters;
    private MappingJackson2HttpMessageConverter converter;

    /**
     * Initial method to use the API
     * @param token
     */
    public HetznerCloudAPI(String token) {
        this.token = token;

        restTemplate = new RestTemplate();
        messageConverters = new ArrayList<HttpMessageConverter<?>>();
        converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.ALL}));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);

        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.add("Authorization", "Bearer " + token);
        httpEntity = new HttpEntity<>("parameters", httpHeaders);
    }

    /**
     * Creates a Cloud-server
     *
     * @param requestServer
     * @return response of the API
     */
    public ResponseServer createServer(RequestServer requestServer) {
        return restTemplate.postForEntity(API_URL + "/servers", new HttpEntity<>(requestServer, httpHeaders), ResponseServer.class).getBody();
    }

    /**
     * Get all of your servers in a list
     *
     * @return the server
     */
    public Servers getServers() {
        return restTemplate.exchange(API_URL + "/servers", HttpMethod.GET, httpEntity, Servers.class).getBody();
    }

    /**
     * Get the server by the name
     *
     * @param name of the server
     * @return the server
     */
    public Servers getServersByName(String name) {
        return restTemplate.exchange(API_URL + "/server?" + name, HttpMethod.GET, httpEntity, Servers.class).getBody();
    }

    /**
     * Get the server by the server-id
     *
     * @param id of the server
     * @returns the server
     */
    public Server getServerById(long id) {
        return restTemplate.exchange(API_URL + "/server/" + id, HttpMethod.GET, httpEntity, Server.class).getBody();
    }

    /**
     * Change the name of the server, in the Hetzner-Cloud Console
     *
     * @param id of the server
     * @param newServerName new server name
     * @return respond
     */
    public ResponseServernameChange changeServerName(int id, RequestServernameChange newServerName) {
        return restTemplate.exchange(API_URL + "/server/" + id, HttpMethod.PUT, new HttpEntity<>(newServerName, httpHeaders), ResponseServernameChange.class).getBody();
    }

    /**
     * Power on a specific server with the id
     *
     * @param id of the server
     * @return respond
     */
    public ResponsePower powerOnServer(long id) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/poweron", HttpMethod.POST, httpEntity, ResponsePower.class).getBody();
    }

    /**
     * Reboot a specific server with the id
     *
     * @param id of the server
     * @return respond
     */
    public ResponsePower softRebootServer(long id) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/reboot", HttpMethod.POST, httpEntity, ResponsePower.class).getBody();
    }

    /**
     * Reset a specific server with the id
     *
     * @param id of the server
     * @return respond
     */
    public ResponsePower resetServer(long id) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/reset", HttpMethod.POST, httpEntity, ResponsePower.class).getBody();
    }

    /**
     * Soft-shutdown a specific server with the id
     *
     * @param id of the server
     * @return respond
     */
    public ResponsePower shutdownServer(long id) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/shutdown", HttpMethod.POST, httpEntity, ResponsePower.class).getBody();
    }

    /**
     * Force power off a specific server with the id
     *
     * @param id of the server
     * @return respond
     */
    public ResponsePower forceShutdownServer(long id) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/poweroff", HttpMethod.POST, httpEntity, ResponsePower.class).getBody();
    }

    /**
     * Resets the root password from a specific server with the id
     *
     * @param id of the server
     * @return respond
     */
    public ResetPassword resetRootPassword(long id) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/reset_password", HttpMethod.POST, httpEntity, ResetPassword.class).getBody();
    }

    /**
     * Enables the rescue mode from the server
     *
     * @param id of the server
     * @return respond
     */
    public ResponseEnableRescue enableRescue(long id) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/enable_rescue", HttpMethod.POST, httpEntity, ResponseEnableRescue.class).getBody();
    }

    /**
     * Enables the rescue mode from the server
     *
     * @param id of the server
     * @param requestEnableRescue
     * @return respond
     */
    public ResponseEnableRescue enableRescue(long id, RequestEnableRescue requestEnableRescue) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/enable_rescue", HttpMethod.POST, new HttpEntity<>(requestEnableRescue, httpHeaders), ResponseEnableRescue.class).getBody();
    }

    /**
     * Enables the rescue mode from the server and reset the server
     *
     * @param id of the server
     * @param requestEnableRescue
     * @return respond
     */
    public ResponseEnableRescue enableRescueAndReset(long id, RequestEnableRescue requestEnableRescue) {
        ResponseEnableRescue request = restTemplate.exchange(API_URL + "/servers/" + id + "/actions/enable_rescue", HttpMethod.POST, new HttpEntity<>(requestEnableRescue, httpHeaders), ResponseEnableRescue.class).getBody();
        resetServer(id);
        return request;
    }

    /**
     * Disables the rescue mode from the server.
     * Only needed, if the server doesn't booted into the rescue mode.
     *
     * @param id of the server
     * @return respond
     */
    public ResponseDisableRescue disableRescue(long id) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/disable_rescue", HttpMethod.POST, httpEntity, ResponseDisableRescue.class).getBody();
    }

    /**
     * Get all available Images
     *
     * @return respond
     */
    public Images getImages() {
        return restTemplate.exchange(API_URL + "/images", HttpMethod.GET, httpEntity, Images.class).getBody();
    }

    /**
     * Rebuild a server, with the specific image.
     * example: ubuntu-16.04
     *
     * @param id of the server
     * @param requestRebuildServer
     * @return respond
     */
    public ResponseRebuildServer rebuildServer(long id, RequestRebuildServer requestRebuildServer) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/rebuild", HttpMethod.POST, new HttpEntity<>(requestRebuildServer, httpHeaders), ResponseRebuildServer.class).getBody();
    }

    /**
     * Change the type from the server
     * example: cx11 -> cx21
     *
     * @param id of the server
     * @param requestChangeType
     * @return respond
     */
    public ResponseChangeType changeServerType(long id, RequestChangeType requestChangeType) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/change_type", HttpMethod.POST, new HttpEntity<>(requestChangeType, httpHeaders), ResponseChangeType.class).getBody();
    }

    /**
     * Get the metrics from a server
     *
     * @param id of the server
     * @param metricType like cpu, disk or network (but also cpu,disk possible)
     * @param start of the metric
     * @param end of the metric
     * @return respond
     */
    public ResponseMetrics getMetrics(long id, String metricType, ZonedDateTime start, ZonedDateTime end) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/metrics?type=" + metricType + "&start=" + start.toString() + "&end=" + end.toString(), HttpMethod.GET, httpEntity, ResponseMetrics.class).getBody();
    }

    /**
     * Create a image from a server
     *
     * @param id of the server
     * @param requestCreateImage
     * @return respond
     */
    public ResponseCreateImage createImage(long id, RequestCreateImage requestCreateImage) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/create_image", HttpMethod.POST, new HttpEntity<>(requestCreateImage, httpHeaders), ResponseCreateImage.class).getBody();
    }

    /**
     * Enable the backups from a server
     * Please reminder, that will increase the price of the server by 20%
     *
     * @param id of the server
     * @param requestEnableBackup
     * @return respond
     */
    public ResponseEnableBackup enableBackup(long id, RequestEnableBackup requestEnableBackup) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/enable_backup", HttpMethod.POST, new HttpEntity<>(requestEnableBackup, httpHeaders), ResponseEnableBackup.class).getBody();
    }

    /**
     * Disable the backups from a server
     * Caution!: This will delete all existing backups immediately
     *
     * @param id of the server
     * @return respond
     */
    public ResponseDisableBackup disableBackup(long id) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/disable_backup", HttpMethod.POST, httpEntity, ResponseDisableBackup.class).getBody();
    }

    /**
     * Get all available ISO's.
     *
     * @return respond
     */
    public ResponseISOS getISOS() {
        return restTemplate.exchange(API_URL + "/isos", HttpMethod.GET, httpEntity, ResponseISOS.class).getBody();
    }

    public ResponseISO getISOById(long id) {
        return restTemplate.exchange(API_URL + "/isos/" + id, HttpMethod.GET, httpEntity, ResponseISO.class).getBody();
    }

    /**
     * Attach an ISO to a server.
     *
     * To get all ISO's
     * @see HetznerCloudAPI#getISOS
     *
     * @param id of the server
     * @param requestAttachISO
     * @return respond
     */
    public ResponseAttachISO attachISO(long id, RequestAttachISO requestAttachISO) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/attach_iso", HttpMethod.POST, new HttpEntity<>(requestAttachISO, httpHeaders), ResponseAttachISO.class).getBody();
    }

    /**
     * Detach an ISO from a server.
     *
     * @param id of the server
     * @return respond
     */
    public ResponseDetachISO detachISO(long id) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/detach_iso", HttpMethod.POST, httpEntity, ResponseDetachISO.class).getBody();
    }

    /**
     * Changes the reverse DNS entry from a server.
     *
     * Floating IPs assigned to the server are not affected!
     *
     * @param id of the server
     * @param requestChangeDNSPTR
     * @return respond
     */
    public ResponseChangeDNSPTR changeDNSPTR(long id, RequestChangeDNSPTR requestChangeDNSPTR) {
        return restTemplate.exchange(API_URL + "/servers/" + id + "/actions/change_dns_ptr", HttpMethod.POST, new HttpEntity<>(requestChangeDNSPTR, httpHeaders), ResponseChangeDNSPTR.class).getBody();
    }

    /**
     * Get a Datacenter by ID
     *
     * @param id of the datacenter
     * @return respond
     */
    public ResponseDatacenter getDatacenter(long id) {
        return restTemplate.exchange(API_URL + "/datacenters/" + id, HttpMethod.GET, httpEntity, ResponseDatacenter.class).getBody();
    }

    /**
     * Get all available datacenters and the recommendation
     *
     * @return respond
     */
    public ResponseDatacenters getDatacenters() {
        return restTemplate.exchange(API_URL + "/datacenters", HttpMethod.GET, httpEntity, ResponseDatacenters.class).getBody();
    }

    /**
     * Get a datacenter by name
     *
     * @param name of the datacenter
     * @return respond
     */
    public ResponseDatacenters getDatacenter(String name) {
        return restTemplate.exchange(API_URL + "/datacenters?" + name, HttpMethod.GET, httpEntity, ResponseDatacenters.class).getBody();
    }

    /**
     * Get all prices from the products
     *
     * @return respond
     */
    public ResponsePricing getPricing() {
        return restTemplate.exchange(API_URL + "/pricing", HttpMethod.GET, httpEntity, ResponsePricing.class).getBody();
    }
}
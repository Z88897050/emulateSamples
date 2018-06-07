package java_first_pojo.dao.enitity;

import java.io.Serializable;

/**
 * 删除服务时的信息对象
 * User: lil
 * Date: 2011-1-14
 * Time: 15:05:23
 */
public class ServiceDelInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String identification = "GXPT";  //身份
    private String ip;                         //ip地址，不是hostname
    private	 String version;                   // 版本
    private	 String devId;                     //设备编号，与集控对此设备配置的设备编号一致
    private	 String serviceId;                 // 要删除服务的服务编号

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}

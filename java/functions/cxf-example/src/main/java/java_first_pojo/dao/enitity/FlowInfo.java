package java_first_pojo.dao.enitity;

import java.io.Serializable;

/**
 * ���������Ϣ�Ķ���.
 * User: lil
 * Date: 2011-1-14
 * Time: 15:31:13
 */
public class FlowInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String identification = "GXPT";       //��ݣ���������б�
    private String ip;                              //ip��ַ������hostname
    private String version;                        //�汾����ƽ̨4.5��д��4.5��
    private String devId;                          //�豸���
    private String serviceId;                      //������
    private String auditDate;                      //������ڣ���ʽ��yyyy-mm-dd��
    private String auditTime;                      //�����ϸʱ�䣬��ʽ��yyyy��mm��dd��HHʱMM��SS�롱
    private String source;                         //������壬�����ʶ�
    private String action;                         //�������ͣ����磺����
    private String dest;                           //��ƿ��壬�������ʶ�
    private long flow;                            //��������λ��B
    private long auditNum;                        //�����
    private String result;                         //������ɹ�����ʧ�ܣ�����¼����ƽ����
    private String desc;                           //����

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

    public String getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(String auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public long getFlow() {
        return flow;
    }

    public void setFlow(long flow) {
        this.flow = flow;
    }

    public long getAuditNum() {
        return auditNum;
    }

    public void setAuditNum(long auditNum) {
        this.auditNum = auditNum;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

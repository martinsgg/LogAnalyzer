package entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table
public class LogEntry {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment") 
	@Column
	Integer id;
	
	@Column
	Date requestTime;
	
	@Column
	String ip;
	
	@Column
	String requestType;
	
	@Column
	Integer http_code;
	
	@Column
	String deviceAndBrowser;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Integer getHttp_code() {
		return http_code;
	}

	public void setHttp_code(Integer http_code) {
		this.http_code = http_code;
	}

	public String getDeviceAndBrowser() {
		return deviceAndBrowser;
	}

	public void setDeviceAndBrowser(String deviceAndBrowser) {
		this.deviceAndBrowser = deviceAndBrowser;
	}
	
}

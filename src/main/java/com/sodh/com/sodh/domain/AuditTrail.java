package com.sodh.com.sodh.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by dhruti on 29/12/16.
 */
@Entity
@Table(name = "audit_trail")
public class AuditTrail extends BaseDomain {

	@Id
	@GeneratedValue
	private Long id;
	private String tableName;
	private String objectId;
	private String ipAddress;
	private String oldEntries;
	private String newEntries;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getOldEntries() {
		return oldEntries;
	}

	public void setOldEntries(String oldEntries) {
		this.oldEntries = oldEntries;
	}

	public String getNewEntries() {
		return newEntries;
	}

	public void setNewEntries(String newEntries) {
		this.newEntries = newEntries;
	}

	/*
	* Overridden for debugging during development.
	 */
	@Override
	public String toString() {
		return "\nTable Name = " + tableName + "\n" +
				"\nObject Id = " + objectId + "\n" +
				"\nOld Entries = " + oldEntries + "\n" +
				"\nIP Address = " + ipAddress + "\n" +
				"\nCreated By = " + createdBy + "\n" +
				"\nNew Entries= " + newEntries + "\n" +
				"\nCreated On= " + createdOn + "\n";
	}
}


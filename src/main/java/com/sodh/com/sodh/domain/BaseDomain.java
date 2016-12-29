package com.sodh.com.sodh.domain;

import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * base domain for all database tables Created by dhruti on 29/12/16.
 */
@MappedSuperclass
public class BaseDomain {

	protected Date createdOn, updatedOn;
	protected String createdBy, updatedBy;

	public BaseDomain() {

	}

	/*
	 * Added copy constructor for the sole purpose of use in Audit Trail.
	 *
	 * Important: Do not forget to add additional properties along the course of
	 * development or else the new properties would not get accounted in the
	 * Audit Trail.
	 */
	public BaseDomain(BaseDomain baseDomain) {
		if (baseDomain == null) {
			return;
		}
		this.createdOn = baseDomain.createdOn;
		this.updatedOn = baseDomain.updatedOn;
		this.createdBy = baseDomain.createdBy;
		this.updatedBy = baseDomain.updatedBy;
	}

	public Date getCreatedOn() {
		return createdOn != null ? new Date(createdOn.getTime()) : null;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn != null ? new Date(createdOn.getTime()) : null;
	}

	public Date getUpdatedOn() {
		return updatedOn != null ? new Date(updatedOn.getTime()) : null;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn != null ? new Date(updatedOn.getTime()) : null;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
}

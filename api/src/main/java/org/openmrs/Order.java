/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.order.OrderUtil;
import org.openmrs.util.OpenmrsUtil;

/**
 * Encapsulates information about the clinical action of a provider requesting something for a
 * patient e.g requesting a test to be performed, prescribing a medication, requesting the patient
 * to enroll on a specific diet etc. There is the notion of effective dates, these are used to
 * determine the span of an order's schedule i.e its effective start and stop dates therefore dates
 * should be interpreted as follows: The effective start of the schedule is the scheduledDate if
 * urgency is set to ON_SCHEDULED_DATE otherwise it is the dateActivated; the effective end date is
 * dateStopped, if it is null then it is the autoExpireDate. For DrugOrders, if the autoExpireDate
 * is not specified then it will be calculated and set by the API based on the duration and
 * frequency, note that frequency is only used in case duration is specified as a recurring interval
 * e.g. 3 times.
 * 
 * @version 1.0
 */
public class Order extends BaseOpenmrsData implements java.io.Serializable {
	
	public static final long serialVersionUID = 4334343L;
	
	/**
	 * @since 1.9.2, 1.10
	 */
	public enum Urgency {
		ROUTINE, STAT, ON_SCHEDULED_DATE
	}
	
	/**
	 * @since 1.10
	 */
	public enum Action {
		NEW, REVISE, DISCONTINUE, RENEW
	}
	
	private static final Log log = LogFactory.getLog(Order.class);
	
	// Fields
	
	private Integer orderId;
	
	private Patient patient;
	
	private Concept concept;
	
	private String instructions;
	
	private Date dateActivated;
	
	private Date autoExpireDate;
	
	private Encounter encounter;
	
	private Provider orderer;
	
	private Date dateStopped;
	
	private Concept orderReason;
	
	private String accessionNumber;
	
	private String orderReasonNonCoded;
	
	private Urgency urgency = Urgency.ROUTINE;
	
	private String orderNumber;
	
	private String commentToFulfiller;
	
	private CareSetting careSetting;
	
	private OrderType orderType;
	
	private Date scheduledDate;
	
	/**
	 * Allows orders to be linked to a previous order - e.g., an order discontinue ampicillin linked
	 * to the original ampicillin order (the D/C gets its own order number)
	 */
	private Order previousOrder;
	
	/**
	 * Represents the action being taken on an order.
	 * 
	 * @see org.openmrs.Order.Action
	 */
	private Action action = Action.NEW;
	
	// Constructors
	
	/** default constructor */
	public Order() {
	}
	
	/** constructor with id */
	public Order(Integer orderId) {
		this.orderId = orderId;
	}
	
	/**
	 * Performs a shallow copy of this Order. Does NOT copy orderId.
	 * 
	 * @return a shallow copy of this Order
	 * @should copy all fields
	 */
	public Order copy() {
		return copyHelper(new Order());
	}
	
	/**
	 * The purpose of this method is to allow subclasses of Order to delegate a portion of their
	 * copy() method back to the superclass, in case the base class implementation changes.
	 * 
	 * @param target an Order that will have the state of <code>this</code> copied into it
	 * @return Returns the Order that was passed in, with state copied into it
	 */
	protected Order copyHelper(Order target) {
		target.setPatient(getPatient());
		target.setConcept(getConcept());
		target.setOrderType(getOrderType());
		target.setInstructions(getInstructions());
		target.setDateActivated(getDateActivated());
		target.setAutoExpireDate(getAutoExpireDate());
		target.setEncounter(getEncounter());
		target.setOrderer(getOrderer());
		target.setCreator(getCreator());
		target.setDateCreated(getDateCreated());
		target.dateStopped = getDateStopped();
		target.setOrderReason(getOrderReason());
		target.setOrderReasonNonCoded(getOrderReasonNonCoded());
		target.setAccessionNumber(getAccessionNumber());
		target.setVoided(isVoided());
		target.setVoidedBy(getVoidedBy());
		target.setDateVoided(getDateVoided());
		target.setVoidReason(getVoidReason());
		target.setUrgency(getUrgency());
		target.setCommentToFulfiller(getCommentToFulfiller());
		target.previousOrder = getPreviousOrder();
		target.action = getAction();
		target.orderNumber = getOrderNumber();
		target.setCareSetting(getCareSetting());
		target.setChangedBy(getChangedBy());
		target.setDateChanged(getDateChanged());
		target.setScheduledDate(getScheduledDate());
		return target;
	}
	
	// Property accessors
	
	/**
	 * @return Returns the autoExpireDate.
	 */
	
	public Date getAutoExpireDate() {
		return autoExpireDate;
	}
	
	/**
	 * @param autoExpireDate The autoExpireDate to set.
	 */
	public void setAutoExpireDate(Date autoExpireDate) {
		this.autoExpireDate = autoExpireDate;
	}
	
	/**
	 * @return Returns the concept.
	 */
	public Concept getConcept() {
		return concept;
	}
	
	/**
	 * @param concept The concept to set.
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	
	/**
	 * @return the scheduledDate
	 * @since 1.10
	 */
	public Date getScheduledDate() {
		return scheduledDate;
	}
	
	/**
	 * @param scheduledDate the date to set
	 * @since 1.10
	 */
	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
	
	/**
	 * @return Returns the dateStopped.
	 * @since 1.10
	 */
	public Date getDateStopped() {
		return dateStopped;
	}
	
	/**
	 * @return Returns the orderReason.
	 */
	public Concept getOrderReason() {
		return orderReason;
	}
	
	/**
	 * @param orderReason The orderReason to set.
	 */
	public void setOrderReason(Concept orderReason) {
		this.orderReason = orderReason;
	}
	
	/**
	 * @return Returns the encounter.
	 */
	public Encounter getEncounter() {
		return encounter;
	}
	
	/**
	 * @param encounter The encounter to set.
	 */
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}
	
	/**
	 * @return Returns the instructions.
	 */
	public String getInstructions() {
		return instructions;
	}
	
	/**
	 * @param instructions The instructions to set.
	 */
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	
	/**
	 * @return Returns the accessionNumber.
	 */
	public String getAccessionNumber() {
		return accessionNumber;
	}
	
	/**
	 * @param accessionNumber The accessionNumber to set.
	 */
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	
	/**
	 * @return Returns the orderer.
	 */
	public Provider getOrderer() {
		return orderer;
	}
	
	/**
	 * @param orderer The orderer to set.
	 */
	public void setOrderer(Provider orderer) {
		this.orderer = orderer;
	}
	
	/**
	 * @return Returns the orderId.
	 */
	public Integer getOrderId() {
		return orderId;
	}
	
	/**
	 * @param orderId The orderId to set.
	 */
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	/**
	 * @return Returns the dateActivated.
	 */
	public Date getDateActivated() {
		return dateActivated;
	}
	
	/**
	 * @param dateActivated The dateActivated to set.
	 */
	public void setDateActivated(Date dateActivated) {
		this.dateActivated = dateActivated;
	}
	
	/**
	 * @return Returns the orderReasonNonCoded.
	 */
	public String getOrderReasonNonCoded() {
		return orderReasonNonCoded;
	}
	
	/**
	 * @param orderReasonNonCoded The orderReasonNonCoded to set.
	 */
	public void setOrderReasonNonCoded(String orderReasonNonCoded) {
		this.orderReasonNonCoded = orderReasonNonCoded;
	}
	
	/**
	 * @return the commentToFulfiller
	 * @since 1.10
	 */
	public String getCommentToFulfiller() {
		return commentToFulfiller;
	}
	
	/**
	 * @param commentToFulfiller The commentToFulfiller to set
	 * @since 1.10
	 */
	public void setCommentToFulfiller(String commentToFulfiller) {
		this.commentToFulfiller = commentToFulfiller;
	}
	
	/**
	 * Convenience method to determine if the order was active as of the current date
	 * 
	 * @since 1.10.1
	 * @return boolean indicating whether the order was active on the check date
	 */
	public boolean isActive() {
		return isActive(new Date());
	}
	
	/**
	 * Convenience method to determine if the order is active as of the specified date
	 * 
	 * @param checkDate - the date on which to check order. if null, will use current date
	 * @return boolean indicating whether the order was active on the check date
	 * @since 1.10.1
	 * @should return true if an order expired on the check date
	 * @should return true if an order was discontinued on the check date
	 * @should return true if an order was activated on the check date
	 * @should return false for a voided order
	 * @should return false for a discontinued order
	 * @should return false for an expired order
	 * @should return false for an order activated after the check date
	 * @should return false for a discontinuation order
	 */
	public boolean isActive(Date checkDate) {
		if (isVoided() || action == Action.DISCONTINUE) {
			return false;
		}
		if (checkDate == null) {
			checkDate = new Date();
		}
		
		return !isFuture(checkDate) && !isDiscontinued(checkDate) && !isExpired(checkDate);
	}
	
	/**
	 * Convenience method to determine if order is current
	 * 
	 * @see #isActive(java.util.Date)
	 * @param checkDate - the date on which to check order. if null, will use current date
	 * @return boolean indicating whether the order was current on the input date
	 */
	@Deprecated
	public boolean isCurrent(Date checkDate) {
		return isActive(checkDate);
	}
	
	/**
	 * @see #isActive()
	 * @return
	 */
	@Deprecated
	public boolean isCurrent() {
		return isActive(new Date());
	}
	
	/**
	 * Convenience method to determine if the order is not yet activated as of the given date
	 * 
	 * @deprecated use isStarted(java.util.Date)
	 * @see #isStarted(java.util.Date)
	 * @param checkDate - the date on which to check order. if null, will use current date
	 * @return boolean indicating whether the order was activated after the check date
	 * @should return false for a voided order
	 * @should return false if dateActivated is null
	 * @should return false if order was activated on the check date
	 * @should return true if order was activated after the check date
	 */
	@Deprecated
	public boolean isFuture(Date checkDate) {
		if (isVoided())
			return false;
		if (checkDate == null) {
			checkDate = new Date();
		}
		
		return dateActivated != null && checkDate.before(dateActivated);
	}
	
	/**
	 * @deprecated use isStarted()
	 * @see #isStarted()
	 * @return
	 */
	@Deprecated
	public boolean isFuture() {
		return isFuture(new Date());
	}
	
	/**
	 * Convenience method to determine if order is started as of the current date
	 * 
	 * @return boolean indicating whether the order is started as of the current date
	 * @since 1.10.1
	 * @see #isStarted(java.util.Date)
	 */
	public boolean isStarted() {
		return isStarted(new Date());
	}
	
	/**
	 * Convenience method to determine if the order is started as of the specified date, returns
	 * true only if the order has been activated. In case of scheduled orders, the scheduledDate
	 * becomes the effective start date that gets used to determined if it is started.
	 * 
	 * @param checkDate - the date on which to check order. if null, will use current date
	 * @return boolean indicating whether the order is started as of the check date
	 * @since 1.10.1
	 * @should return false for a voided order
	 * @should return false if dateActivated is null
	 * @should return false if the order is not yet activated as of the check date
	 * @should return false if the order was scheduled to start after the check date
	 * @should return true if the order was scheduled to start on the check date
	 * @should return true if the order was scheduled to start before the check date
	 * @should return true if the order is started and not scheduled
	 */
	public boolean isStarted(Date checkDate) {
		if (isVoided()) {
			return false;
		}
		if (checkDate == null) {
			checkDate = new Date();
		}
		if (getEffectiveStartDate() == null) {
			return false;
		}
		return !checkDate.before(getEffectiveStartDate());
	}
	
	/**
	 * Convenience method to determine if the order is discontinued as of the specified date
	 * 
	 * @param checkDate - the date on which to check order. if null, will use current date
	 * @return boolean indicating whether the order was discontinued on the input date
	 * @should return false for a voided order
	 * @should return false if date stopped and auto expire date are both null
	 * @should return false if auto expire date is null and date stopped is equal to check date
	 * @should return false if auto expire date is null and date stopped is after check date
	 * @should return false if dateActivated is after check date
	 * @should return true if auto expire date is null and date stopped is before check date
	 * @should fail if date stopped is after auto expire date
	 * @should return true if check date is after date stopped but before auto expire date
	 * @should return true if check date is after both date stopped auto expire date
	 */
	public boolean isDiscontinued(Date checkDate) {
		if (dateStopped != null && autoExpireDate != null && dateStopped.after(autoExpireDate)) {
			throw new APIException("The order has invalid dateStopped and autoExpireDate values");
		}
		if (isVoided()) {
			return false;
		}
		if (checkDate == null) {
			checkDate = new Date();
		}
		if (dateActivated == null || isFuture(checkDate) || dateStopped == null) {
			return false;
		}
		return checkDate.after(dateStopped);
	}
	
	/**
	 * Convenience method to determine if the order is expired as of the specified date
	 * 
	 * @return boolean indicating whether the order is expired at the current time
	 * @since 1.10.1
	 */
	public boolean isExpired() {
		return isExpired(new Date());
	}
	
	/**
	 * Convenience method to determine if order was expired at a given time
	 * 
	 * @param checkDate - the date on which to check order. if null, will use current date
	 * @return boolean indicating whether the order was expired on the input date
	 * @should return false for a voided order
	 * @should return false if date stopped and auto expire date are both null
	 * @should return false if date stopped is null and auto expire date is equal to check date
	 * @should return false if date stopped is null and auto expire date is after check date
	 * @should return false if check date is after both date stopped auto expire date
	 * @should return false if dateActivated is after check date
	 * @should return false if check date is after date stopped but before auto expire date
	 * @should fail if date stopped is after auto expire date
	 * @should return true if date stopped is null and auto expire date is before check date
	 * @since 1.10.1
	 */
	public boolean isExpired(Date checkDate) {
		if (dateStopped != null && autoExpireDate != null && dateStopped.after(autoExpireDate)) {
			throw new APIException("The order has invalid dateStopped and autoExpireDate values");
		}
		if (isVoided()) {
			return false;
		}
		if (checkDate == null) {
			checkDate = new Date();
		}
		if (dateActivated == null || isFuture(checkDate)) {
			return false;
		}
		if (isDiscontinued(checkDate) || autoExpireDate == null) {
			return false;
		}
		
		return checkDate.after(autoExpireDate);
	}
	
	/*
	 * orderForm:jsp: <spring:bind path="order.discontinued" /> results in a call to
	 * isDiscontinued() which doesn't give access to the discontinued property so renamed it to
	 * isDiscontinuedRightNow which results in a call to getDiscontinued.
	 * @since 1.5
	 */
	public boolean isDiscontinuedRightNow() {
		return isDiscontinued(new Date());
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public Integer getId() {
		return getOrderId();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String prefix = Action.DISCONTINUE == getAction() ? "DC " : "";
		return prefix + "Order. orderId: " + orderId + " patient: " + patient + " concept: " + concept + " care setting: "
		        + careSetting;
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		setOrderId(id);
		
	}
	
	/**
	 * @return the urgency
	 * @since 1.9.2
	 */
	public Urgency getUrgency() {
		return urgency;
	}
	
	/**
	 * @param urgency the urgency to set
	 * @since 1.9.2
	 */
	public void setUrgency(Urgency urgency) {
		this.urgency = urgency;
	}
	
	/**
	 * @return the orderNumber
	 * @since 1.10
	 */
	public String getOrderNumber() {
		return orderNumber;
	}
	
	/**
	 * Gets the previous related order.
	 * 
	 * @since 1.10
	 * @return the previous order.
	 */
	public Order getPreviousOrder() {
		return HibernateUtil.getRealObjectFromProxy(previousOrder);
	}
	
	/**
	 * Sets the previous order.
	 * 
	 * @since 1.10
	 * @param previousOrder the previous order to set.
	 */
	public void setPreviousOrder(Order previousOrder) {
		this.previousOrder = previousOrder;
	}
	
	/**
	 * Gets the action
	 * 
	 * @return the action
	 * @since 1.10
	 */
	public Action getAction() {
		return action;
	}
	
	/**
	 * Sets the ation
	 * 
	 * @param action the action to set
	 * @since 1.10
	 */
	public void setAction(Action action) {
		this.action = action;
	}
	
	/**
	 * Gets the careSetting
	 * 
	 * @return the action
	 * @since 1.10
	 */
	public CareSetting getCareSetting() {
		return careSetting;
	}
	
	/**
	 * Sets the careSetting
	 * 
	 * @param careSetting the action to set
	 * @since 1.10
	 */
	public void setCareSetting(CareSetting careSetting) {
		this.careSetting = careSetting;
	}
	
	/**
	 * Get the {@link org.openmrs.OrderType}
	 * 
	 * @return the {@link org.openmrs.OrderType}
	 */
	public OrderType getOrderType() {
		return orderType;
	}
	
	/**
	 * Set the {@link org.openmrs.OrderType}
	 * 
	 * @param orderType the {@link org.openmrs.OrderType}
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
	
	/**
	 * Creates a discontinuation order for this order, sets the previousOrder and action fields,
	 * note that the discontinuation order needs to be saved for the discontinuation to take effect
	 * 
	 * @return the newly created order
	 * @since 1.10
	 * @should set all the relevant fields
	 */
	public Order cloneForDiscontinuing() {
		Order newOrder = new Order();
		newOrder.setCareSetting(getCareSetting());
		newOrder.setConcept(getConcept());
		newOrder.setAction(Action.DISCONTINUE);
		newOrder.setPreviousOrder(this);
		newOrder.setPatient(getPatient());
		newOrder.setOrderType(getOrderType());
		
		return newOrder;
	}
	
	/**
	 * Creates an order for revision from this order, sets the previousOrder and action field.
	 * 
	 * @return the newly created order
	 * @since 1.10
	 * @should set all the relevant fields
	 * @should set the relevant fields for a DC order
	 */
	public Order cloneForRevision() {
		return cloneForRevisionHelper(new Order());
	}
	
	/**
	 * The purpose of this method is to allow subclasses of Order to delegate a portion of their
	 * cloneForRevision() method back to the superclass, in case the base class implementation
	 * changes.
	 * 
	 * @param target an Order that will have the state of <code>this</code> copied into it
	 * @return Returns the Order that was passed in, with state copied into it
	 */
	protected Order cloneForRevisionHelper(Order target) {
		if (getAction() == Action.DISCONTINUE) {
			target.setAction(Action.DISCONTINUE);
			target.setPreviousOrder(getPreviousOrder());
			target.setDateActivated(getDateActivated());
		} else {
			target.setAction(Action.REVISE);
			target.setPreviousOrder(this);
			target.setAutoExpireDate(getAutoExpireDate());
		}
		target.setCareSetting(getCareSetting());
		target.setConcept(getConcept());
		target.setPatient(getPatient());
		target.setOrderType(getOrderType());
		target.setScheduledDate(getScheduledDate());
		target.setInstructions(getInstructions());
		target.setUrgency(getUrgency());
		target.setCommentToFulfiller(getCommentToFulfiller());
		target.setOrderReason(getOrderReason());
		target.setOrderReasonNonCoded(getOrderReasonNonCoded());
		
		return target;
	}
	
	/**
	 * Checks whether this order's orderType matches or is a sub type of the specified one
	 * 
	 * @since 1.10
	 * @param orderType the orderType to match on
	 * @return true if the type of the order matches or is a sub type of the other order
	 * @should true if it is the same or is a subtype
	 * @should false if it neither the same nor a subtype
	 */
	public boolean isType(OrderType orderType) {
		return OrderUtil.isType(orderType, this.orderType);
	}
	
	/**
	 * Checks whether orderable of this order is same as other order
	 * 
	 * @see org.openmrs.DrugOrder for overridden behaviour
	 * @since 1.10
	 * @param otherOrder the other order to match on
	 * @return true if the concept of the orders match
	 * @should return false if the concept of the orders do not match
	 * @should return false if other order is null
	 * @should return true if the orders have the same concept
	 */
	public boolean hasSameOrderableAs(Order otherOrder) {
		if (otherOrder == null) {
			return false;
		}
		return OpenmrsUtil.nullSafeEquals(this.getConcept(), otherOrder.getConcept());
	}
	
	/**
	 * A convenience method to return start of the schedule for order.
	 * 
	 * @since 1.10
	 * @should return scheduledDate if Urgency is Scheduled
	 * @should return dateActivated if Urgency is not Scheduled
	 */
	public Date getEffectiveStartDate() {
		return this.urgency == Urgency.ON_SCHEDULED_DATE ? this.getScheduledDate() : this.getDateActivated();
	}
	
	/**
	 * A convenience method to return end of the schedule for order.
	 * 
	 * @since 1.10
	 * @should return dateStopped if dateStopped is not null
	 * @should return autoExpireDate if dateStopped is null
	 */
	public Date getEffectiveStopDate() {
		return this.getDateStopped() != null ? this.getDateStopped() : this.getAutoExpireDate();
	}
	
}

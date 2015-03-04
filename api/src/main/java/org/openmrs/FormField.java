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

/**
 * The FormField object relates/orders the <code>fields</code> on a <code>form</code> A form can
 * have many 0 to n fields associated with it in a hierarchical manor. This FormField object governs
 * what/how that takes place
 * 
 * @see org.openmrs.Form
 * @see org.openmrs.Field
 */
public class FormField extends BaseOpenmrsMetadata implements java.io.Serializable, Comparable<FormField> {
	
	public static final long serialVersionUID = 3456L;
	
	// Fields
	
	protected Integer formFieldId;
	
	protected FormField parent;
	
	protected Form form;
	
	protected Field field;
	
	protected Integer fieldNumber;
	
	protected String fieldPart;
	
	protected Integer pageNumber;
	
	protected Integer minOccurs;
	
	protected Integer maxOccurs;
	
	protected Boolean required = false;
	
	protected Float sortWeight;
	
	// Constructors
	
	/** default constructor */
	public FormField() {
	}
	
	/** constructor with id */
	public FormField(Integer formFieldId) {
		this.formFieldId = formFieldId;
	}
	
	/**
	 * Sort order for the form fields in the schema. Attempts: 1) sortWeight 2) fieldNumber 3)
	 * fieldPart 4) fieldName
	 * 
	 * @param f FormField to compare this object to
	 * @return -1, 0, or +1 depending on the difference between the FormFields
	 */
	public int compareTo(FormField f) {
		if (getSortWeight() != null || f.getSortWeight() != null) {
			if (getSortWeight() == null)
				return -1;
			if (f.getSortWeight() == null)
				return 1;
			int c = getSortWeight().compareTo(f.getSortWeight());
			if (c != 0)
				return c;
		}
		if (getPageNumber() != null || f.getPageNumber() != null) {
			if (getPageNumber() == null)
				return -1;
			if (f.getPageNumber() == null)
				return 1;
			int c = getPageNumber().compareTo(f.getPageNumber());
			if (c != 0)
				return c;
		}
		if (getFieldNumber() != null || f.getFieldNumber() != null) {
			if (getFieldNumber() == null)
				return -1;
			if (f.getFieldNumber() == null)
				return 1;
			int c = getFieldNumber().compareTo(f.getFieldNumber());
			if (c != 0)
				return c;
		}
		if (getFieldPart() != null || f.getFieldPart() != null) {
			if (getFieldPart() == null)
				return -1;
			if (f.getFieldPart() == null)
				return 1;
			int c = getFieldPart().compareTo(f.getFieldPart());
			if (c != 0)
				return c;
		}
		if (getField() != null && f.getField() != null) {
			int c = getField().getName().compareTo(f.getField().getName());
			if (c != 0)
				return c;
		}
		if (getFormFieldId() == null && f.getFormFieldId() != null)
			return -1;
		if (getFormFieldId() != null && f.getFormFieldId() == null)
			return 1;
		if (getFormFieldId() == null && f.getFormFieldId() == null)
			return 1;
		
		return getFormFieldId().compareTo(f.getFormFieldId());
		
	}
	
	// Property accessors
	
	/**
	 * @return Returns the formFieldId.
	 */
	public Integer getFormFieldId() {
		return formFieldId;
	}
	
	/**
	 * @param formFieldId The formFieldId to set.
	 */
	public void setFormFieldId(Integer formFieldId) {
		this.formFieldId = formFieldId;
	}
	
	/**
	 * @return Returns the parent FormField.
	 */
	public FormField getParent() {
		return parent;
	}
	
	/**
	 * @param parent The formField to set as parent.
	 */
	public void setParent(FormField parent) {
		this.parent = parent;
	}
	
	/**
	 * @return Returns the form.
	 */
	public Form getForm() {
		return form;
	}
	
	/**
	 * @param form The form to set.
	 */
	public void setForm(Form form) {
		this.form = form;
	}
	
	/**
	 * @return Returns the field.
	 */
	public Field getField() {
		return field;
	}
	
	/**
	 * @param field The field to set.
	 */
	public void setField(Field field) {
		this.field = field;
	}
	
	/**
	 * @return Returns the fieldNumber.
	 */
	public Integer getFieldNumber() {
		return fieldNumber;
	}
	
	/**
	 * @param fieldNumber The fieldNumber to set.
	 */
	public void setFieldNumber(Integer fieldNumber) {
		this.fieldNumber = fieldNumber;
	}
	
	/**
	 * @return Returns the fieldPart.
	 */
	public String getFieldPart() {
		return fieldPart;
	}
	
	/**
	 * @param fieldPart The fieldPart to set.
	 */
	public void setFieldPart(String fieldPart) {
		this.fieldPart = fieldPart;
	}
	
	/**
	 * @return Returns the pageNumber.
	 */
	public Integer getPageNumber() {
		return pageNumber;
	}
	
	/**
	 * @param pageNumber The pageNumber to set.
	 */
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	/**
	 * @return Returns the minOccurs.
	 */
	public Integer getMinOccurs() {
		return minOccurs;
	}
	
	/**
	 * @param minOccurs The minOccurs to set.
	 */
	public void setMinOccurs(Integer minOccurs) {
		this.minOccurs = minOccurs;
	}
	
	/**
	 * @return Returns the maxOccurs.
	 */
	public Integer getMaxOccurs() {
		return maxOccurs;
	}
	
	/**
	 * @param maxOccurs The maxOccurs to set.
	 */
	public void setMaxOccurs(Integer maxOccurs) {
		this.maxOccurs = maxOccurs;
	}
	
	/**
	 * @return Returns the required status.
	 */
	public Boolean isRequired() {
		return (required == null ? false : required);
	}
	
	/**
	 * @return same as isRequired()
	 */
	public Boolean getRequired() {
		return isRequired();
	}
	
	/**
	 * @param required The required status to set.
	 */
	public void setRequired(Boolean required) {
		this.required = required;
	}
	
	/**
	 * @return Returns the sortWeight.
	 */
	public Float getSortWeight() {
		return sortWeight;
	}
	
	/**
	 * @param sortWeight The weight to order the formFields on.
	 */
	public void setSortWeight(Float sortWeight) {
		this.sortWeight = sortWeight;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (formFieldId == null)
			return "null";
		
		return this.formFieldId.toString();
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	public Integer getId() {
		return getFormFieldId();
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		setFormFieldId(id);
		
	}
	
}

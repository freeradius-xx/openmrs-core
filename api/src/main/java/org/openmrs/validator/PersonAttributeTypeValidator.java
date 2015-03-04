/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.validator;

import org.openmrs.PersonAttributeType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates the {@link PersonAttributeType} class.
 * 
 * @since 1.5
 */
@Handler(supports = { PersonAttributeType.class }, order = 50)
public class PersonAttributeTypeValidator implements Validator {
	
	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return PersonAttributeType.class.isAssignableFrom(c);
	}
	
	/**
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 * @should fail validation if name is null
	 * @should fail validation if name already in use
	 * @should pass validation if all fields are correct
	 */
	public void validate(Object obj, Errors errors) {
		PersonAttributeType patObj = (PersonAttributeType) obj;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "PersonAttributeType.error.nameEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.description");
		PersonService ps = Context.getPersonService();
		PersonAttributeType pat = ps.getPersonAttributeTypeByName(patObj.getName());
		if (pat != null && !pat.getPersonAttributeTypeId().equals(patObj.getPersonAttributeTypeId())) {
			errors.rejectValue("name", "PersonAttributeType.error.nameAlreadyInUse");
		}
	}
}

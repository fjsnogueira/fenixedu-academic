/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 20/Out/2004
 *
 */
package org.fenixedu.academic.service.services.student;

import static org.fenixedu.academic.predicate.AccessControl.check;

import org.fenixedu.academic.domain.Grouping;
import org.fenixedu.academic.domain.StudentGroup;
import org.fenixedu.academic.predicate.RolePredicates;
import org.fenixedu.academic.service.services.exceptions.ExistingServiceException;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.service.services.exceptions.InvalidArgumentsServiceException;
import org.fenixedu.academic.service.services.exceptions.InvalidSituationServiceException;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

/**
 * @author joaosa & rmalo
 * 
 */
public class VerifyGroupingAndStudentGroupWithoutShift {

    @Atomic
    public static Integer run(String studentGroupCode, String groupPropertiesCode, String shiftCodeString, String username)
            throws FenixServiceException {
        check(RolePredicates.STUDENT_PREDICATE);
        Grouping groupProperties = FenixFramework.getDomainObject(groupPropertiesCode);

        if (groupProperties == null) {
            throw new ExistingServiceException();
        }

        StudentGroup studentGroup = FenixFramework.getDomainObject(studentGroupCode);

        if (studentGroup == null) {
            throw new InvalidSituationServiceException();
        }

        if (studentGroup.getShift() != null && shiftCodeString == null) {
            throw new InvalidArgumentsServiceException();
        }

        if (studentGroup.getShift() == null) {
            if (shiftCodeString != null) {
                throw new InvalidArgumentsServiceException();
            }
        } else {
            if (!studentGroup.getShift().getExternalId().equals(shiftCodeString)) {
                throw new InvalidArgumentsServiceException();
            }
        }

        if (studentGroup.getShift() != null && groupProperties.getShiftType() != null) {
            return Integer.valueOf(1);
        }

        if (studentGroup.getShift() != null && groupProperties.getShiftType() == null) {
            return Integer.valueOf(2);
        }

        if (studentGroup.getShift() == null && groupProperties.getShiftType() != null) {
            return Integer.valueOf(3);
        }

        if (studentGroup.getShift() == null && groupProperties.getShiftType() == null) {
            return Integer.valueOf(4);
        }

        return Integer.valueOf(5);

    }
}
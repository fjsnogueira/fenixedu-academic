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
 * Created on 8/Jan/2005
 *
 */
package org.fenixedu.academic.service.services.publico;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.Grouping;
import org.fenixedu.academic.domain.StudentGroup;
import org.fenixedu.academic.dto.InfoGrouping;
import org.fenixedu.academic.dto.InfoSiteStudentAndGroup;
import org.fenixedu.academic.dto.InfoSiteStudentInformation;
import org.fenixedu.academic.dto.InfoSiteStudentsAndGroups;
import org.fenixedu.academic.dto.InfoStudentGroup;
import org.fenixedu.academic.service.services.exceptions.ExistingServiceException;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

/**
 * @author joaosa & rmalo
 * 
 */
public class ReadAllStudentsAndGroups {

    @Atomic
    public static InfoSiteStudentsAndGroups run(String groupingId) throws FenixServiceException {
        InfoSiteStudentsAndGroups infoSiteStudentsAndGroups = new InfoSiteStudentsAndGroups();

        Grouping grouping = FenixFramework.getDomainObject(groupingId);

        if (grouping == null) {
            throw new ExistingServiceException();
        }

        List infoSiteStudentsAndGroupsList = new ArrayList();
        List studentGroups = getAllStudentGroups(grouping);
        Iterator iterStudentGroups = studentGroups.iterator();
        while (iterStudentGroups.hasNext()) {

            Collection studentGroupAttendList;
            StudentGroup studentGroup = (StudentGroup) iterStudentGroups.next();

            studentGroupAttendList = studentGroup.getAttendsSet();

            Iterator iterStudentGroupAttendList = studentGroupAttendList.iterator();
            InfoSiteStudentInformation infoSiteStudentInformation = null;
            InfoSiteStudentAndGroup infoSiteStudentAndGroup = null;
            Attends attend = null;

            while (iterStudentGroupAttendList.hasNext()) {
                infoSiteStudentInformation = new InfoSiteStudentInformation();
                infoSiteStudentAndGroup = new InfoSiteStudentAndGroup();

                attend = (Attends) iterStudentGroupAttendList.next();

                infoSiteStudentAndGroup.setInfoStudentGroup(InfoStudentGroup.newInfoFromDomain(studentGroup));

                infoSiteStudentInformation.setNumber(attend.getRegistration().getNumber());

                infoSiteStudentInformation.setName(attend.getRegistration().getPerson().getName());

                infoSiteStudentInformation.setUsername((attend.getRegistration().getPerson().getUsername()));

                infoSiteStudentInformation.setEmail(attend.getRegistration().getPerson().getEmail());

                infoSiteStudentInformation.setPersonID(attend.getRegistration().getPerson().getExternalId());

                infoSiteStudentAndGroup.setInfoSiteStudentInformation(infoSiteStudentInformation);

                infoSiteStudentsAndGroupsList.add(infoSiteStudentAndGroup);
            }
        }

        Collections.sort(infoSiteStudentsAndGroupsList, new BeanComparator("infoStudentGroup.groupNumber"));

        infoSiteStudentsAndGroups.setInfoSiteStudentsAndGroupsList(infoSiteStudentsAndGroupsList);
        infoSiteStudentsAndGroups.setInfoGrouping(InfoGrouping.newInfoFromDomain(grouping));

        return infoSiteStudentsAndGroups;
    }

    private static List getAllStudentGroups(Grouping groupProperties) {
        List result = new ArrayList();
        Collection studentGroups = groupProperties.getStudentGroupsSet();
        result.addAll(studentGroups);
        return result;
    }
}
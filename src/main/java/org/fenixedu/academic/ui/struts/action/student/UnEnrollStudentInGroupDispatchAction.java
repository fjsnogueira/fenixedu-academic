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
 * Created on 27/Ago/2003
 *
 */
package org.fenixedu.academic.ui.struts.action.student;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.dto.InfoSiteStudentGroup;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.service.services.exceptions.InvalidArgumentsServiceException;
import org.fenixedu.academic.service.services.exceptions.InvalidSituationServiceException;
import org.fenixedu.academic.service.services.exceptions.NotAuthorizedException;
import org.fenixedu.academic.service.services.student.ReadStudentGroupInformation;
import org.fenixedu.academic.service.services.student.UnEnrollStudentInGroup;
import org.fenixedu.academic.service.services.student.VerifyStudentGroupAtributes;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.exceptions.FenixActionException;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;

/**
 * @author asnr and scpo
 * 
 */
@Mapping(module = "student", path = "/removeGroupEnrolment", formBean = "enroledExecutionCoursesForm",
        functionality = ViewEnroledExecutionCoursesAction.class)
@Forwards(value = { @Forward(name = "sucess", path = "/student/removeStudentInGroup.jsp"),
        @Forward(name = "insucess", path = "/student/viewEnroledExecutionCourses.do?method=prepare"),
        @Forward(name = "viewStudentGroupInformation", path = "/student/viewStudentGroupInformation.do"),
        @Forward(name = "viewShiftsAndGroups", path = "/student/viewShiftsAndGroups.do") })
public class UnEnrollStudentInGroupDispatchAction extends FenixDispatchAction {

    public ActionForward prepareRemove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixActionException {

        User userView = getUserView(request);

        String studentGroupCodeString = request.getParameter("studentGroupCode");

        String shiftCodeString = request.getParameter("shiftCode");
        request.setAttribute("shiftCode", shiftCodeString);

        try {
            VerifyStudentGroupAtributes.run(null, null, studentGroupCodeString, userView.getUsername(), new Integer(3));

        } catch (NotAuthorizedException e) {
            ActionErrors actionErrors = new ActionErrors();
            ActionError error = null;
            error = new ActionError("errors.noStudentInAttendsSetToDelete");
            actionErrors.add("errors.noStudentInAttendsSetToDelete", error);
            saveErrors(request, actionErrors);
            return mapping.findForward("insucess");

        } catch (InvalidSituationServiceException e) {
            ActionErrors actionErrors = new ActionErrors();
            ActionError error = null;
            // Create an ACTION_ERROR
            error = new ActionError("errors.removeEnrolment.studentNotEnroled");
            actionErrors.add("errors.removeEnrolment.studentNotEnroled", error);
            saveErrors(request, actionErrors);
            return mapping.findForward("viewStudentGroupInformation");

        } catch (InvalidArgumentsServiceException e) {
            ActionErrors actionErrors = new ActionErrors();
            ActionError error = null;
            // Create an ACTION_ERROR
            error = new ActionError("errors.removeEnrolment.minimumNumberOfElements");
            actionErrors.add("errors.removeEnrolment.minimumNumberOfElements", error);
            saveErrors(request, actionErrors);
            return mapping.findForward("viewStudentGroupInformation");

        } catch (FenixServiceException e) {
            ActionErrors actionErrors = new ActionErrors();
            ActionError error = null;
            error = new ActionError("error.noGroup");
            actionErrors.add("error.noGroup", error);
            saveErrors(request, actionErrors);
            return mapping.findForward("viewShiftsAndGroups");
        }

        InfoSiteStudentGroup viewStudentGroup;

        try {
            viewStudentGroup = ReadStudentGroupInformation.run(studentGroupCodeString);

        } catch (InvalidSituationServiceException e) {
            ActionErrors actionErrors = new ActionErrors();
            ActionError error = null;
            error = new ActionError("error.noGroup");
            actionErrors.add("error.noGroup", error);
            saveErrors(request, actionErrors);
            return mapping.findForward("viewShiftsAndGroups");
        } catch (FenixServiceException e) {
            throw new FenixActionException(e);
        }

        InfoSiteStudentGroup infoSiteStudentGroup = viewStudentGroup;

        request.setAttribute("infoSiteStudentGroup", infoSiteStudentGroup);

        return mapping.findForward("sucess");
    }

    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws FenixActionException {

        User userView = getUserView(request);
        String userName = userView.getUsername();

        String studentGroupCodeString = request.getParameter("studentGroupCode");

        Boolean shiftWithGroups;
        try {
            shiftWithGroups = UnEnrollStudentInGroup.run(userName, studentGroupCodeString);

        } catch (NotAuthorizedException e) {
            ActionErrors actionErrors = new ActionErrors();
            ActionError error = null;
            error = new ActionError("errors.noStudentInAttendsSetToDelete");
            actionErrors.add("errors.noStudentInAttendsSetToDelete", error);
            saveErrors(request, actionErrors);
            return mapping.findForward("insucess");

        } catch (InvalidSituationServiceException e) {
            ActionErrors actionErrors = new ActionErrors();
            ActionError error = null;
            error = new ActionError("error.noGroup");
            actionErrors.add("error.noGroup", error);
            saveErrors(request, actionErrors);
            return mapping.findForward("viewShiftsAndGroups");

        } catch (InvalidArgumentsServiceException e) {
            ActionErrors actionErrors = new ActionErrors();
            ActionError error = null;
            error = new ActionError("errors.removeEnrolment.studentNotEnroled");
            actionErrors.add("errors.removeEnrolment.studentNotEnroled", error);
            saveErrors(request, actionErrors);
            return mapping.findForward("viewStudentGroupInformation");

        } catch (FenixServiceException e) {
            throw new FenixActionException(e);

        } catch (DomainException e) {
            ActionErrors actionErrors = new ActionErrors();
            ActionError error = null;
            error = new ActionError("error.studentGroup.cannotRemoveAttendsBecauseAlreadyHasProjectSubmissions");
            actionErrors.add("error.studentGroup.cannotRemoveAttendsBecauseAlreadyHasProjectSubmissions", error);
            saveErrors(request, actionErrors);
            return mapping.findForward("insucess");
        }

        if (!shiftWithGroups.booleanValue()) {
            return mapping.findForward("viewShiftsAndGroups");
        }

        return mapping.findForward("viewStudentGroupInformation");

    }

}
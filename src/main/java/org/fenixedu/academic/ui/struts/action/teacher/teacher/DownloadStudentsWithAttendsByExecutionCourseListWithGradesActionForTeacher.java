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
package org.fenixedu.academic.ui.struts.action.teacher.teacher;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.Evaluation;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Mark;
import org.fenixedu.academic.domain.curriculum.EnrolmentEvaluationType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.teacher.ManageExecutionCourseDA;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.struts.annotations.Mapping;

import pt.utl.ist.fenix.tools.util.excel.Spreadsheet;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet.Row;

@Mapping(module = "teacher", path = "/getTabSeparatedStudentListWithGrades", functionality = ManageExecutionCourseDA.class)
public class DownloadStudentsWithAttendsByExecutionCourseListWithGradesActionForTeacher extends FenixDispatchAction {

    public ActionForward downloadStudentListForGradesForm(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {

        final ExecutionCourse executionCourse = getDomainObject(request, "executionCourseOID");

        final Spreadsheet spreadsheet = new Spreadsheet(executionCourse.getSigla());
        spreadsheet.setHeader(BundleUtil.getString(Bundle.APPLICATION, "label.username"));
        spreadsheet.setHeader(BundleUtil.getString(Bundle.APPLICATION, "label.number"));
        spreadsheet.setHeader(BundleUtil.getString(Bundle.APPLICATION, "label.name"));
        spreadsheet.setHeader(BundleUtil.getString(Bundle.APPLICATION, "label.degree.code"));
        spreadsheet.setHeader(BundleUtil.getString(Bundle.APPLICATION, "label.attends.enrollmentState"));
        final List<Evaluation> evaluations = executionCourse.getOrderedAssociatedEvaluations();
        for (final Evaluation evaluation : evaluations) {
            spreadsheet.setHeader(evaluation.getPresentationName());
        }

        for (final Attends attends : executionCourse.getOrderedAttends()) {
            final Registration registration = attends.getRegistration();
            final Row row = spreadsheet.addRow();
            row.setCell(registration.getPerson().getUsername());
            row.setCell(registration.getNumber());
            row.setCell(registration.getName());
            row.setCell(registration.getDegree().getSigla());
            if (attends.getEnrolment() != null) {
                final EnrolmentEvaluationType enrolmentEvaluationType = attends.getEnrolmentEvaluationType();
                row.setCell(enrolmentEvaluationType.getDescription());
            } else {
                row.setCell(BundleUtil.getString(Bundle.APPLICATION, "label.attends.enrollmentState.notEnrolled"));
            }
            for (final Evaluation evaluation : evaluations) {
                final Mark mark = findMark(attends, evaluation);
                if (mark == null) {
                    row.setCell(" ");
                } else {
                    row.setCell(mark.getMark());
                }
            }
        }

        try {
            response.setHeader("Content-disposition", "attachment; filename=" + executionCourse.getSigla() + ".xls");
            response.setContentType("application/vnd.ms-excel");

            final ServletOutputStream outputStream = response.getOutputStream();
            spreadsheet.exportToXLSSheet(outputStream);
            outputStream.close();
        } catch (final IOException e1) {
            throw new Error(e1);
        }

        return null;

    }

    private Mark findMark(final Attends attends, final Evaluation evaluation) {
        for (final Mark mark : attends.getAssociatedMarksSet()) {
            if (mark.getEvaluation() == evaluation) {
                return mark;
            }
        }
        return null;
    }

}
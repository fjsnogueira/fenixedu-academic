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
 * Created on Apr 3, 2003
 *
 */
package org.fenixedu.academic.servlet.taglib.sop.examsMapNew.renderers;

import javax.servlet.jsp.PageContext;

import org.fenixedu.academic.servlet.taglib.sop.examsMapNew.ExamsMap;
import org.fenixedu.academic.servlet.taglib.sop.examsMapNew.ExamsMapSlot;

/**
 * @author Luis Cruz & Sara Ribeiro
 * 
 */
public interface ExamsMapSlotContentRenderer {

    public StringBuilder renderDayLabel(ExamsMapSlot examsMapSlot, ExamsMap examsMap, String typeUser, PageContext pageContext);

    public StringBuilder renderDayContents(ExamsMapSlot examsMapSlot, Integer year1, Integer year2, String typeUser,
            PageContext pageContext);

    public StringBuilder renderDayContents(ExamsMapSlot slot, ExamsMap examsMap, String user, PageContext pageContext);
}
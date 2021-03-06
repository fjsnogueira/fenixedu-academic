<%--

    Copyright © 2002 Instituto Superior Técnico

    This file is part of FenixEdu Academic.

    FenixEdu Academic is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Academic is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:xhtml/>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="org.fenixedu.academic.util.EvaluationType" %>
<logic:present name="siteView">
<bean:message key="classification.nonOfficial.information" />
<br />
<bean:define id="marksListComponent" name="siteView" property="component" type="org.fenixedu.academic.dto.InfoSiteMarks"/>
<bean:define id="infoEvaluation" name="marksListComponent" property="infoEvaluation" type="org.fenixedu.academic.dto.InfoEvaluation"/>
<span class="error"><!-- Error messages go here --><html:errors /></span>
<table width="90%" align="center">
	<tr>
		<td colspan="3" align="center">
			<logic:equal name="infoEvaluation" property="evaluationType" value="<%= EvaluationType.EXAM_STRING %>">  		
				<h2>
					<bean:message key="label.exam"/>:				
					<bean:write name="marksListComponent" property="infoEvaluation.season"/>&nbsp;
					<bean:write name="marksListComponent" property="infoEvaluation.date"/> - 
					<bean:write name="marksListComponent" property="infoEvaluation.beginningHour"/>
				</h2>
				<br />
			</logic:equal>
			<logic:equal name="infoEvaluation" property="evaluationType" value="<%= EvaluationType.FINAL_STRING %>">  		
				<h2><bean:message key="label.publishedMarks"/>&nbsp;<%= EvaluationType.EXAM_STRING %></h2><br />
			</logic:equal>
			<br />
		</td>	   
	</tr> 
	<tr>
		<th class="listClasses-header">
			<bean:message key="label.number" /> 
	    </th>
		<th class="listClasses-header">
			<bean:message key="label.name" />
		</th>					
		<th class="listClasses-header">
			<bean:message key="label.mark" />
		</th>
	</tr>    		
		
	<logic:present name="marksListComponent" property="infoAttends">  								
	   	<logic:iterate id="attendElem" name="marksListComponent" property="infoAttends" type="org.fenixedu.academic.dto.InfoFrequenta"> 
	   		<bean:define id="studentNumber" name="attendElem" property="aluno.number" />
	   		<bean:define id="studentMark" value=""/>
			<logic:notEmpty name="marksListComponent" property='<%="marks(" + studentNumber + ")"%>'>
		    	<bean:define id="studentMark" name="marksListComponent" property='<%="marks(" + studentNumber + ")"%>' type="java.lang.String"/>
	    	</logic:notEmpty>
	    	
			<tr>
				<td class="listClasses">
					<bean:write name="attendElem" property="aluno.number"/>&nbsp;
				</td>
				<td class="listClasses">
					<bean:write name="attendElem" property="aluno.infoPerson.nome"/>
				</td>											
				<td class="listClasses">
					<logic:notEmpty name="studentMark">
						<bean:write name="studentMark"/>
					</logic:notEmpty>
					<logic:empty name="studentMark">
						&nbsp;
					</logic:empty>
				</td>
			</tr>
	   	</logic:iterate>
	</logic:present>
</table>    
</logic:present>   
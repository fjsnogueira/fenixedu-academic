<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/taglibs-datetime.tld" prefix="dt" %>

<html:messages id="messages" message="true">
	<span class="error"><bean:write name="messages" /></span>
</html:messages>

<html:form action="/executionDegreesManagement">
	<html:hidden property="method" value=""/>
	
	<div class='simpleblock4'>
	<fieldset class='lfloat'>
	
	<p><label><strong><bean:message bundle="MANAGER_RESOURCES" key="label.manager.degree.tipoCurso"/></strong>:</label>
	<html:select property="degreeType" onchange="this.form.method.value='readDegreeCurricularPlans';this.form.submit();" >
		<html:options collection="degreeTypes" property="value" labelProperty="label" /> 
	</html:select>
	</p>
	
	<logic:notEmpty name="degreeCurricularPlans">
		<p><label><strong><bean:message bundle="MANAGER_RESOURCES" key="label.manager.degreeCurricularPlan"/></strong>:</label>
		<html:select property="degreeCurricularPlanID" onchange="this.form.method.value='readExecutionDegrees';this.form.submit();">
			<html:options collection="degreeCurricularPlans" property="value" labelProperty="label" /> 
		</html:select>
		</p>
	</logic:notEmpty>
	
	</fieldset>
	</div>
	<br/>
	<logic:notEmpty name="executionDegrees">
		<table cellpadding='0' border='0'>
			<tr>
				<td class='listClasses-header'> <bean:message bundle="MANAGER_RESOURCES" key="label.manager.executionDegree.executionYear"/> </td>
				<td class='listClasses-header'> 
					<bean:message bundle="MANAGER_RESOURCES" key="label.manager.lessons"/>
					1� <bean:message bundle="MANAGER_RESOURCES" key="label.manager.semester"/>
				</td>
				<td class='listClasses-header'> 
					<bean:message bundle="MANAGER_RESOURCES" key="label.manager.exams"/>
					1� <bean:message bundle="MANAGER_RESOURCES" key="label.manager.semester"/>
				</td>
				<td class='listClasses-header'> 
					<bean:message bundle="MANAGER_RESOURCES" key="label.manager.lessons"/>
					2� <bean:message bundle="MANAGER_RESOURCES" key="label.manager.semester"/>
				</td>
				<td class='listClasses-header'> 
					<bean:message bundle="MANAGER_RESOURCES" key="label.manager.exams"/>
					2� <bean:message bundle="MANAGER_RESOURCES" key="label.manager.semester"/>
				</td>
				<td class='listClasses-header'> <bean:message bundle="MANAGER_RESOURCES" key="label.manager.executionDegree.temporaryExamMap"/> </td>
				<td class='listClasses-header'> <bean:message bundle="MANAGER_RESOURCES" key="label.manager.executionDegree.coordinator"/> </td>
				<td class='listClasses-header'> </td>
			</tr>
			<logic:iterate id="executionDegree" name="executionDegrees">
				<tr>
					<td class='listClasses'>
						<bean:write name="executionDegree" property="executionYear.year" /> 
					</td>
					<td class='listClasses'>
						<bean:define id="periodLessonsFirstSemester" name="executionDegree" property="periodLessonsFirstSemester" />
						<dt:format pattern="dd/MM/yyyy">
							<bean:write name="periodLessonsFirstSemester" property="start.time" />
						</dt:format>
						<p> <bean:message bundle="MANAGER_RESOURCES" key="label.manager.to" /> </p>
						<dt:format pattern="dd/MM/yyyy">
							<bean:write name="periodLessonsFirstSemester" property="end.time" />
						</dt:format>
					</td>
					<td class='listClasses'>
						<bean:define id="periodExamsFirstSemester" name="executionDegree" property="periodExamsFirstSemester" />
						<dt:format pattern="dd/MM/yyyy">
							<bean:write name="periodExamsFirstSemester" property="start.time" />
						</dt:format>
						<p> <bean:message bundle="MANAGER_RESOURCES" key="label.manager.to" /> </p>
						<dt:format pattern="dd/MM/yyyy">
							<bean:write name="periodExamsFirstSemester" property="end.time" />
						</dt:format>
					</td>
					<td class='listClasses'>
						<bean:define id="periodLessonsSecondSemester" name="executionDegree" property="periodLessonsSecondSemester" />
						<dt:format pattern="dd/MM/yyyy">
							<bean:write name="periodLessonsSecondSemester" property="start.time" />
						</dt:format>
						<p> <bean:message bundle="MANAGER_RESOURCES" key="label.manager.to" /> </p>
						<dt:format pattern="dd/MM/yyyy">
							<bean:write name="periodLessonsSecondSemester" property="end.time" />
						</dt:format>
					</td>
					<td class='listClasses'>
						<bean:define id="periodExamsSecondSemester" name="executionDegree" property="periodExamsSecondSemester" />
						<dt:format pattern="dd/MM/yyyy">
							<bean:write name="periodExamsSecondSemester" property="start.time" />
						</dt:format>
						<p> <bean:message bundle="MANAGER_RESOURCES" key="label.manager.to" /> </p>
						<dt:format pattern="dd/MM/yyyy">
							<bean:write name="periodExamsSecondSemester" property="end.time" />
						</dt:format>
					</td>
					<td class='listClasses'>
						<logic:equal name="executionDegree" property="temporaryExamMap" value="true">
							<bean:message bundle="MANAGER_RESOURCES" key="label.manager.yes" />
						</logic:equal>
						<logic:notEqual name="executionDegree" property="temporaryExamMap" value="true">
							<bean:message bundle="MANAGER_RESOURCES" key="label.manager.no" />
						</logic:notEqual>
					</td>
					<td class='listClasses'>
						<logic:notEmpty name="executionDegree"  property="coordinatorsList">
							<logic:iterate id="coordinator" name="executionDegree"  property="coordinatorsList">
								<bean:write name="coordinator" property="teacher.person.name" />
								<br/>
							</logic:iterate>
						</logic:notEmpty>
						<logic:empty name="executionDegree"  property="coordinatorsList">
							<bean:message bundle="MANAGER_RESOURCES" key="label.manager.noCoordinatorsList" />
						</logic:empty>
					</td>
					<td class='listClasses'>
						<html:link module="/manager" action="/executionDegreesManagement.do?method=prepareEditExecutionDegree" paramId="executionDegreeID" paramName="executionDegree" paramProperty="idInternal">
							<bean:message bundle="MANAGER_RESOURCES" key="link.edit"/>
						</html:link>
						, <br/>
						<html:link module="/manager" action="/executionDegreesManagement.do?method=readCoordinators" paramId="executionDegreeID" paramName="executionDegree" paramProperty="idInternal">
							<bean:message bundle="MANAGER_RESOURCES" key="label.manager.edit.executionDegree.coordinators" />
						</html:link>
					</td>
				</tr>
			</logic:iterate>
		</table>
	</logic:notEmpty>

</html:form>
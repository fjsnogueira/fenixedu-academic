package ServidorApresentacao.Action.sop;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.util.LabelValueBean;

import DataBeans.InfoExecutionPeriod;
import DataBeans.InfoLesson;
import DataBeans.InfoRoom;
import ServidorAplicacao.GestorServicos;
import ServidorAplicacao.IUserView;
import ServidorAplicacao.Servico.exceptions.InvalidTimeIntervalServiceException;
import ServidorApresentacao
	.Action
	.exceptions
	.InvalidTimeIntervalActionException;
import ServidorApresentacao.Action.sop.utils.ServiceUtils;
import ServidorApresentacao.Action.sop.utils.SessionConstants;
import ServidorApresentacao.Action.sop.utils.SessionUtils;
import ServidorApresentacao.Action.sop.utils.Util;
import ServidorApresentacao.validator.form.FenixDynaValidatorForm;
import Util.DiaSemana;

/**
 * @author jpvl
 */
public class SearchEmptyRoomsDispatchAction extends DispatchAction {

	/* (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#dispatchMethod(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	public ActionForward prepare(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		HttpSession session = request.getSession(false);
		session.setAttribute("minutes", Util.getMinutes());
		session.setAttribute("hours", Util.getHours());
		session.setAttribute("weekDays", Util.getDaysOfWeek());

		// execution period selection		
		IUserView userView = (IUserView) session.getAttribute("UserView");
		GestorServicos gestor = GestorServicos.manager();

		Object argsReadExecutionPeriods[] = {
		};
		ArrayList executionPeriods =
			(ArrayList) gestor.executar(
				userView,
				"ReadExecutionPeriods",
				argsReadExecutionPeriods);

		ArrayList executionPeriodsLabelValueList = new ArrayList();
		for (int i = 0; i < executionPeriods.size(); i++) {
			InfoExecutionPeriod infoExecutionPeriod =
				(InfoExecutionPeriod) executionPeriods.get(i);
			executionPeriodsLabelValueList.add(
				new LabelValueBean(
					infoExecutionPeriod.getName()
						+ " - "
						+ infoExecutionPeriod.getInfoExecutionYear().getYear(),
					"" + i));
		}

		session.setAttribute(
			SessionConstants.LIST_INFOEXECUTIONPERIOD,
			executionPeriods);

		request.setAttribute(
			SessionConstants.LABELLIST_EXECUTIONPERIOD,
			executionPeriodsLabelValueList);

		return mapping.findForward("searchPage");
	}

	/* (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#dispatchMethod(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	public ActionForward doSearch(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		try {
			FenixDynaValidatorForm searchForm = (FenixDynaValidatorForm) form;
			Integer normalCapacity = new Integer(0);
			try {
				normalCapacity =
					new Integer((String) searchForm.get("normalCapacity"));
			} catch (NumberFormatException e) {
				//ignored
			}

			Integer weekDay = new Integer((String) searchForm.get("weekDay"));

			Calendar start = Calendar.getInstance();
			start.set(
				Calendar.HOUR_OF_DAY,
				Integer.parseInt((String) searchForm.get("startHour")));
			start.set(
				Calendar.MINUTE,
				Integer.parseInt((String) searchForm.get("startMinutes")));
			start.set(Calendar.SECOND, 0);
			Calendar end = Calendar.getInstance();
			end.set(
				Calendar.HOUR_OF_DAY,
				Integer.parseInt((String) searchForm.get("endHour")));
			end.set(
				Calendar.MINUTE,
				Integer.parseInt((String) searchForm.get("endMinutes")));
			end.set(Calendar.SECOND, 0);

			InfoRoom infoRoom = new InfoRoom();
			infoRoom.setCapacidadeNormal(normalCapacity);

			InfoLesson infoLesson = new InfoLesson();

			infoLesson.setDiaSemana(new DiaSemana(weekDay));
			infoLesson.setInicio(start);
			infoLesson.setFim(end);

			//	execution period selection
			HttpSession session = request.getSession(false);

			ArrayList infoExecutionPeriodList =
				(ArrayList) session.getAttribute(
					SessionConstants.LIST_INFOEXECUTIONPERIOD);
			Integer index = (Integer) searchForm.get("executionPeriodIndex");

			InfoExecutionPeriod infoExecutionPeriod = null;
			if (infoExecutionPeriodList != null && index != null) {
				infoExecutionPeriod =
					(InfoExecutionPeriod) infoExecutionPeriodList.get(
						index.intValue());
				session.setAttribute(
					SessionConstants.INFO_EXECUTION_PERIOD_KEY,
					infoExecutionPeriod);
			}
			// --------------------------------	

			Object args[] = { infoRoom, infoLesson, infoExecutionPeriod };

			List emptyRoomsList =
				(List) ServiceUtils.executeService(
					SessionUtils.getUserView(request),
					"ReadEmptyRoomsService",
					args);

			if (emptyRoomsList == null || emptyRoomsList.isEmpty()) {
				ActionErrors actionErrors = new ActionErrors();
				actionErrors.add(
					"search.empty.rooms.no.rooms",
					new ActionError("search.empty.rooms.no.rooms"));

				saveErrors(request, actionErrors);
				return mapping.getInputForward();
			}
			// keep search criteria in request 
			// (executionPeriod is already in session...)
			request.setAttribute("weekDay", infoLesson.getDiaSemana());
			request.setAttribute(
				"intervalStart",
				timeToString(infoLesson.getInicio()));
			request.setAttribute(
				"intervalEnd",
				timeToString(infoLesson.getFim()));
			request.setAttribute("minimumCapacity", normalCapacity);
			//--------------------------------------------

			request.setAttribute("roomList", emptyRoomsList);

			return mapping.findForward("showSearchResult");

		} catch (InvalidTimeIntervalServiceException ex) {
			throw new InvalidTimeIntervalActionException(ex);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}

	private String timeToString(Calendar time) {
		String hourStr = "" + time.get(Calendar.HOUR_OF_DAY);
		String minutesStr = "" + time.get(Calendar.MINUTE);

		if (time.get(Calendar.HOUR_OF_DAY) < 10)
			hourStr = "0" + hourStr;
		if (time.get(Calendar.MINUTE) < 10)
			minutesStr = "0" + minutesStr;

		return hourStr + ":" + minutesStr;

	}

}

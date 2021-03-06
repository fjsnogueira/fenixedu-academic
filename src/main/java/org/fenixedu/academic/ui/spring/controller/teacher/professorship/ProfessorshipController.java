package org.fenixedu.academic.ui.spring.controller.teacher.professorship;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.TeacherAuthorization;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.ui.spring.controller.AcademicAdministrationSpringApplication;
import org.fenixedu.academic.ui.spring.controller.teacher.authorization.AuthorizationService;
import org.fenixedu.academic.ui.spring.controller.teacher.authorization.SearchBean;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = AcademicAdministrationSpringApplication.class, title = "teacher.professorships.title",
        accessGroup = "academic(MANAGE_TEACHER_PROFESSORSHIPS)")
@RequestMapping("/teacher/professorships")
public class ProfessorshipController {

    @Autowired
    private ProfessorshipService professorshipService;

    @Autowired
    private AuthorizationService authorizationService;

    private String view(String string) {
        return "fenixedu-academic/teacher/professorship/" + string;
    }

    /***
     * This funcionality entrypoint.
     * Shows a web view that lists the teacher authorizations for the selected period and department
     * 
     * @param search the web view filter bean (period and department)
     * @param authorization used to scroll to a specific authorization
     * @return
     */

    @RequestMapping(method = GET)
    public String home(Model model, @ModelAttribute SearchBean search,
            @RequestParam(defaultValue = "null") TeacherAuthorization authorization) {
        if (search.getPeriod() == null) {
            search.setPeriod(authorizationService.getCurrentPeriod());
        }

        //TODO: previously select the current user department?

        // Used to scroll to a specific authorization in the webview
        model.addAttribute("authorization", authorization);

        model.addAttribute("departments", authorizationService.getDepartments());
        model.addAttribute("periods", authorizationService.getExecutionPeriods());
        model.addAttribute("search", search);
        model.addAttribute("authorizations", authorizationService.searchAuthorizations(search));
        return view("show");
    }

    /***
     * Shows web view to create a new professorship (associate course with teacher)
     * 
     * @param authorization the teacher information
     * @param bean the bean where the professorship info will be stored
     * @return
     */

    @RequestMapping(method = GET, value = "{authorization}")
    public String showCreate(Model model, @PathVariable TeacherAuthorization authorization,
            @ModelAttribute CreateProfessorshipBean bean) {

        if (bean.getPeriod() == null) {
            bean.setPeriod(authorization.getExecutionSemester());
        }

        final List<ExecutionDegree> degrees = professorshipService.getDegrees(bean.getPeriod());
        if (bean.getDegree() == null && !degrees.isEmpty()) {
            bean.setDegree(degrees.get(0));
        }

        model.addAttribute("bean", bean);
        model.addAttribute("authorization", authorization);
        model.addAttribute("degrees", degrees);
        model.addAttribute("periods", authorizationService.getExecutionPeriods(authorization.getTeacher()));
        model.addAttribute("courses", professorshipService.getCourses(bean.getDegree(), bean.getPeriod()));
        return view("create");
    }

    /***
     * Creates a new professorship for the teacher of this {@link TeacherAuthorization}
     * 
     * @param authorization the teacher information
     * @param bean information to create the professorship
     * @return {@link #showCreate(Model, TeacherAuthorization, CreateProfessorshipBean)} if there is an error, otherwise redirects
     *         to {@link #home(Model, SearchBean, TeacherAuthorization)}
     */
    @RequestMapping(method = POST, value = "{authorization}")
    public String create(Model model, @PathVariable TeacherAuthorization authorization,
            @ModelAttribute CreateProfessorshipBean bean, RedirectAttributes attrs) {
        try {
            professorshipService.create(bean);
            attrs.addAttribute("authorization", authorization.getExternalId());
            return "redirect:/teacher/professorships/" + authorization.getExternalId();
        } catch (DomainException e) {
            model.addAttribute("error", e.getLocalizedMessage());
            return showCreate(model, authorization, bean);
        }
    }

    /***
     * Sets professorship as responsible
     * 
     * @param professorship the professorship to change
     * @param responsibleFor the new value of the responsibleFor
     * @return true if professorship is responsibleFor, false otherwise.
     */
    @RequestMapping(method = PUT, value = "{professorship}/{responsibleFor}")
    @ResponseBody
    public Boolean changeResponsibleFor(@PathVariable Professorship professorship, @PathVariable Boolean responsibleFor) {
        return professorshipService.changeResponsibleFor(professorship, responsibleFor);
    }

    @RequestMapping(method = DELETE, value = "{professorship}")
    @ResponseBody
    public ResponseEntity<String> deleteProfessorship(@PathVariable Professorship professorship) {
        try {
            professorshipService.deleteProfessorship(professorship);
            return new ResponseEntity<String>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.PRECONDITION_FAILED);
        }
    }

}

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
package org.fenixedu.academic.domain.phd.individualProcess.activities;

import org.fenixedu.academic.domain.caseHandling.PreConditionNotValidException;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.phd.PhdIndividualProgramProcess;
import org.fenixedu.academic.domain.phd.PhdIndividualProgramProcessBean;
import org.fenixedu.academic.domain.phd.PhdIndividualProgramProcessState;
import org.fenixedu.academic.domain.phd.PhdProgramProcessState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.bennu.core.domain.User;
import org.joda.time.DateTime;

public class ActivatePhdProgramProcessInWorkDevelopmentState extends PhdIndividualProgramProcessActivity {
    @Override
    protected void processPreConditions(PhdIndividualProgramProcess process, User userView) {
        // remove restrictions
    }

    @Override
    protected void activityPreConditions(PhdIndividualProgramProcess process, User userView) {
        if (!process.isAllowedToManageProcessState(userView)) {
            throw new PreConditionNotValidException();
        }

    }

    @Override
    protected PhdIndividualProgramProcess executeActivity(PhdIndividualProgramProcess process, User userView, Object object) {

        PhdIndividualProgramProcessBean bean = (PhdIndividualProgramProcessBean) object;
        DateTime stateDate = bean.getStateDate().toDateTimeAtStartOfDay();

        PhdProgramProcessState.createWithGivenStateDate(process, PhdIndividualProgramProcessState.WORK_DEVELOPMENT,
                userView.getPerson(), "", stateDate);

        /*
         * If it is associated to a registration we check that is not active and
         * try to reactivate it setting the last active state of the
         * registration
         */

        if (process.getRegistration() == null) {
            return process;
        }

        /*
         * The registration is concluded so we skip
         */
        if (process.getRegistration().isConcluded() || process.getRegistration().isSchoolPartConcluded()) {
            return process;
        }

        if (process.getRegistration().isActive()) {
            throw new DomainException("error.PhdIndividualProgramProcess.set.work.development.state.registration.is.active");
        }

        RegistrationState registrationLastActiveState = process.getRegistration().getLastActiveState();

        if (!registrationLastActiveState.isActive()) {
            throw new DomainException(
                    "error.PhdIndividualProgramProcess.set.work.development.state.registration.last.state.is.not.active");
        }

        RegistrationState.createRegistrationState(process.getRegistration(), userView.getPerson(), stateDate,
                registrationLastActiveState.getStateType());

        return process;
    }
}
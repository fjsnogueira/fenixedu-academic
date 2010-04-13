package net.sourceforge.fenixedu.domain.accounting.postingRules.candidacy;

import net.sourceforge.fenixedu.domain.accounting.EntryType;
import net.sourceforge.fenixedu.domain.accounting.Event;
import net.sourceforge.fenixedu.domain.accounting.EventType;
import net.sourceforge.fenixedu.domain.accounting.PaymentCodeType;
import net.sourceforge.fenixedu.domain.accounting.ServiceAgreementTemplate;
import net.sourceforge.fenixedu.util.Money;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.security.accessControl.Checked;

public class Over23IndividualCandidacyPR extends Over23IndividualCandidacyPR_Base {
    
    public  Over23IndividualCandidacyPR() {
        super();
    }

    public Over23IndividualCandidacyPR(final DateTime startDate, final DateTime endDate,
	    final ServiceAgreementTemplate serviceAgreementTemplate, final Money fixedAmount) {
	this();
	init(EntryType.OVER23_INDIVIDUAL_CANDIDACY_FEE, EventType.OVER23_INDIVIDUAL_CANDIDACY, startDate, endDate,
		serviceAgreementTemplate, fixedAmount);
    }

    @Checked("PostingRulePredicates.editPredicate")
    public Over23IndividualCandidacyPR edit(final Money fixedAmount) {
	deactivate();
	return new Over23IndividualCandidacyPR(new DateTime().minus(1000), null, getServiceAgreementTemplate(), fixedAmount);
    }

    @Override
    public PaymentCodeType calculatePaymentCodeTypeFromEvent(Event event, DateTime when, boolean applyDiscount) {
	return PaymentCodeType.OVER_23_INDIVIDUAL_CANDIDACY_PROCESS;
    }

}

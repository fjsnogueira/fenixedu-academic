package net.sourceforge.fenixedu.domain.accounting.postingRules;

import net.sourceforge.fenixedu.dataTransferObject.accounting.AccountingTransactionDetailDTO;
import net.sourceforge.fenixedu.domain.User;
import net.sourceforge.fenixedu.domain.accounting.Account;
import net.sourceforge.fenixedu.domain.accounting.AccountingTransaction;
import net.sourceforge.fenixedu.domain.accounting.EntryType;
import net.sourceforge.fenixedu.domain.accounting.Event;
import net.sourceforge.fenixedu.domain.accounting.EventType;
import net.sourceforge.fenixedu.domain.accounting.ServiceAgreementTemplate;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import pt.ist.fenixWebFramework.security.accessControl.Checked;
import net.sourceforge.fenixedu.util.Money;

import org.joda.time.DateTime;

public abstract class GenericSingleEntryTypePR extends GenericSingleEntryTypePR_Base {

    protected GenericSingleEntryTypePR() {
	super();
    }

    protected void init(EntryType entryType, EventType eventType, DateTime startDate, DateTime endDate,
	    ServiceAgreementTemplate serviceAgreementTemplate) {
	super.init(eventType, startDate, endDate, serviceAgreementTemplate);
	checkParameters(entryType);
	super.setEntryType(entryType);
    }

    private void checkParameters(EntryType entryType) {
	if (entryType == null) {
	    throw new DomainException("error.accounting.postingRules.GenericSingleEntryTypePR.entryType.cannot.be.null");
	}
    }

    @Override
    public void setEntryType(EntryType entryType) {
	throw new DomainException("error.accounting.postingRules.GenericSingleEntryTypePR.cannot.modify.entryType");
    }

    @Override
    public void internalAddOtherPartyAmount(User responsibleUser, Event event, Account fromAcount, Account toAccount,
	    Money amount, AccountingTransactionDetailDTO transactionDetailDTO) {
	makeAccountingTransaction(responsibleUser, event, fromAcount, toAccount, getEntryType(), amount, transactionDetailDTO);
    }

    @Override
    @Checked("RolePredicates.MANAGER_PREDICATE")
    public AccountingTransaction depositAmount(User responsibleUser, Event event, Account fromAcount, Account toAccount,
	    Money amount, AccountingTransactionDetailDTO transactionDetailDTO) {

	checkEntryTypeForDeposit(event, getEntryType());

	return makeAccountingTransaction(responsibleUser, event, fromAcount, toAccount, getEntryType(), amount,
		transactionDetailDTO);
    }

}

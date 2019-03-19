/*
 * This is the Axelor-gst Application.
 */
package com.axelor.gst.service;


import java.util.List;

import com.axelor.gst.db.Invoice;
import com.axelor.gst.db.InvoiceLine;
import com.axelor.gst.db.State;

public interface InvoiceService {
	
	public InvoiceLine calculate(InvoiceLine invoiceLine, State companyState, State InvoiceState);
	public Invoice calculateInvoice(Invoice invoice);
	public String changeStatus(String currentStatus);
	public InvoiceLine setInvoiceLineValues(InvoiceLine lines);
	public List<InvoiceLine> onChangeInvoiceAddress(boolean b,Invoice invoice);
}

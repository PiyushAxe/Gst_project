/*
 * This is the Axelor-gst Application.
 */
package com.axelor.gst.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.axelor.common.ObjectUtils;
import com.axelor.gst.db.Invoice;
import com.axelor.gst.db.InvoiceLine;
import com.axelor.gst.db.Product;
import com.axelor.gst.db.State;
import com.axelor.gst.db.repo.InvoiceLineRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class InvoiceServiceImpl implements InvoiceService {
	
	@Inject
	InvoiceLineRepository invoiceLineRepository;
	
	public InvoiceLine calculate(InvoiceLine invoiceLine, State companyState, State invoiceState) {
		
			BigDecimal netAmount = BigDecimal.ZERO;
			BigDecimal netIgst = BigDecimal.ZERO;
			BigDecimal netCgst = BigDecimal.ZERO;
			BigDecimal netSgst = BigDecimal.ZERO;
			BigDecimal grossAmount = BigDecimal.ZERO;
		
			BigDecimal gstRate = invoiceLine.getProduct().getGstRate();
		
			netAmount = invoiceLine.getPrice().multiply(new BigDecimal(invoiceLine.getQty()));
		
		
			if(!(invoiceState.equals(companyState))) {
					
					System.err.println(invoiceState);
					System.err.println(companyState);
					
					netIgst = netAmount.multiply(gstRate.divide(new BigDecimal("100")));
					invoiceLine.setIgst(netIgst);
					grossAmount = netAmount.add(netIgst);
					invoiceLine.setGrossAmount(grossAmount);
					invoiceLine.setNetAmount(netAmount);
					
					
			}else if((invoiceState.equals(companyState))) {
				
					netCgst = netAmount.multiply(gstRate.divide(new BigDecimal("200")));
					invoiceLine.setCgst(netCgst);
					netSgst = netAmount.multiply(gstRate.divide(new BigDecimal("200")));
					invoiceLine.setSgst(netSgst);
					grossAmount = netAmount.add(netCgst).add(netSgst);
					invoiceLine.setGrossAmount(grossAmount);
					invoiceLine.setNetAmount(netAmount);
			}
				return invoiceLine;
		
		
	}

	
	public Invoice calculateInvoice(Invoice invoice) {
		
			BigDecimal finalAmount = BigDecimal.ZERO;
			BigDecimal finalIgst = BigDecimal.ZERO;
			BigDecimal finalCgst = BigDecimal.ZERO;
			BigDecimal finalSgst = BigDecimal.ZERO;
			BigDecimal finalGrossAmount = BigDecimal.ZERO;
		
		if(ObjectUtils.isEmpty(invoice.getInvoiceItems())){
			return invoice;
			
		}
		if(!ObjectUtils.isEmpty(invoice.getInvoiceItems())) {
				for(InvoiceLine invoiceLine : invoice.getInvoiceItems()) {
					
					finalAmount = finalAmount.add(invoiceLine.getNetAmount());
					finalIgst = finalIgst.add(invoiceLine.getIgst());
					finalCgst = finalCgst.add(invoiceLine.getCgst());
					finalSgst = finalSgst.add(invoiceLine.getSgst());
					finalGrossAmount = finalGrossAmount.add(invoiceLine.getGrossAmount());
					
				}
		}
		
			invoice.setNetAmount(finalAmount);
			invoice.setNetIGST(finalIgst);
			invoice.setNetCGST(finalCgst);
			invoice.setNetSGST(finalSgst);
			invoice.setGrossAmount(finalGrossAmount);
			
		return invoice;
	}


	@Override
	public String changeStatus(String currentStatus) {
		
			String validate = "validated";
			String paid = "paid";
		
			if(currentStatus.equals("draft")) {
				
				return validate;
				
			}else if(currentStatus.equals("validated")) {
				
				return paid;
				
			}
			return null;
			
	}


	@Override
	public InvoiceLine setInvoiceLineValues(InvoiceLine invoicelines) {
			Product product = invoicelines.getProduct();
			String defaultItem = product.getName() + "-" + product.getCode();
			invoicelines.setItem(defaultItem);
			
			if(product.getGstRate() !=null) {
				
				invoicelines.setGstRate(product.getGstRate());
			}
			if(product.getHsbn()!=null) {
				
				invoicelines.setHsbn(product.getHsbn());
			}
			if(product.getSalePrice()!=null) {
				
				invoicelines.setPrice(product.getSalePrice());
				
			}
			if(invoicelines.getQty() == 0) {
				
				invoicelines.setQty(0);
			}
			invoicelines.setNetAmount(invoicelines.getPrice());
		
			return invoicelines;
	}

	@Transactional
	public List<InvoiceLine> onChangeInvoiceAddress(boolean b,Invoice invoice){
		
			List<InvoiceLine> invoiceLineList = invoice.getInvoiceItems();
			List<InvoiceLine> invoiceLineList1 = new ArrayList<>();
			
			
			
			BigDecimal test = BigDecimal.ZERO;
			InvoiceLine invoiceLine = new InvoiceLine();
			
			if(b == true && invoiceLineList.get(0).getCgst().equals(test)) {
				
				for(InvoiceLine line : invoiceLineList) {
					
					line.setSgst(line.getIgst().divide(new BigDecimal("2")));
					line.setCgst(line.getIgst().divide(new BigDecimal("2")));
					line.setIgst(new BigDecimal("0"));
					
					invoiceLineList1.add(line);
				}
				
				
				
			}else if(b == false && invoiceLineList.get(0).getIgst().equals(test)) {
				
				for(InvoiceLine line : invoiceLineList) {
					
					invoiceLine.setIgst(line.getCgst().multiply(new BigDecimal("2")));
					invoiceLine.setCgst(new BigDecimal("0"));
					invoiceLine.setSgst(new BigDecimal("0"));
					

					
					line.setIgst(line.getCgst().multiply(new BigDecimal("2")));
					line.setCgst(new BigDecimal("0"));
					line.setSgst(new BigDecimal("0"));
					invoiceLineList1.add(line);
					
					
				}
			}
			System.err.println(invoiceLineList.get(0).getIgst());
				return invoiceLineList1;
		
	}
	

}

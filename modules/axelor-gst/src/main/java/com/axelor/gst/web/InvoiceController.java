/*
 * This is the Axelor-gst Application.
 */

package com.axelor.gst.web;

import java.util.ArrayList;
import java.util.List;


import com.axelor.gst.db.Address;
import com.axelor.gst.db.Invoice;
import com.axelor.gst.db.InvoiceLine;
import com.axelor.gst.db.Product;
import com.axelor.gst.db.Sequence;
import com.axelor.gst.db.State;
import com.axelor.gst.db.repo.ProductRepository;
import com.axelor.gst.db.repo.SequenceRepository;
import com.axelor.gst.service.InvoiceService;
import com.axelor.gst.service.InvoiceServiceImpl;
import com.axelor.gst.service.SequenceService;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaModel;
import com.axelor.meta.db.repo.MetaModelRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;



public class InvoiceController {

	
	@Inject
	InvoiceService invoiceService;
	
	@Inject
	SequenceService sequenceService;
	
	@Inject
	ProductRepository productRepository;
	

	
	
	public void changeStatus(ActionRequest request, ActionResponse response) {
		
			Invoice invoice = request.getContext().asType(Invoice.class);
			String currentStatus = invoice.getStatus();
			String nextStatus = invoiceService.changeStatus(currentStatus);
			response.setValue("status", nextStatus);
			
			if(nextStatus.equals("validated")) {
			
				response.setAttr("btnChangeStatus", "title", "Paid");
			}		
						
	}
	
	public void setReference(ActionRequest request, ActionResponse response) {
		
		Invoice invoice = request.getContext().asType(Invoice.class);
		
		if(invoice.getReference() == null) {
			
			MetaModel model = Beans.get(MetaModelRepository.class).all().filter("self.name = ?","Invoice").fetchOne();
			long modelId = model.getId();
			Sequence sequence = Beans.get(SequenceRepository.class).all().filter("self.model  = ?", modelId).fetchOne();
			
			if(sequence == null) {
				
				response.setError("No sequence found");
				
			}else{
				
				long sequenceId = sequence.getId();
				sequence = sequenceService.getReference(sequenceId);
				response.setValue("reference", sequence.getNextNumber());
				
			}
			
		}
		
		
	}
	
	
	public void defaultButtonTitle(ActionRequest request, ActionResponse response) {
		
		Invoice invoice = request.getContext().asType(Invoice.class);
		if(invoice.getStatus().equals("validated")) {
			response.setAttr("btnChangeStatus", "title", "Paid");
			
		}
		
	}
	
	
	public void setGst(ActionRequest request, ActionResponse response) {
			
			Invoice invoice = request.getContext().getParent().asType(Invoice.class);
			
			InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
			
			try {	
					State invoiceState = invoice.getInvoiceAddress().getState();
					State companyState = invoice.getCompany().getAddress().getState();
				
					if(invoiceState != null && companyState != null) {
						
						invoiceLine = invoiceService.calculate(invoiceLine, companyState, invoiceState);
						response.setValue("netAmount", invoiceLine.getNetAmount());
						response.setValue("igst", invoiceLine.getIgst());
						response.setValue("sgst", invoiceLine.getSgst());
						response.setValue("cgst", invoiceLine.getCgst());
						response.setValue("grossAmount", invoiceLine.getGrossAmount());
						
					}
				
				}catch(Exception e) {
				
					response.setError("Please Enter Company Address and Invoice Address");
			}
		
	}
	
	public void onChangeAddress(ActionRequest request, ActionResponse response) {
		
		Invoice invoice = request.getContext().asType(Invoice.class);
		InvoiceServiceImpl invoiceServiceImpl = new InvoiceServiceImpl();
		
		List<InvoiceLine> list = new ArrayList<>();
		
		try {
				State invoiceState = invoice.getInvoiceAddress().getState();
				State companyState = invoice.getCompany().getAddress().getState();
				
			
			if(invoiceState!= null && companyState != null) {
				
				list = invoiceServiceImpl.onChangeInvoiceAddress(invoiceState == companyState,invoice);
				invoice.setInvoiceItems(invoiceServiceImpl.onChangeInvoiceAddress(invoiceState == companyState,invoice)); 
				
			}
			
			invoice = invoiceService.calculateInvoice(invoice);
			response.setValue("item",invoice.getInvoiceItems());
			response.setValue("netAmount", invoice.getNetAmount());
			response.setValue("netIGST", invoice.getNetIGST());
			response.setValue("netCGST", invoice.getNetCGST());
			response.setValue("netSGST", invoice.getNetSGST());
			response.setValue("grossAmount", invoice.getGrossAmount());
			response.setValue("invoiceItems", list);
			
		}catch(Exception e) {
			System.err.println(e);
		}
		
	}
	
	
	public void calculateTotal(ActionRequest request, ActionResponse response) {
		
			Invoice invoice = request.getContext().asType(Invoice.class);
			invoice = invoiceService.calculateInvoice(invoice);
			response.setValue("item",invoice.getInvoiceItems());
			response.setValue("netAmount", invoice.getNetAmount());
			response.setValue("netIGST", invoice.getNetIGST());
			response.setValue("netCGST", invoice.getNetCGST());
			response.setValue("netSGST", invoice.getNetSGST());
			response.setValue("grossAmount", invoice.getGrossAmount());
			
			response.setAttr("useInvoiceAsShippingAddress", "hidden", true);
		
	}
	
	
	public void setShippingAddress(ActionRequest request, ActionResponse response) {
		
			Invoice invoice = request.getContext().asType(Invoice.class);
			
				
			if(invoice.getUseInvoiceAsShippingAddress() == true) {
				
				response.setValue("shippingAddress", invoice.getInvoiceAddress());
				
				
				
			}else if(invoice.getUseInvoiceAsShippingAddress() == Boolean.FALSE) {
				
				List<Address> listAddress = invoice.getParty().getAddress();
				
				for(Address address: listAddress) {
					
					if(address.getType().equals("shipping")) {
						 
						 response.setValue("shippingAddress", address);
						 break;
						
					}else if(address.getType().equals("default")) {
						
						response.setValue("shippingAddress", address);
						
					}
		
			}
							
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void setSelectedProducts(ActionRequest request, ActionResponse response) {
		
			Invoice invoice =request.getContext().asType(Invoice.class);
			
			List<Integer> productId1 = new ArrayList<>();
					productId1= (List<Integer>) request.getContext().get("_ids");
			
					System.err.println(productId1);
					
			if(productId1 == null) {
				
//				setReference(request, response);
				
				
			}else {

			System.err.println(productId1);
					
					
				List<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>();
				
				for(Integer productId : productId1) {
					Product product = productRepository.find(productId.longValue());
					
					InvoiceLine invoiceLine = new InvoiceLine();
					invoiceLine.setProduct(product);
					
					invoiceLine = invoiceService.setInvoiceLineValues(invoiceLine);
					
					invoiceLines.add(invoiceLine);
					
				response.setValue("invoiceItems", invoiceLines);
				invoice = invoiceService.calculateInvoice(invoice);
				response.setValue("netAmount", invoice.getNetAmount());
				response.setValue("netIGST", invoice.getNetIGST());
				response.setValue("netCGST", invoice.getNetCGST());
				response.setValue("netSGSt",invoice.getNetSGST());
				response.setValue("grossAmount", invoice.getGrossAmount());
				response.setNotify("Set the Address Details and Product Quantity.");
				
			}
		}
	}
	
	
}	


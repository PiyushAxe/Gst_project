package com.axelor.gst.web;


import java.util.List;

import com.axelor.gst.db.Product;
import com.axelor.gst.db.ProductCategory;
import com.axelor.gst.db.repo.ProductRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.collect.Lists;
import com.google.common.base.Functions;
import com.google.inject.Inject;

public class ProductController {

	@Inject
	public ProductRepository productRepository;
	
	public void checkProductId(ActionRequest request, ActionResponse response) {
		
		try {
				@SuppressWarnings("unchecked")
				List<Integer> productIds = (List<Integer>) request.getContext().get("_ids");	
					
				
				System.err.println(request.getContext().entrySet());
				
				if(productIds.size() > 1 ) {
					response.setError("Select one product Only");
					
				}
			
		}catch (Exception e) {
		
				response.setError("Select one Product");
		}
		
	}
	
	public void getGSTRate(ActionRequest request, ActionResponse response) {
			Product product = request.getContext().asType(Product.class);
			if(product.getCategory() != null) {
				ProductCategory category = product.getCategory();
				response.setValue("gstRate", category.getGstRate());
				
			}else if(product.getCategory() == null) {
				
					response.setValue("gstRate", 0);
				
			}
		
	}
	
	public void selectedProductPrint(ActionRequest request, ActionResponse response) {
		
		@SuppressWarnings("unchecked")
		List<Integer> productList = (List<Integer>) request.getContext().get("_ids");
		List<String> idList = Lists.transform(productList, Functions.toStringFunction());
		String productIds = String.join(",", idList);
		request.getContext().put("productIds", productIds);
	}

	
}

package com.axelor.gst.web;

import com.axelor.gst.db.Sequence;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class SequenceController {
	
		public void generateSequence(ActionRequest request, ActionResponse response) {
			
			Sequence sequence = request.getContext().asType(Sequence.class);
			String prefix = sequence.getPrefix();
			String suffix = sequence.getSuffix();
			int padding = (int)sequence.getPadding();
			String paddingString = "";
			
			for(int i=0; i<padding;i++) {
				
				paddingString += String.valueOf(0);
				
			}
			
			String nextNumber = prefix + paddingString;
			if(suffix !=null || suffix == "") {
				
					nextNumber = nextNumber + suffix;
			}
		
			response.setValue("nextNumber", nextNumber);
		} 

	
}

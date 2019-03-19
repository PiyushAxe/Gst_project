/*
 * This is the Axelor-gst Application.
 */
package com.axelor.gst.service;


import com.axelor.gst.db.Sequence;
import com.axelor.gst.db.repo.SequenceRepository;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class SequenceServiceImpl implements SequenceService {

	@Inject
	SequenceRepository sequenceRepository;
	
	@Transactional
	public Sequence getReference(long sequenceId) {
		
		Sequence sequence = sequenceRepository.find(sequenceId);
			
		if(sequence == null) {
			
			return null;
			
		}else{
			
			String prefix = sequence.getPrefix();
			int padding = sequence.getPadding();
			String suffix = sequence.getSuffix();
			
			int paddingValue = Integer.parseInt((sequence.getNextNumber().substring(prefix.length(), prefix.length()+padding)))+1;
			String newPadding = Integer.toString(paddingValue);
			
			while(newPadding.length()<padding) {
				
				newPadding = "0"+newPadding;
				
			}
			
			String nextNumber = prefix+newPadding+suffix; 
			System.err.println(nextNumber);
			sequence.setNextNumber(nextNumber);
			sequence = Beans.get(SequenceRepository.class).save(sequence);
			
			return sequence;
		}
	
	}
	
}

/*
 * This is the Axelor-gst Application.
 */

package com.axelor.gst.web;





import com.axelor.gst.db.Party;
import com.axelor.gst.db.Sequence;
import com.axelor.gst.db.repo.SequenceRepository;
import com.axelor.gst.service.SequenceService;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaModel;
import com.axelor.meta.db.repo.MetaModelRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;


public class PartyController {

	@Inject
	SequenceService sequenceService;
	
	@Transactional
	public void getPartyReference(ActionRequest request, ActionResponse response) {
			
					Party party =request.getContext().asType(Party.class);
					
					if(party.getReference() == null) {
						
						MetaModel model = Beans.get(MetaModelRepository.class).all().filter("self.name = ?","Party").fetchOne();
						
						
						long modelId = model.getId();
						
						Sequence sequence = Beans.get(SequenceRepository.class).all().filter("self.model = ?",modelId).fetchOne();
						
						if(sequence == null) {
							
								response.setError("No sequence");
									
								
						}else {
							
							long sequenceId = sequence.getId();
							sequence = sequenceService.getReference(sequenceId);
							response.setValue("reference", sequence.getNextNumber());
						}
						
					}						
	}

}
package com.mw.search;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.io.Serializable;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael Wall
 */
@Component(
	property = {
		"indexer.class.name=com.liferay.object.model.ObjectDefinition#33306"  // HARDCODED: Fruit Object Definition ID.
	},	
	service = ModelDocumentContributor.class
	)
	public class FruitObjectEntryDocumentContributor
		implements ModelDocumentContributor<ObjectEntry> {

	@Override
	public void contribute(Document document, ObjectEntry objectEntry) {		
		try {
			Map<String, Serializable> fruitObjectValues = objectEntryLocalService.getValues(objectEntry.getObjectEntryId());
			
			String descriptionMW = "";
			
			if (fruitObjectValues.containsKey("descriptionMW")) { // HARDCODED field name
				descriptionMW = (String)fruitObjectValues.get("descriptionMW"); // HARDCODED: Field name
			}

			if (!descriptionMW.equalsIgnoreCase("")) {
				String objectEntryContent = document.get("objectEntryContent");
				
				objectEntryContent += ", descriptionMW: " + descriptionMW;
				
				try {
					document.addKeyword("objectEntryContent", objectEntryContent);
					
					_log.info("Updated objectEntryContent field on " + objectEntry.getObjectEntryId());
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to update objectEntryContent on object entry " +
								objectEntry.getObjectEntryId(),
							exception);
					}
				}
			}
		} catch (PortalException e) {
			e.printStackTrace();
		}
	}
	
	@Reference(unbind = "-")
	private ObjectEntryLocalService objectEntryLocalService;
	
	private static Log _log = LogFactoryUtil.getLog(FruitObjectEntryDocumentContributor.class);
}
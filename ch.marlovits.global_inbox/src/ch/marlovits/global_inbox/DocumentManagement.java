/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and medelexis AG
 * All rights reserved.
 * $Id$
 *******************************************************************************/
package ch.marlovits.global_inbox;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.marlovits.global_inbox.DocHandle;
import ch.rgw.tools.RegexpFilter;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

public class DocumentManagement implements IDocumentManager {
	
	public String addDocument(IOpaqueDocument doc) throws ElexisException{
		DocHandle dh = new DocHandle(doc);
		return dh.getId();
	}
	
	public String[] getCategories(){
		return DocHandle.getMainCategoryNames().toArray(new String[0]);
	}
	
	@Override
	public InputStream getDocument(String id){
		DocHandle dh = DocHandle.load(id);
		byte[] cnt;
		try {
			cnt = dh.getContents();
			ByteArrayInputStream bais = new ByteArrayInputStream(cnt);
			return bais;
		} catch (ElexisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<IOpaqueDocument> listDocuments(final Patient pat, final String categoryMatch,
		final String titleMatch, final String keywordMatch, final TimeSpan dateMatch,
		final String contentsMatch) throws ElexisException{
		Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
		if (pat != null) {
			qbe.add("PatID", Query.EQUALS, pat.getId());
		}
		if (dateMatch != null) {
			String from = dateMatch.from.toString(TimeTool.DATE_COMPACT);
			String until = dateMatch.until.toString(TimeTool.DATE_COMPACT);
			qbe.add("Datum", Query.GREATER_OR_EQUAL, from);
			qbe.add("Datum", Query.LESS_OR_EQUAL, until);
		}
		if (titleMatch != null) {
			if (titleMatch.matches("/.+/")) {
				qbe.addPostQueryFilter(new RegexpFilter(titleMatch.substring(1,
					titleMatch.length() - 1)));
			} else {
				qbe.add("Titel", Query.EQUALS, titleMatch);
			}
		}
		if (keywordMatch != null) {
			if (keywordMatch.matches("/.+/")) {
				qbe.addPostQueryFilter(new RegexpFilter(keywordMatch.substring(1,
					keywordMatch.length() - 1)));
			} else {
				qbe.add("Keywords", Query.LIKE, "%" + keywordMatch + "%");
			}
		}
		
		if (categoryMatch != null) {
			if (categoryMatch.matches("/.+/")) {
				qbe.addPostQueryFilter(new RegexpFilter(categoryMatch.substring(1,
					categoryMatch.length() - 1)));
			} else {
				qbe.add("Cat", Query.EQUALS, categoryMatch);
			}
		}
		
		if (contentsMatch != null) {
			throw new ElexisException(getClass(), "ContentsMatch not supported",
				ElexisException.EE_NOT_SUPPORTED);
		}
		List<DocHandle> dox = qbe.execute();
		ArrayList<IOpaqueDocument> ret = new ArrayList<IOpaqueDocument>(dox.size());
		for (DocHandle doc : dox) {
			ret.add(doc);
		}
		return ret;
	}
	
	@Override
	public boolean removeDocument(String guid){
		DocHandle dh = DocHandle.load(guid);
		if (dh != null && dh.exists()) {
			return dh.delete();
		}
		return false;
	}
	
	@Override
	public boolean addCategorie(String categorie){
		DocHandle.addMainCategory(categorie);
		return true;
	}
	
}

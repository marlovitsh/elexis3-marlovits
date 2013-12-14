/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.marlovits.global_inbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.marlovits.global_inbox.Activator;

public class InboxLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	public InboxLabelProvider(){
		
	}
	
	@Override
	public Image getColumnImage(Object element, int columnIndex){
		return null;
	}
	
	// Messages.InboxView_category, Messages.InboxView_title, "Datum", "Patient", gebdat
	// Stamm-Pletscher Regina@#@10.05.1952@#@-Winkelbestimmung@#@28.04.2011@#@PatNr
	@Override
	public String getColumnText(Object element, int columnIndex){
		if (element instanceof File) {
			File file = (File) element;
			String fileName = file.getPath();
			String infoFileName = fileName + ".file_info";
			File infoFile = new File(infoFileName);
			if (infoFile.exists()) {
				String[] fileInfoData = new String[6];
				fileInfoData[5] = "";
				BufferedReader in = null;
				FileReader fr = null;
				try {
					fr = new FileReader(infoFileName);
					in = new BufferedReader(fr);
					String line;
					int ix = 0;
					while ((line = in.readLine()) != null) {
						if (ix >= 5) {
							fileInfoData[ix] = fileInfoData[ix] + "\n" + line;
						} else {
							fileInfoData[ix] = line;
							ix++;
						}
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						in.close();
					} catch (IOException e) {}
					try {
						fr.close();
					} catch (IOException e) {}
				}
				
				switch (columnIndex) {
				case (0): // category
					return Activator.getDefault().getCategory(file);
				case (1): // doc title
					return fileInfoData[5].substring(0,
						fileInfoData[5].length() > 255 ? 255 : fileInfoData[5].length()).replace(
						"[\\n\\r]", " ");
				case (2): // datum
					return fileInfoData[4];
				case (3): // patient
					String patNr = fileInfoData[3];
					String realPatNr = "";
					if ((patNr.length() < 7) && (!fileInfoData[3].isEmpty()))
						realPatNr = " (" + fileInfoData[3] + ")";
					return fileInfoData[1] + realPatNr;
				case (4): // birth date
					return fileInfoData[2];
				}
			} else {
				switch (columnIndex) {
				case (0):
					return Activator.getDefault().getCategory(file);
				case (1):
					return file.getName();
				default:
					return "";
				}
			}
		}
		return "?"; //$NON-NLS-1$
	}
	
}

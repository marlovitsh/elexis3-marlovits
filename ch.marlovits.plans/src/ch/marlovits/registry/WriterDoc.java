package ch.marlovits.registry;

/**
 * File: WriterDoc.java
 *
 * Initial version: Aug 28, 2007
 *
 * Modifications:
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ooo.connector.BootstrapConnector;
import ooo.connector.BootstrapSocketConnector;
import ooo.connector.server.OOoServer;

import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowListener;
import com.sun.star.beans.Property;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XController;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XEventListener;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.style.XStyleFamiliesSupplier;
import com.sun.star.text.XBookmarksSupplier;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextFieldsSupplier;
import com.sun.star.text.XTextRange;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.xml.dom.XDocument;

/**
 * An example of creating an OpenOffice Writer document. This class demonstrates techniques to do
 * the following:
 * <ul>
 * <li>Create an OpenOffice Writer document and save it in PDF or an MS Word-compatible format.
 * <li>Optionally use a template document from which the new document will be initially copied.
 * <li>Insert text, charts, and images into template-defined bookmarked text fields, or into the
 * main document body.
 * <li>Control whether the user can edit the displayed window.
 * <li>Control document window visibility.
 * <li>Add document window visibility and dispose listeners.
 * </ul>
 * <p>
 * <b>Implementation Suggestions:</b>
 * <ul>
 * <li>The lengthy code sequences in this demo class should be logically abstracted into additional
 * methods and classes.
 * <li>Methods should be included to let callers add window listeners (examples of adding listeners
 * are within the <code>createDoc</code> method).
 * <li>The enumeration classes <code>DocumentFormatEnum</code> and
 * <code>TextInsertionModeEnum</code> should be removed into their own files.
 * </ul>
 * <p>
 * 
 * @author rdg
 */
public class WriterDoc {
	
	private static XComponentContext context;
	
	/** Constructor. */
	//public WriterDoc(){}
	
	/**
	 * Create an OpenOffice Writer document and make it visible on the screen.
	 * <p>
	 * 
	 * @return The 'handle' to the document.
	 * @param docTitle
	 *            The title of the document. This will show up in the title bar of the document
	 *            window.
	 * @param templateFile
	 *            The absolute path of a template file. If not null, the newly created file will
	 *            initially be an exact duplicate of this file. Typically, the template file
	 *            contains bookmarks where this application can insert text, images, charts, etc.
	 *            (The template can be created using OpenOffice Writer). If this parameter is null,
	 *            the new document will be blank.
	 * @param isUserEditable
	 *            True = allow user to manipulate/edit the Writer document window. False = make the
	 *            Writer document window uneditable.
	 */
	public static XComponent createDoc(String docTitle, String templateFile, boolean isUserEditable)
		throws java.lang.Exception{
		
		XComponent document = null;
		
		// //////////////////////////////////////////////////////////////////
		// Create the document.
		// //////////////////////////////////////////////////////////////////
		
		System.out.println("WriterDoc: Start up or connect to the remote service manager.");
		
		// Get remote service manager. We only need one instance regardless
		// of the number of documents we create.
		String oooExeFolder = "C:/Program Files (x86)/OpenOffice.org 3/program/";
		oooExeFolder = "F:/Program Files/OpenOffice.org 3/program";
		context = BootstrapSocketConnector.bootstrap(oooExeFolder);
		List oooOptions = OOoServer.getDefaultOOoOptions();
		oooOptions.add("-headless");
		oooOptions.add("-invisible");
		OOoServer oooServer = new OOoServer(oooExeFolder, oooOptions);

		// Connect to OOo
	//	BootstrapConnector bootstrapSocketConnector = new BootstrapConnector(oooServer);

		//wait until office is started
		//	XComponentContext xComponentContext = bootstrapSocketConnector.bootstrap(oooExeFolder, "uno:socket,host=localhost,port=8100;urp;StarOffice.ComponentContext", "-accept=socket,host=localhost,port=8100;urp;");
// xRemoteContext = bootstrapSocketConnector.connect(oooExeFolder, oooExeFolder);
// break;
// }
// catch (BootstrapException e) {
// System.out.println("Error Connecting to OO server");
// }
//
// System.out.println("Connected to a running office ...");
//
// XComponent xInputSpreadsheetComponent=null;
// XComponent xTemplateSpreadsheetComponent=null;
// XComponent xOutputSpreadsheetComponent=null;
// XComponent xDummySpreadsheetComponent=null;
//
// try{
// xRemoteServiceManager = xRemoteContext.getServiceManager();
// String available = (xRemoteServiceManager != null ? "available" : "not available");
//
// //Get the Desktop, we need its XComponentLoader interface to load a new document
// Object desktop = xRemoteServiceManager.createInstanceWithContext("com.sun.star.frame.Desktop",
// xRemoteContext);
//
// //Query the XComponentLoader interface from the desktop
// xComponentLoader = (XComponentLoader)UnoRuntime.queryInterface(XComponentLoader.class, desktop);
//
// //create empty array of PropertyValue structs, needed for loadComponentFromURL
// PropertyValue[] loadProps = new PropertyValue[0];
//
// //load input file
// xInputSpreadsheetComponent = OpenIOFile(FILE+inputFileName,password);
// //Checking if the file has actually opened or not
// selectSheetByIndex(xInputSpreadsheetComponent, 0);
//
// File file = new File(outputFileName);
// if(file.exists()){
// file.delete();
// }
// //create empty file and save it to output file to prevent open from failing
// xDummySpreadsheetComponent = xComponentLoader.loadComponentFromURL("private:factory/scalc",
// "_blank", 0, loadProps);
// storeDocComponent(xDummySpreadsheetComponent,FILE+outputFileName,MS97);
// xDummySpreadsheetComponent.dispose();
//
//
//
// //load template file
// xTemplateSpreadsheetComponent = xComponentLoader.loadComponentFromURL(FILE+templateFileName,
// "_blank", 0, loadProps);
//
// CopySheetContentsSpecial (xInputSpreadsheetComponent, 0, xTemplateSpreadsheetComponent, 0);
//
//
//
// //load output file
// xOutputSpreadsheetComponent = OpenIOFile(FILE+outputFileName,"");
// CopySheetContentsSpecial (xTemplateSpreadsheetComponent, 1, xOutputSpreadsheetComponent, 0);
// storeDocComponent(xOutputSpreadsheetComponent,FILE+outputFileName,CSV);
//
// //Close All files
// xInputSpreadsheetComponent.dispose();
// xTemplateSpreadsheetComponent.dispose();
// xOutputSpreadsheetComponent.dispose();
//
//
		
		/////////////////////////////
		
		if (context == null) {
			try {
				context = Bootstrap.bootstrap();
			} catch (BootstrapException e) {
				throw new java.lang.Exception(e);
			}
		}
		XMultiComponentFactory serviceManager = context.getServiceManager();
		
		// Retrieve the Desktop object and get its XComponentLoader.
		Object desktop =
			serviceManager.createInstanceWithContext("com.sun.star.frame.Desktop", context);
		XComponentLoader loader =
			(XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class, desktop);
		
		// Define general document properties (see
		// com.sun.star.document.MediaDescriptor for the possibilities).
		ArrayList<PropertyValue> props = new ArrayList<PropertyValue>();
		PropertyValue p = null;
		if (templateFile != null) {
			// Enable the use of a template document.
			p = new PropertyValue();
			p.Name = "AsTemplate";
			p.Value = new Boolean(true);
			props.add(p);
		}
		if ((docTitle != null) && (docTitle.length() > 0)) {
			p = new PropertyValue();
			p.Name = "DocumentTitle";
			p.Value = docTitle;
			props.add(p);
		}
		// Make the document initially invisible so the user does not
		// have to watch it being built.
		p = new PropertyValue();
		p.Name = "Hidden";
		p.Value = new Boolean(true);
		props.add(p);
		
		PropertyValue[] properties = new PropertyValue[props.size()];
		props.toArray(properties);
		
		// Create the document
		// (see com.sun.star.frame.XComponentLoader for argument details).
		
		document = null;
		
		if (templateFile != null) {
			// Create a new document that is a duplicate of the template.
			
			System.out.println("WriterDoc: Create the document '" + docTitle
				+ "' based on template " + templateFile + ".");
			
			String templateFileURL = filePathToURL(templateFile);
			document = loader.loadComponentFromURL(templateFileURL, // URL of templateFile.
				"_blank", // Target frame name (_blank creates new frame).
				0, // Search flags.
				properties); // Document attributes.
		} else {
			// Create an empty document.
			
			System.out.println("WriterDoc: Create the empty document '" + docTitle + "'.");
			
			String docType = "swriter";
			String newDocURL = "private:factory/" + docType;
			document = loader.loadComponentFromURL(newDocURL, // URL for creating a new document.
				"_blank", // Target frame name (_blank creates a new frame).
				0, // Search flags.
				properties); // Document properties.
		}
		
		// Get the document window and frame.
		
		XModel model = (XModel) UnoRuntime.queryInterface(XModel.class, document);
		XController c = model.getCurrentController();
		XFrame frame = c.getFrame();
		XWindow window = frame.getContainerWindow();
		
		// //////////////////////////////////////////////////////////////////
		// Add document window listeners.
		// //////////////////////////////////////////////////////////////////
		
		System.out.println("WriterDoc: Add window listeners.");
		
		// Example of adding a document displose listener so the application
		// can know if the user manually exits the Writer window.
		
		document.addEventListener(new XEventListener() {
			public void disposing(EventObject e){
				System.out.println("WriterDoc (Event Listener): The document window is closing.");
			}
		});
		
		// Example of adding a window listener so the application can know
		// when the document becomes initially visible (in the case of this
		// implementation, we will manually set it visible below after we
		// finish building it).
		
		window.addWindowListener(new XWindowListener() {
			public void windowShown(com.sun.star.lang.EventObject e){
				System.out
					.println("WriterDoc (Window listener): The document window has become visible.");
			}
			
			public void windowHidden(com.sun.star.lang.EventObject e){}
			
			public void disposing(com.sun.star.lang.EventObject e){}
			
			public void windowResized(com.sun.star.awt.WindowEvent e){}
			
			public void windowMoved(com.sun.star.awt.WindowEvent e){}
		});
		
		// //////////////////////////////////////////////////////////////////
		// Control whether user can edit the document.
		// //////////////////////////////////////////////////////////////////
		
		if (!isUserEditable) {
			window.setEnable(isUserEditable);
		}
		
		// //////////////////////////////////////////////////////////////////
		// Get the document's fields and bookmarks if any.
		// //////////////////////////////////////////////////////////////////
		
		System.out.println("WriterDoc: Get document fields/bookmarks if any.");
		
		// Fetch field and style information.
		// NOTE: I have not found a use for these yet.
		
		XTextFieldsSupplier fieldSupplier =
			(XTextFieldsSupplier) UnoRuntime.queryInterface(XTextFieldsSupplier.class, document);
		XStyleFamiliesSupplier styleFamiliesSupplier =
			(XStyleFamiliesSupplier) UnoRuntime.queryInterface(XStyleFamiliesSupplier.class,
				document);
		
		// Get the document's bookmark name supplier.
		
		XBookmarksSupplier bookmarksSupplier =
			(XBookmarksSupplier) UnoRuntime.queryInterface(XBookmarksSupplier.class, document);
		
		// //////////////////////////////////////////////////////////////////
		// Example of inserting text into a field bookmarked in the template
		// //////////////////////////////////////////////////////////////////
		
		XTextRange textRange = null;
		
		// XText is the main interface to a distinct text unit, for
		// example, the main text of a document, the text for headers
		// and footers, or a single cell of a table. It's
		// insertTextContent method allows insertion of a text table,
		// text frame, or text field.
		XText textContent = null;
		
		// XTextCursor provides methods to move the insertion/extraction
		// cursor within an XText text unit. In this case, we're
		// defaulting the cursor to the beginning of the text unit.
		XTextCursor textCursor = null;
		
		// The name of the bookmark to insert the text into.
		String bookmarkName = "Bookmark1";
		
		if (templateFile != null) {
			System.out.println("WriterDoc: Insert text into the bookmarked text field named '"
				+ bookmarkName + "'.");
			// Get the XTextRange associated with the specified bookmark.
			textRange = getBookmarkTextRange(bookmarksSupplier, bookmarkName);
			textContent = textRange.getText();
			// Create a text cursor then navigate to the text range.
			textCursor = textContent.createTextCursor();
			textCursor.gotoRange(textRange, false);
		} else {
			System.out.println("WriterDoc: Insert text into the document body.");
			// Get the XTextRange associated with the main document body.
			XTextDocument textDocument =
				(XTextDocument) UnoRuntime.queryInterface(XTextDocument.class, model);
			textContent = textDocument.getText();
			textCursor = textContent.createTextCursor();
		}
		
		// How we will insert the text.
		TextInsertionModeEnum textInsertMode = TextInsertionModeEnum.REPLACE_ALL;
		
		// The text we will insert.
		String textToInsert = "Inserted " + bookmarkName + " text";
		if (textInsertMode == TextInsertionModeEnum.ADD_SENTENCE) {
			textToInsert = "  " + textToInsert;
		} else if (textInsertMode == TextInsertionModeEnum.ADD_PARAGRAPH) {
			textToInsert = "\r\n" + textToInsert;
		}
		
		// Insert the given text at the bookmark position according to
		// the desired insertion mode.
		if (textInsertMode == TextInsertionModeEnum.REPLACE_ALL) {
			textCursor.setString(textToInsert);
		} else {
			String existingText = textCursor.getString();
			if (existingText.length() > 0) {
				textCursor.goRight((short) 0, false);
			}
			textCursor.setString(textToInsert);
		}
		
		// Move to the end of the text we just inserted.
		textCursor.goRight((short) textToInsert.length(), false);
		
		// NOTE: If the inserted text needs to have a particular font
		// and/or color, then do the following in the above code:
		//
		// 1. Before calling setString, save the XTextCursor's property set.
		// 2. Modify the XTextCursor's property set as desired. Following
		// are the names of properties typically modified for color and
		// font: CharColor, CharFontName, CharFontFamily, CharFontPitch,
		// CharFontPosture, CharFontWeight, CharHeight.
		// 3. After calling setString and moving the cursor to the end of
		// the inserted text, restore the XTextCursor's original properties.
		
		// //////////////////////////////////////////////////////////////////
		// Example of inserting an image from a file into the document.
		// //////////////////////////////////////////////////////////////////
		
		String imageFile =
			"C:/_Daten/Eigene Dateien/Praxis etc/Werbung/Briefmarken/BriefmarkenTest_1.jpg";
		String imageFileURL = filePathToURL(imageFile);
		ImageInsertionModeEnum imageInsertMode = null;
		bookmarkName = "Bookmark3";
		
		if (templateFile != null) {
			System.out.println("WriterDoc: Insert an image into the bookmarked text field named '"
				+ bookmarkName + "'.");
			imageInsertMode = ImageInsertionModeEnum.INSERT_IN_TEXT_FIELD;
			// Get the XTextRange associated with the specified bookmark.
			textRange = getBookmarkTextRange(bookmarksSupplier, bookmarkName);
			textContent = textRange.getText();
			// Create a text cursor then navigate to the text range.
			textCursor = textContent.createTextCursor();
			textCursor.gotoRange(textRange, false);
		} else {
			System.out.println("WriterDoc: Insert an image from a file into the document body.");
			imageInsertMode = ImageInsertionModeEnum.INSERT_IN_TEXT_BODY;
			// Get the XTextRange associated with the document body.
			XTextDocument textDocument =
				(XTextDocument) UnoRuntime.queryInterface(XTextDocument.class, model);
			textContent = textDocument.getText();
			textCursor = textContent.createTextCursor();
		}
		
		// Create an internal bit map.
		XMultiServiceFactory docServices =
			(XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, model);
		XNameContainer bitMap =
			(XNameContainer) UnoRuntime.queryInterface(XNameContainer.class,
				docServices.createInstance("com.sun.star.drawing.BitmapTable"));
		
		// Copy the image from the file into the document, then create an internal
		// URL that points to it.
		bitMap.insertByName("MyTempID", imageFileURL);
		String internalURL = (String) bitMap.getByName("MyTempID");
		
		// Create an object in which to store the image, and get its properties.
		XTextContent imageObject =
			(XTextContent) UnoRuntime.queryInterface(XTextContent.class,
				docServices.createInstance("com.sun.star.text.TextGraphicObject"));
		XPropertySet pSet =
			(XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, imageObject);
		
		// Link the internal image to the TextGraphicObject and insert it
		// at the cursor position.
		pSet.setPropertyValue("AnchorType", com.sun.star.text.TextContentAnchorType.AS_CHARACTER);
		pSet.setPropertyValue("GraphicURL", internalURL);
		// Note: If Width and Height are not given, the image's normal size
		// will be used. Width and Height units are 100ths of a millimeter.
		pSet.setPropertyValue("Width", new Integer(2600));
		pSet.setPropertyValue("Height", new Integer(2000));
		
		if ((imageInsertMode == ImageInsertionModeEnum.INSERT_IN_TEXT_FIELD)
			|| (imageInsertMode == ImageInsertionModeEnum.INSERT_IN_TEXT_BODY)) {
			textContent.insertTextContent(textCursor, imageObject, false);
		} else {
			textContent.insertTextContent(textCursor, imageObject, true);
		}
		
		// Remove the temp ID which we don't need any more.
		bitMap.removeByName("MyTempID");
		
		// //////////////////////////////////////////////////////////////////
		// Make the document visible.
		// //////////////////////////////////////////////////////////////////
		
		System.out.println("WriterDoc: Document construction finished - make it visible.");
		
		window.setVisible(true);
		
		return document;
	}
	
	/**
	 * Save a given document to a specified file.
	 * <p>
	 * 
	 * @param document
	 *            The 'handle' to the document.
	 * @param saveFile
	 *            The full path of the file in which to save the document. If null, this method will
	 *            do nothing.
	 * @param format
	 *            The format in which to save the document.
	 * @param overwrite
	 *            If true, and the file already exists, it will be overwritten. Otherwise, if the
	 *            file already exists an exception will be thrown.
	 */
	public void saveDoc(XDocument document, String saveFile, DocumentFormatEnum format,
		boolean overwrite) throws java.lang.Exception{
		
		if ((saveFile == null) || (saveFile.trim().length() == 0)) {
			return;
		}
		if (!overwrite) {
			File f = new File(saveFile);
			if (f.exists()) {
				throw new java.lang.Exception("File " + saveFile
					+ " already exists; overwriting disabled.");
			}
		}
		
		String saveFileURL = filePathToURL(saveFile);
		XStorable storable = (XStorable) UnoRuntime.queryInterface(XStorable.class, document);
		
		PropertyValue[] properties = new PropertyValue[1];
		PropertyValue p = new PropertyValue();
		p.Name = "FilterName";
		p.Value = format.getFormatCode();
		properties[0] = p;
		
		storable.storeAsURL(saveFileURL, properties);
	}
	
	/** Get a bookmarked field's <code>TextRange</code>. */
	private static XTextRange getBookmarkTextRange(XBookmarksSupplier bookmarksSupplier,
		String bookmarkName) throws java.lang.Exception{
		
		XTextRange textRange = null;
		
		// Get the collection of bookmarks in the document.
		XNameAccess bookmarkNames = bookmarksSupplier.getBookmarks();
		
		// Find the bookmark having the given name.
		Object bmk = null;
		try {
			bmk = bookmarkNames.getByName(bookmarkName);
		} catch (NoSuchElementException e) {}
		if (bmk == null) {
			throw new java.lang.Exception("Cannot find bookmark '" + bookmarkName + "'");
		}
		
		// Get the bookmark's XTextContent. It allows objects to be
		// inserted into a text and provides their location within a
		// text after insertion.
		
		XTextContent bookmarkContent =
			(XTextContent) UnoRuntime.queryInterface(XTextContent.class, bmk);
		
		// Get the bookmark's XTextRange. It describes the object's
		// position within the text and provides access to the text.
		
		textRange = bookmarkContent.getAnchor();
		
		return textRange;
	}
	
	/** Convert a file path to URL format. */
	private static String filePathToURL(String file){
		File f = new File(file);
		StringBuffer sb = new StringBuffer("file:///");
		try {
			sb.append(f.getCanonicalPath().replace('\\', '/'));
		} catch (IOException e) {}
		return sb.toString();
	}
	
	/**
	 * Dump a given <code>XPropertySet</code>.
	 */
	private void showProperties(String title, XPropertySet pSet){
		System.out.println("\n" + title + "\n");
		XPropertySetInfo info = pSet.getPropertySetInfo();
		Property[] props = info.getProperties();
		if (props != null) {
			try {
				for (int i = 0; i < props.length; i++) {
					Property p = props[i];
					String value = "<null>";
					try {
						Object o = (Object) pSet.getPropertyValue(p.Name);
						if (o != null) {
							value = o.toString();
						}
					} catch (java.lang.Exception e) {
						value = "<null>";
					}
					System.out.println("   Name = " + p.Name + ", Type = " + p.Type + ", Value = "
						+ value);
				}
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Enumeration for document formats. */
	public enum DocumentFormatEnum {
		PDF("PDF", "writer_pdf_Export"), MSWORD("MS-Word", "MS Word 97");
		private String name;
		private String code;
		private static DocumentFormatEnum[] values = {
			PDF, MSWORD
		};
		
		private DocumentFormatEnum(String name, String code){
			this.name = name;
			this.code = code;
		}
		
		public String getName(){
			return name;
		}
		
		public String getFormatCode(){
			return code;
		}
		
		public static DocumentFormatEnum[] getValues(){
			return values;
		}
	}
	
	/** Enumeration for techniques of inserting text into the document. */
	public enum TextInsertionModeEnum {
			REPLACE_ALL("ReplaceAll", "Insert text into a specific text field, replacing contents"),
			ADD_TEXT("AddText", "Append text to the existing contents of a text field"),
			ADD_SENTENCE("AddSentence", "Append text into a specific text field as a new sentence"),
			ADD_PARAGRAPH("AddParagraph",
				"Append text into a specific text field as a new paragraph");
		private String name;
		private String desc;
		private static TextInsertionModeEnum[] values = {
			REPLACE_ALL, ADD_TEXT, ADD_SENTENCE, ADD_PARAGRAPH
		};
		
		private TextInsertionModeEnum(String name, String desc){
			this.name = name;
			this.desc = desc;
		}
		
		public String getName(){
			return name;
		}
		
		public String getDesc(){
			return desc;
		}
		
		public static TextInsertionModeEnum[] getValues(){
			return values;
		}
	}
	
	/** Enumeration for techniques of inserting the image into a document. */
	public enum ImageInsertionModeEnum {
		INSERT_IN_TEXT_FIELD("InsertInTextField", "Insert image into a specific text field"),
			INSERT_IN_TEXT_BODY("InsertInTextBody", "Insert image into the document body"),
			REPLACE_IN_TEXT_FIELD("ReplaceInTextField",
				"Insert image into a specific text field, replacing existing content"),
			REPLACE_IN_TEXT_BODY("ReplaceInTextBody",
				"Insert chart into the document body, replacing existing content");
		private String name;
		private String desc;
		private static ImageInsertionModeEnum[] values = {
			INSERT_IN_TEXT_FIELD, INSERT_IN_TEXT_BODY, REPLACE_IN_TEXT_FIELD, REPLACE_IN_TEXT_BODY
		};
		
		private ImageInsertionModeEnum(String name, String desc){
			this.name = name;
			this.desc = desc;
		}
		
		public String getName(){
			return name;
		}
		
		public String getDesc(){
			return desc;
		}
		
		public static ImageInsertionModeEnum[] getValues(){
			return values;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args){
		
		String templateDoc =
			"C:/Documents and Settings/Rob/My Documents/EclipseProjects/OpenOffice/MyTemplateDoc.odt";
		
		WriterDoc doc = new WriterDoc();
		try {
			doc.createDoc("My Document Title", null, true);
			// doc.createDoc("My Document Title", templateDoc, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
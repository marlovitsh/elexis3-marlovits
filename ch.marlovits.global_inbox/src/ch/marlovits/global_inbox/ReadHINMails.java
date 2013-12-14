package ch.marlovits.global_inbox;

import ch.elexis.data.PersistentObject;
import ch.marlovits.global_inbox.ReadHINMails;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.VersionInfo;

public class ReadHINMails extends PersistentObject {
	private static final String TABLENAME = "CH_MARLOVITS_READHINMAILS"; //$NON-NLS-1$
	private static final String createDB = "CREATE TABLE " + TABLENAME + "(" //$NON-NLS-1$ //$NON-NLS-2$
		+ "ID		VARCHAR(25) primary key," //$NON-NLS-1$
		+ "deleted	CHAR(1) default '0'," + "hash		VARCHAR(32)," //$NON-NLS-1$
		+ "docid		VARCHAR(25)," //$NON-NLS-1$
		+ "lastupdate BIGINT default '0'" //$NON-NLS-1$
		+ ");"; //$NON-NLS-1$
	
	static {
		addMapping(TABLENAME, "hash=hash", "docid=docid"); //$NON-NLS-1$ //$NON-NLS-2$
		ReadHINMails start = load("1"); //$NON-NLS-1$
		if (start.state() < PersistentObject.DELETED) {
			try {
				createOrModifyTable(createDB);
			} catch (Exception ex) {
				ExHandler.handle(ex);
			}
		} else {
			VersionInfo vi = new VersionInfo("1"); //$NON-NLS-1$
		}
	}
	
	public ReadHINMails(String hash, String docid){
		if (hash == null)
			return;
		if (hash.isEmpty())
			return;
		if (docid == null)
			return;
		if (docid.isEmpty())
			return;
		set(new String[] {
			"hash", "docid" //$NON-NLS-1$ //$NON-NLS-2$
		}, hash, docid);
	}
	
	public String getHash(){
		return get("hash"); //$NON-NLS-1$
	}
	
	public String getDocId(){
		return get("docid"); //$NON-NLS-1$
	}
	
	public void setHash(String hash){
		set("hash", hash); //$NON-NLS-1$
	}
	
	public void setDocId(String docid){
		set("docid", docid); //$NON-NLS-1$
	}
	
	@Override
	public String getLabel(){
		return get("hash"); //$NON-NLS-1$
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	protected ReadHINMails(final String id){
		super(id);
	}
	
	protected ReadHINMails(){}
	
	public static ReadHINMails load(final String id){
		return new ReadHINMails(id);
	}
}

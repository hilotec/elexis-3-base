package at.medevit.elexis.impfplan.model.po;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import at.medevit.elexis.impfplan.model.ArticleToImmunisationModel;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.data.Artikel;
import ch.elexis.data.Mandant;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.TimeTool;

public class Vaccination extends PersistentObject {
	
	public static final String TABLENAME = "AT_MEDEVIT_ELEXIS_IMPFPLAN";
	static final String VERSION = "1.0.0";
	
	//@formatter:off
	public static final String FLD_PATIENT_ID = "Patient_ID";
	public static final String FLD_ARTIKEL_REF = "Artikel_REF";
	/** Handelsname */	public static final String FLD_BUSS_NAME = "BusinessName";
	/** EAN Code */		public static final String FLD_EAN = "ean";
	/** ATC Code */		public static final String FLD_ATCCODE = "ATCCode";
	/** Chargen-No */	public static final String FLD_LOT_NO ="lotnr";	
	/** Verabr. Datum*/	public static final String FLD_DOA = "dateOfAdministration";
	/** Verabreicher, entweder ein Mandant im lokalen System, oder ein Kontakt-String */	
						public static final String FLD_ADMINISTRATOR = "administrator";
	/** Impfung gegen */public static final String FLD_VACC_AGAINST = "vaccAgainst";
	
	/** Definition of the database table */
	static final String createDB =
		"CREATE TABLE " + TABLENAME
			+ "("
			+ "ID VARCHAR(25) primary key," // has to be of type varchar else version.exists fails
			+ "lastupdate BIGINT,"
			+ "deleted CHAR(1) default '0'," // will never be set to 1
			+ FLD_PATIENT_ID +" VARCHAR(25),"	
			+ FLD_ARTIKEL_REF +" VARCHAR(255),"	
			+ FLD_BUSS_NAME +" VARCHAR(255),"
			+ FLD_EAN +" VARCHAR(13),"
			+ FLD_ATCCODE +" VARCHAR(20),"
			+ FLD_LOT_NO +" VARCHAR(255),"
			+ FLD_DOA +" CHAR(8),"
			+ FLD_ADMINISTRATOR +" VARCHAR(255),"
			+ FLD_VACC_AGAINST +" VARCHAR(255),"
			+ PersistentObject.FLD_EXTINFO + " BLOB"
			+ "); "

			+ "INSERT INTO " + TABLENAME + " (ID,"+FLD_PATIENT_ID+","+FLD_DOA+") VALUES ('"+StringConstants.VERSION_LITERAL+"',"
			+ JdbcLink.wrap(VERSION) +","+new TimeTool().toString(TimeTool.DATE_COMPACT)+");";
	//@formatter:on
	
	static {
		addMapping(TABLENAME, FLD_PATIENT_ID, FLD_ARTIKEL_REF, FLD_BUSS_NAME, FLD_EAN, FLD_ATCCODE,
			FLD_LOT_NO, FLD_DOA, FLD_ADMINISTRATOR, FLD_VACC_AGAINST, PersistentObject.FLD_EXTINFO);
	}
	
	Vaccination(){}
	
	protected Vaccination(String id){
		super(id);
	}
	
	public static Vaccination load(String id){
		return new Vaccination(id);
	}
	
	public Vaccination(final String patientId, final Artikel a, final Date doa, final String lotNo,
		final String mandantId){
		this(patientId, a.storeToString(), a.getLabel(), a.getEAN(), a.getATC_code(), doa, lotNo,
			mandantId);
	}
	
	public Vaccination(final String patientId, final String articleStoreToString,
		final String articleLabel, final String articleEAN, final String articleATCCode,
		final Date doa, final String lotNo, final String mandantId){
		
		create(null);
		
		TimeTool doaTt = new TimeTool(doa);
		String vaccAgainst =
			StringUtils.join(ArticleToImmunisationModel.getImmunisationForAtcCode(articleATCCode),
				",");
		
		String[] fields =
			new String[] {
				FLD_PATIENT_ID, FLD_ARTIKEL_REF, FLD_BUSS_NAME, FLD_EAN, FLD_ATCCODE, FLD_LOT_NO,
				FLD_DOA, FLD_ADMINISTRATOR, FLD_VACC_AGAINST
			};
		String[] vals =
			new String[] {
				patientId, articleStoreToString, articleLabel, articleEAN, articleATCCode, lotNo,
				doaTt.toString(TimeTool.DATE_COMPACT), mandantId, vaccAgainst
			};
		set(fields, vals);
	}
	
	static {
		addMapping(TABLENAME, FLD_PATIENT_ID, FLD_ARTIKEL_REF, FLD_BUSS_NAME, FLD_EAN, FLD_ATCCODE,
			FLD_LOT_NO, FLD_DOA, FLD_ADMINISTRATOR, FLD_VACC_AGAINST, PersistentObject.FLD_EXTINFO);
		Vaccination version = load(StringConstants.VERSION_LITERAL); //$NON-NLS-1$
		if (!version.exists()) {
			createOrModifyTable(createDB);
		} else {
			// update code
		}
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	@Override
	public String getLabel(){
		return getDateOfAdministration().toString(TimeTool.DATE_COMPACT) + " " + getBusinessName()
			+ " (" + getChargeNo() + ") - " + getAdministratorLabel();
	}
	
	public TimeTool getDateOfAdministration(){
		return new TimeTool(get(FLD_DOA));
	}
	
	public String getBusinessName(){
		return get(FLD_BUSS_NAME);
	}
	
	public String getChargeNo(){
		return get(FLD_LOT_NO);
	}
	
	public String getAtcCode(){
		return get(FLD_ATCCODE);
	}
	
	/**
	 * @return a human-readable label of the person that administered the vaccine
	 */
	public @NonNull
	String getAdministratorLabel(){
		String value = get(FLD_ADMINISTRATOR);
		if (value.startsWith(Mandant.class.getName())) {
			return new PersistentObjectFactory().createFromString(value).getLabel();
		} else {
			if (value == null || value.length() < 2)
				return "";
			
			return value;
		}
	}
	
}

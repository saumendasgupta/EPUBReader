
package com.sdg.EPUBparser;

import com.sdg.DisplayManager.DispMgrUtility;

import android.widget.Button;
import android.widget.TextView;

/*XHTML Parser*/
class XhtmlLinkInfo {
	String ui8href;/* href -- URL Specifies the location of linked document */
	String ui8Class; /* class -- Specifies a classname for an element */
	String ui8Id; /* id -- Specifies a unique id for an element */
	String ui8Style; /* style -- Specifies an inline style for an element */
	String ui8Title; /* title -- Specifies extra information about an element */
	String ui8CSSFilePath;/* Specifies the location of the document created */

	public XhtmlLinkInfo(String href, String Class, String id, String style,
			String title, String cssfilepath) {
		this.ui8href = href;
		this.ui8Class = Class;
		this.ui8Id = id;
		this.ui8Style = style;
		this.ui8Title = title;
		this.ui8CSSFilePath = cssfilepath;

	}

}

class XhtmlMetaInfo {
	String ui8Content; /* content -- Specifies content of the meta info */
	String ui8Scheme; /* scheme used to interpret val of content attr */

	public XhtmlMetaInfo(String content, String scheme) {
		this.ui8Content = content;
		this.ui8Scheme = scheme;

	}
}

class XhtmlFieldParma {
	int enContentype;
	String pui8Content;
	String pui8Href;
	int ui32ContentSize;
	public String pui8ContentPath;
	String ui8URL;
	String ui8version;
	String ui8encoding;
	String ui8xmlns;
	String ui8Title;
	XhtmlLinkInfo pstxhtmllink[];
	int ui32linkcount;
	XhtmlMetaInfo pstMetaInfo[];
	int ui8MetaCount;

	String ui8BookPath;
	
	/* For The CSS parser values holding count*/
	public int propstindex;
	

}



/* CSSparser Values*/

class CssTagPropertyInfo
{
	int enpropty;
	int value;
	int enunit;
	
	public CssTagPropertyInfo(int enpropty, int value,int enunit) {
		this.enpropty = enpropty;
		this.value  = value;
		this.enunit = enunit;
	}
	
	
}





/* NCX Parser Classes */
class NcxNavtargetInfo {
	String aui8NavTarClass; /* navTarget class */
	String aui8NavTarFilename; /* navTarget content FilePath */
	int ui32NavTarPlayorder; /* navTarget Playorder */
	String aui8NavLableText; /* NavList-navLabel Label */
	String aui8NavLableImage; /* NavList-navLabel Image Path */

}

class NcxTocInfo {
	String aui8ContentFilename; /* navMap-navPoint Content filePath */
	int ui32Playorder; /* navMap-navPoint Playorder */
	String aui8Text; /* navMap-navPoint label */
	String aui8ImagePath; /* navMap-navPoint Image Path */

}

/*
 * Hold level4 information of TOC
 */
class NcxTocLvl4 extends NcxTocInfo {
	public NcxTocLvl4(String contentfilename, int playorder, String text,
			String imagePath) {
		this.aui8ContentFilename = contentfilename;
		this.ui32Playorder = playorder;
		this.aui8Text = text;
		this.aui8ImagePath = imagePath;
	}

}

/*
 * Hold level3 information of TOC
 */
class NcxTocLvl3 extends NcxTocInfo {
	NcxTocLvl4 pstLevel4Nodes[];
	int ui32LVL4NodeCount; /* Level 4 Noce count */

	public NcxTocLvl3(String contentfilename, int playorder, String text,
			String imagePath, int l4count) {
		this.aui8ContentFilename = contentfilename;
		this.ui32Playorder = playorder;
		this.aui8Text = text;
		this.aui8ImagePath = imagePath;
		this.ui32LVL4NodeCount = l4count;
	}

}

/*
 * Hold level2 information of TOC
 */
class NcxTocLvl2 extends NcxTocInfo {
	NcxTocLvl3 pstLevel3Nodes[];
	int ui32LVL3NodeCount; /* Level 3 Noce count */

	public NcxTocLvl2(String contentfilename, int playorder, String text,
			String imagePath, int l3count) {
		this.aui8ContentFilename = contentfilename;
		this.ui32Playorder = playorder;
		this.aui8Text = text;
		this.aui8ImagePath = imagePath;
		this.ui32LVL3NodeCount = l3count;
	}

}

/*
 * Hold level1 information of TOC
 */
class NcxTocLvl1 extends NcxTocInfo {
	NcxTocLvl2 pstLevel2Nodes[];
	int ui32LVL2NodeCount; /* Level 2 Noce count */

	public NcxTocLvl1(String contentfilename, int playorder, String text,
			String imagePath, int l2count) {
		this.aui8ContentFilename = contentfilename;
		this.ui32Playorder = playorder;
		this.aui8Text = text;
		this.aui8ImagePath = imagePath;
		this.ui32LVL2NodeCount = l2count;
	}

}

class NcxParsedInfo {
	String aui8version; /* NCX Version */
	String aui8xmlns; /* NCX Name space */
	String aui8dir; /* NCX Direction */
	String aui8TitleName; /* docTilte Name */
	String aui8TitleImagePath; /* docTitle Image Path */
	String aui8AuthorName; /* docAuthor Name */
	String aui8AuthorImagePath; /* docAuthor Image Path */
	int ui32LVL1NodeCount; /* Level 1 Node count */
	NcxTocLvl1 pstLevel1Nodes[]; /* Array of object for lavel1 node class */
	String aui8NavLableText; /* NavList-navLabel Label */
	String aui8NavLableImage; /* NavList-navLabel Image Path */
	NcxNavtargetInfo pstnavtargetinfo[]; /*
										 * Array of object to navTarget-info
										 * class
										 */
	int ui32NavTargetCount; /* NavTarget count */
}

/* OPF Parser Classes */

class JniOPFMediaType {
	public static final int OPF_NO_MEDIA_TYPE = -1; /* upon no media-type info */

	public static final int OPF_IMAGE_GIF = 0; /* image/gif */

	public static final int OPF_IMAGE_JPEG = 1; /* image/jpeg */

	public static final int OPF_IMAGE_PNG = 2; /* image/png */

	public static final int OPF_IMAGE_SVG_XML = 3; /* image/svg+xml */

	public static final int OPF_APPLICATION_XHTML_XML = 4; /*
															 * application/xhtml+
															 * xml
															 */

	public static final int OPF_APPLICATION_X_DTBOOK_XML = 5; /*
															 * application/x-dtbook
															 * +xml
															 */

	public static final int OPF_TEXT_CSS = 6; /* text/css */

	public static final int OPF_TEXT_XML = 7; /* text/xml */

	public static final int OPF_APPLICATION_XML = 8; /* application/xml */

	public static final int OPF_TEXT_X_OEB1_DOCUMENT = 9; /*
														 * text/x-oeb1-document
														 */

	public static final int OPF_TEXT_X_OEB1_CSS = 10; /* text/x-oeb1-css */

	public static final int OPF_APPLICATION_X_DTBNCX_XML = 11; /*
																 * application/x-
																 * dtbncx +xml
																 */

	public static final int OPF_TOTAL_MEDIA_TYPE = 12; /*
														 * total no of media
														 * type strings
														 */
}

class OpfTitle {
	String ui8Title; /* Title for the Book */

}

class OpfCreator extends OpfTitle {
	String ui8Creator; /* Creator Name */

}

class TitlePage extends OpfCreator {
	long ui32TitleCount; /* No of tour site element present inside the tour */
	long ui32CreatorCount; /* No of tour site element present inside the tour */

}

class Metadata extends TitlePage {
	String ui8subject; /* A topic of the content of the resource */
	String ui8description; /* Description of the publication's content */
	String ui8publisher; /*
						 * An entity responsible for making the resource
						 * available
						 */
	String ui8contributor; /*
							 * A party whose contribution to the publication is
							 * secondary
							 */
	String ui8date; /*
					 * Date of publication, in the format defined by
					 * "Date and Time Formats"
					 */

	String ui8type; /* The nature or genre of the content of the resource. */

	String ui8format; /* The media type or dimensions of the resource */

	String ui8identifier; /*
						 * A string or number used to uniquely identify the
						 * resource
						 */

	String ui8source; /*
					 * Information regarding a prior resource from which the
					 * publication was derived
					 */

	String ui8language; /*
						 * Identifies a language of the intellectual content of
						 * the Publication
						 */

	String ui8relation; /*
						 * An identifier of an auxiliary resource and its
						 * relationship to the publication
						 */

	String ui8coverage; /* The extent or scope of the publication�s content */

	String ui8rights; /* Information about rights held in and over the resource */

	long manifestcount;

	String ui8TOC;/* TOC --> storing the table of content ID */

	String ui8version; /* Version of the package identity */

	String ui8UID; /* A Unique-Identifier */

	long spinecount;

	long ui32xmlnscount; /* No. of XML Namespace present in the Package identity */

	long ui32RefCount; /* No of guide element present inside the guide */

}

class ManifestElement {

	String ui8itemid; /*
					 * Element item id with which all other section refers to
					 * the file location
					 */

	String ui8href; /*
					 * A URI interpreted as relative to the JNI_OPF file
					 * containing the reference
					 */

	String ui8fallback; /*
						 * If a fallback attribute points to an item that also
						 * has a fallback attribute
						 */

	String ui8fallbackstyle; /*
							 * fallback-style attribute's value which must
							 * contain a reference to the id of the item
							 * containing an href to the stylesheet desired for
							 * the island
							 */

	String ui8namespace; /*
						 * Inclusion of the required-namespace attribute is not
						 * required in item elements referring to XML documents
						 * authored in Preferred Vocabularies unless Extended
						 * Modules are used, in which case both
						 * required-namespace and required-modules attributes
						 * must be provided
						 */

	String ui8modules;

	public ManifestElement(String ItemId, String href, String fallback,
			String fallbackstyle, String namespace, String modules) {
		this.ui8fallback = fallback;
		this.ui8fallbackstyle = fallbackstyle;
		this.ui8itemid = ItemId;
		this.ui8href = href;
		this.ui8namespace = namespace;
		this.ui8modules = modules;
	}
}

class Manifest {
	ManifestElement pstmanifest[];
}

class SpineElement {
	String ui8itemidref;

	String ui8linearkey;

	public SpineElement(String itemidref, String linearkey) {
		this.ui8itemidref = itemidref;
		this.ui8linearkey = linearkey;

	}
}

class Spine {

	SpineElement pstspine[];
}

class GuideElement {
	String ui8reftype;

	String ui8title;

	String ui8href;

	public GuideElement(String reftype, String title, String href) {

		this.ui8reftype = reftype;
		this.ui8title = title;
		this.ui8href = href;
	}
}

class Guide {
	Guide pstguide[];
}

class PackageElement {
	String ui8xmlns;

	public PackageElement(String xmlns) {
		this.ui8xmlns = xmlns;
	}
}

class Package {
	PackageElement pstpackage[];
}

class OpfInfo {
	Metadata pstmetadata = new Metadata(); /*
											 * Pointer to Meta data structure
											 */

	Manifest pstmanifest[] = new Manifest[20]; /*
												 * Pointer to Mani fest
												 * structure
												 */

	Spine pstspine[] = new Spine[2];

	Guide pstguide[] = new Guide[2];

	Package pstpackage[] = new Package[2];

	TitleAuthor psttitleauthor = new TitleAuthor();

}

class TitleAuthor {
	String ui8Title; // Title Name

	String ui8Author; // Author Name

}

class CpRootFile {
	public String aui8FilePath; /*
								 * File Path of the .opf file in OEBPS/OPF
								 * Directory
								 */
	public String aui8media_type; /* Media type of the rendition file */

	public CpRootFile(String s1, String s2) {
		this.aui8FilePath = s1;
		this.aui8media_type = s2;
	}

}

class CpRoot {
	public long ui32RootFileCount; /* Rootfile count of the container */
	public String aui8version; /* Version of the container */
	public String aui8xmlns; /* Namespace of th eContainer */
}

public class EPUBparser {
	TextView tv;
	Button b;
	public NcxParsedInfo pstNCXroot = new NcxParsedInfo();
	public CpRoot cproot = new CpRoot();// = new CP_ROOT_ST();
	public CpRootFile cprootfile[];
	public Metadata objMetadata = new Metadata();
	public ManifestElement amanifestelement[];
	public SpineElement aspineelement[];
	public GuideElement aguideelement[];
	public PackageElement apackageelement[];
	public CssTagPropertyInfo CssProp[]; 
	public TitleAuthor titileauthorelement = new TitleAuthor();
	public XhtmlFieldParma xhtmlfieldparam = new XhtmlFieldParma();
	public XhtmlFieldParma xhtmlfieldparamPageDown = new XhtmlFieldParma();
	static {
		System.loadLibrary("EPUBparser");
	}
 
	//Native call prototype declaration
	public native CpRootFile[] EPUB_AL_ParseContainerFile(String pui8FilePath,
			CpRoot pstCProot);

	public native ManifestElement[] EPUB_AL_ParseOPFFile(String Path,
			String pui8OPFFilePath, Metadata objMetadata);

	public native SpineElement[] JNI_OPF_Init_Spine(String pui8OPFFilePath,
			Metadata objMetadata);

	public native GuideElement[] JNI_OPF_Init_Guide(String pui8OPFFilePath,
			Metadata objMetadata);

	public native PackageElement[] JNI_OPF_Init_Package(String pui8OPFFilePath,
			Metadata objMetadata);

	public native int EPUB_AL_OPF_ParseTitleAndAuthor(String Path,
			String pui8OPFFilePath, TitleAuthor pstTitleAuthor);

	public native int EPUB_AL_ParseNCXFile(String Path, String filepath,
			NcxParsedInfo pstNCXroot);

	public native NcxTocLvl1[] getLevel1Info();

	public native NcxTocLvl2[] getLevel2Info(int l2index);

	public native NcxTocLvl3[] getLevel3Info(int l2index, int l3index);

	public native NcxTocLvl4[] getLevel4Info(int l2index, int l3index,
			int l4index);

	public native int EPUB_AL_InitXhtmlFile(String BookPath,
			String pui8XHTMLFilePath, XhtmlFieldParma xhtmlfieldparam,
			int ObjDifFlag);

	public native XhtmlLinkInfo[] getLinkInfo();

	public native XhtmlMetaInfo[] getMetaInfo();

	public native int XHTML_GetFirstField(XhtmlFieldParma xhtmlfieldparam,
			String pui8ContentPath, int ObjDifFlag);

	public native int XHTML_GetNextField(XhtmlFieldParma xhtmlfieldparam,
			String pui8ContentPath, int ObjDifFlag,int BypassgetnextField);

	public native void CP_Uninit();

	public native void OPF_DeInit();

	public native void NCX_Uninit();

	public native void XHTML_Deinit();
	
	public native void CSSInit();
	
	public native void CSSUninit();

	public native void XHTML_DeinitIndexgeneration();

	public native String UTILS_ExtractFile(String BookPath, String OpfDir);
	
	public native String SetContentPath();
	
	public native CssTagPropertyInfo[] CssPropInfo();

	public String OPF_GetFilename(Metadata objMetadata, int ui32ChapterIndex,
			SpineElement aspineelement[], ManifestElement amanifestelement[]) {
		int j = 0;
		String pui8Filename = null;
		int i8ChapInManifestFlag = 0;

		if (ui32ChapterIndex <= objMetadata.spinecount) {
			for (j = 0; j < objMetadata.manifestcount; j++) {
				if ((aspineelement[ui32ChapterIndex].ui8itemidref)
						.equals(amanifestelement[j].ui8itemid)) {
					pui8Filename = amanifestelement[j].ui8href;
					i8ChapInManifestFlag = 1;
					break;
				}
			}
			if (i8ChapInManifestFlag == 0) {
				/* idref of spine not contained in id of manifest */
				pui8Filename = pui8Filename + "";

				return null;
			}
		} else {
			pui8Filename = pui8Filename + "";

			return null;
		}

		return pui8Filename;
	}/* end of OPF_GetFilename function */

	public String OPF_GetTOCFileInfo(Metadata objMetadata,
			ManifestElement amanifestelement[]) {
		int j = 0;
		int i8TOCInManifestFlag = 0;
		String pui8TOCLoc = null;

		for (j = 0; j < objMetadata.manifestcount; j++) {
			if ((objMetadata.ui8TOC).equals(amanifestelement[j].ui8itemid)) {
				pui8TOCLoc = amanifestelement[j].ui8href;
				i8TOCInManifestFlag = 1;

				break;
			}
		}
		if (i8TOCInManifestFlag == 0) {
			/* TOC of spine not contained in id of manifest */
			pui8TOCLoc = pui8TOCLoc + "";

			return null;
		}

		return pui8TOCLoc;
	}/* end of OPF_GetTOCFileInfo function */

	//To get RootFile Count
	public long getRootFilecount() {
		return cproot.ui32RootFileCount;
	}
	//To get RootFile Path
	public String getRootFilePath(int i) {
		return cprootfile[i].aui8FilePath;
	}

	//To get content from XhtmlFieldParam class
	public String getContent() {
		return xhtmlfieldparam.pui8Content;
	}
	//To set the content value
	public void setContent(String con) {
		xhtmlfieldparam.pui8Content = con;
	}
	//To set book path
	public void setBookPath(String BookName) {
		xhtmlfieldparam.ui8BookPath = BookName;
	}
	//To get the content path from XhtmlFieldParam class
	public String getContentPath() {
		return xhtmlfieldparam.pui8ContentPath;
	}
	//To set link information
	public void setLinkInfo() {
		xhtmlfieldparam.pstxhtmllink = getLinkInfo();
	}
	//To set meta information
	public void setMetaInfo() {
		xhtmlfieldparam.pstMetaInfo = getMetaInfo();
	}
    //To get the content type
	public int getContentType() {
		return xhtmlfieldparam.enContentype;
	}
	//To get the content size
	public int getContentSize() {
		return xhtmlfieldparam.ui32ContentSize;
	}
	//To get the path of image
	public String getHref() {
		return xhtmlfieldparam.pui8Href;
	}
	// To get the title name
	public String TitleName() {
		return titileauthorelement.ui8Title;
	}
	//To get the authorname
	public String AuthorName() {
		return titileauthorelement.ui8Author;
	}
	//set the level1 info 
	public void setLevel1Info(NcxTocLvl1 l1[]) {
		pstNCXroot.pstLevel1Nodes = l1;
	}
	//set the level2 info
	public void setLevel2Info(NcxTocLvl2 l2[], int i) {
		pstNCXroot.pstLevel1Nodes[i].pstLevel2Nodes = l2;
	}
	//set the level3 info
	public void setLevel3Info(NcxTocLvl3 l3[], int i, int j) {
		pstNCXroot.pstLevel1Nodes[i].pstLevel2Nodes[j].pstLevel3Nodes = l3;
	}
	//set the level4 info
	public void setLevel4Info(NcxTocLvl4 l4[], int i, int j, int k) {
		pstNCXroot.pstLevel1Nodes[i].pstLevel2Nodes[j].pstLevel3Nodes[k].pstLevel4Nodes = l4;
	}
	//get level1 node count
	public int getui32LVL1NodeCount() {
		return pstNCXroot.ui32LVL1NodeCount;
	}

	//get level1 text(chapter) name of ith Location
	public String getaui8Text(int i) {
		return pstNCXroot.pstLevel1Nodes[i].aui8Text;
	}
	//get level1 chapter file name of ith Location
	public String getContentName(int i) {
		return pstNCXroot.pstLevel1Nodes[i].aui8ContentFilename;
	}
	//get level2 node count
	public int getui32LVL2NodeCount(int i) {
		return pstNCXroot.pstLevel1Nodes[i].ui32LVL2NodeCount;
	}
	//get level2 text(sub chapter) name of jth Location in ith chapter
	public String getaui8Text(int i, int j) {
		return pstNCXroot.pstLevel1Nodes[i].pstLevel2Nodes[j].aui8Text;
	}
	//get level2 sub chapter file name of jth Location in ith chapter
	public String getContentName(int i, int j) {
		return pstNCXroot.pstLevel1Nodes[i].pstLevel2Nodes[j].aui8ContentFilename;
	}
	//get level3 node count
	public int getui32LVL3NodeCount(int i, int j) {
		return pstNCXroot.pstLevel1Nodes[i].pstLevel2Nodes[j].ui32LVL3NodeCount;
	}
	//get level3 text(sub sub chapter) name of kth Location in jth sub chapter and ith chapetr
	public String getaui8Text(int i, int j, int k) {
		return pstNCXroot.pstLevel1Nodes[i].pstLevel2Nodes[j].pstLevel3Nodes[k].aui8Text;
	}
	//get level3 sub sub chapter file name of kth Location in jth sub chapter and ith chapetr
	public String getContentName(int i, int j, int k) {
		return pstNCXroot.pstLevel1Nodes[i].pstLevel2Nodes[j].pstLevel3Nodes[k].aui8ContentFilename;
	}
	//get level4 node count
	public int getui32LVL4NodeCount(int i, int j, int k) {
		return pstNCXroot.pstLevel1Nodes[i].pstLevel2Nodes[j].pstLevel3Nodes[k].ui32LVL4NodeCount;
	}
	//get level3 text(sub sub sub chapter) name of lth Location in kth sub sub chapter  jth sub chapetr and ith chapter
	public String getaui8Text(int i, int j, int k, int l) {
		return pstNCXroot.pstLevel1Nodes[i].pstLevel2Nodes[j].pstLevel3Nodes[k].pstLevel4Nodes[l].aui8Text;
	}

	//get level3 sub sub sub chapter name of lth Location in kth sub sub chapter  jth sub chapetr and ith chapter
	public String getContentName(int i, int j, int k, int l) {
		return pstNCXroot.pstLevel1Nodes[i].pstLevel2Nodes[j].pstLevel3Nodes[k].pstLevel4Nodes[l].aui8ContentFilename;
	}
	
	
	/*FOR CSS Parser Code*/
	public int getCsscount()
	{
		return xhtmlfieldparam.propstindex;
	}
	
	public int getenpropty(int i)
	{
		return CssProp[i].enpropty;
	}
	public int getvalue(int i)
	{
		return CssProp[i].value;
	}
	
	public int getenunit(int i)
	{
		return CssProp[i].enunit;
	}
	/*END of CSS parser*/
	//get OPF file path
	public String getOpfPath() {
		return CP_GetOpfDir(CP_GetOpfPath(cproot, cprootfile));
	}
	//sets the content path
	public void setContentPath(String path) {
		xhtmlfieldparam.pui8ContentPath = path;
	}
	
	//to get the chapter name
	public String OPFGetPriviousChapterName(String nameofpresenetchapter)
	{
		for (int k = 0; k < objMetadata.spinecount; k++) {
			for (int j = 0; j < objMetadata.manifestcount; j++) {
				//Comparing chapter name for getting chapter index
				if ((nameofpresenetchapter)
								.equals(amanifestelement[j].ui8href))
				{
					return amanifestelement[j-1].ui8href;
				}
			}
		}
		
		return null;
		
	}
	
	
	
	public String OPFGetNextChapterName(String nameofpresenetchapter)
	{
		for (int k = 0; k < objMetadata.spinecount; k++) {
			for (int j = 0; j < objMetadata.manifestcount; j++) {
				//Comparing chapter name for getting chapter index
				if ((nameofpresenetchapter)
								.equals(amanifestelement[j].ui8href))
				{
					return amanifestelement[j+1].ui8href;
				}
			}
		}
		
		return null;
		
	}
	
	
	
	

	//get the chapter number according to chapter name
	public int OPF_GetChapterIndex(String pui8Filename, Metadata objMetadata,
			SpineElement aspineelement[], ManifestElement amanifestelement[]) {
		int j = 0, k = 0;
		int pui32ChapterIndex = 0;
		int i8ChapInManifestFlag = DispMgrUtility.EBOOK_FALSE;

		if (pui8Filename != null && pui8Filename != "") {

			for (k = 0; k < objMetadata.spinecount; k++) {
				for (j = 0; j < objMetadata.manifestcount; j++) {
					//Comparing chapter name for getting chapter index
					if ((aspineelement[k].ui8itemidref)
							.equals(amanifestelement[j].ui8itemid)
							&& (pui8Filename)
									.equals(amanifestelement[j].ui8href))

					{
						pui32ChapterIndex = k;
						i8ChapInManifestFlag = DispMgrUtility.EBOOK_TRUE;

						return pui32ChapterIndex;
					}
				}
			}
			if (i8ChapInManifestFlag == DispMgrUtility.EBOOK_FALSE) {
				/* Filename name is not present in manifest */
				return -1;
			}

			else {
				pui8Filename = pui8Filename + "";
				return -1;
			}
		}

		return pui32ChapterIndex;
	}/* end of OPF_GetChapterIndex function */

	/*
	 * To get the OPF file path 
	 */
	public String CP_GetOpfPath(CpRoot pstCProot, CpRootFile[] cproot) {

		int i = 0;
		String CP_OPF_MIMETYPE = "application/oebps-package+xml";
		String opffilepath;
		//loop up to rootfile count
		for (i = 0; i < pstCProot.ui32RootFileCount; i++) {
			if ((cproot[i].aui8media_type.equalsIgnoreCase(CP_OPF_MIMETYPE))) {
				opffilepath = cproot[i].aui8FilePath;
				return opffilepath;
			}

		}

		return null;
	}

	//To get total no of chapters
	public long OPF_Get_No_of_Chapters(Metadata objMetadata) {
		long no_of_chapters;
		no_of_chapters = objMetadata.spinecount;
		return no_of_chapters;
	}/* end of OPF_Get_No_of_Chapters function */

	//To get the OPF file  containing directory if any 
	public String CP_GetOpfDir(String OpfFilePath) {
		int pui8p1 = -1;
		int len = 0;

		pui8p1 = OpfFilePath.lastIndexOf("/");

		if (-1 == pui8p1) {
			return null;
		}

		len = (OpfFilePath.length() - pui8p1);

		if (0 == len) {
			return null;
		}

		String OpfDirName = OpfFilePath.substring(0, pui8p1);

		return OpfDirName;

	}

}

//package com.ipaper.myapp;
//
//public class HTMLLinkExtractor {
//
//}

package com.ipaper.myapp;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class HTMLLinkExtractor{
 
	  private Pattern patternTag, patternLink;
	  private Matcher matcherTag, matcherLink;
 
	  private static final String HTML_A_TAG_PATTERN = 
                      "(?i)<a([^>]+)>(.+?)</a>";
 
	  private static final String HTML_A_HREF_TAG_PATTERN = 
                      "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
 
	  public HTMLLinkExtractor(){
		  patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
		  patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
	  }
 
	  /**
	   * Validate html with regular expression
	   * @param html html content for validation
	   * @return Vector links and link text
	   */
	  public Vector<HtmlLink> grabHTMLLinks(final String html){
 
		  Vector<HtmlLink> result = new Vector<HtmlLink>();
 
		  matcherTag = patternTag.matcher(html);
 
		  while(matcherTag.find()){
 
			  String href = matcherTag.group(1); //href
			  String linkText = matcherTag.group(2); //link text
 
			  matcherLink = patternLink.matcher(href);
 
			  while(matcherLink.find()){
 
				  String link = matcherLink.group(1); //link
 
				  result.add(new HtmlLink(link, linkText,html));
 
			  }
 
		  }
 
		  return result;
 
	  }
 
	public class HtmlLink {
 
		String link;
		String linkText;
		String origLink;
 
		HtmlLink(String link, String linkText){
			this.link = link;
			this.linkText = linkText;
		}
		HtmlLink(String link, String linkText, String orig){
			this.link = link;
			this.linkText = linkText;
			this.origLink = orig;
		}
 
		@Override
		public String toString() {
			return new StringBuffer("Link : ")
			.append(this.link)
			.append(" Link Text : ")
			.append(this.linkText)
			.append("Orig Text : ")
			.append(this.origLink).toString();
		}	    
	}
}
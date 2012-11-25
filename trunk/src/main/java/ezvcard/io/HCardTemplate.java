package ezvcard.io;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import ezvcard.VCard;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/*
 Copyright (c) 2012, Michael Angstadt
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met: 

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer. 
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution. 

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those
 of the authors and should not be interpreted as representing official policies, 
 either expressed or implied, of the FreeBSD Project.
 */

/**
 * Writes vCards to an HTML page (hCard format).
 * @author Michael Angstadt
 * @see <a
 * href="http://microformats.org/wiki/hcard">http://microformats.org/wiki/hcard</a>
 */
public class HCardTemplate {
	protected Template template;
	protected final List<VCard> vcards = new ArrayList<VCard>();

	public HCardTemplate() {
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(HCardTemplate.class, "");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setWhitespaceStripping(true);
		try {
			template = cfg.getTemplate("hcard-template.html");
		} catch (IOException e) {
			//should never be thrown because it's always on the classpath
		}
	}

	/**
	 * Adds a vCard to the HTML page.
	 * @param vcard the vCard to add
	 */
	public void addVCard(VCard vcard) {
		vcards.add(vcard);
	}

	/**
	 * Writes the HTML document to a string.
	 * @return the HTML document
	 * @throws TemplateException if there's a problem with the freemarker
	 * template
	 */
	public String write() throws TemplateException {
		try {
			StringWriter sw = new StringWriter();
			write(sw);
			return sw.toString();
		} catch (IOException e) {
			//never thrown because we're writing to a string
		}
		return null;
	}

	/**
	 * Writes the HTML document to an output stream
	 * @param writer the output stream
	 * @throws IOException if there's a problem writing to the output stream
	 * @throws TemplateException if there's a problem with the freemarker
	 * template
	 */
	public void write(Writer writer) throws IOException, TemplateException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("vcards", vcards);
		map.put("base64", new Base64Encoder());

		template.process(map, writer);
		writer.flush();
	}

	/**
	 * Used for base64-encoding binary data in the freemarker template.
	 */
	public static class Base64Encoder {
		public String encode(byte data[]) {
			return new String(Base64.encodeBase64(data));
		}
	}
}

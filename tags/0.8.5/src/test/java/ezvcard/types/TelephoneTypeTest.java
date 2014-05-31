package ezvcard.types;

import static ezvcard.util.TestUtils.assertIntEquals;
import static ezvcard.util.TestUtils.assertJCardValue;
import static ezvcard.util.TestUtils.assertMarshalXml;
import static ezvcard.util.TestUtils.assertSetEquals;
import static ezvcard.util.TestUtils.assertWarnings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ezvcard.VCard;
import ezvcard.VCardDataType;
import ezvcard.VCardSubTypes;
import ezvcard.VCardVersion;
import ezvcard.io.CannotParseException;
import ezvcard.io.CompatibilityMode;
import ezvcard.parameters.TelephoneTypeParameter;
import ezvcard.util.HtmlUtils;
import ezvcard.util.JCardValue;
import ezvcard.util.TelUri;
import ezvcard.util.XCardElement;

/*
 Copyright (c) 2013, Michael Angstadt
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
 * @author Michael Angstadt
 */
public class TelephoneTypeTest {
	private final List<String> warnings = new ArrayList<String>();
	private final CompatibilityMode compatibilityMode = CompatibilityMode.RFC;
	private final VCardSubTypes subTypes = new VCardSubTypes();
	private final VCard vcard = new VCard();
	private final String number = "+1-555-555-1234";
	private final String numberWithExt = "+1-555-555-1234 x101";
	private final String uri = "tel:" + number;
	private final String uriWithExt = uri + ";ext=101";
	private final TelephoneType marshalObjText = new TelephoneType(number);
	private final TelephoneType marshalObjUri = new TelephoneType(new TelUri.Builder(number).extension("101").build());
	private TelephoneType unmarshalObj;

	@Before
	public void before() {
		warnings.clear();
		unmarshalObj = new TelephoneType();
	}

	@Test
	public void validate() {
		assertWarnings(1, unmarshalObj.validate(VCardVersion.V2_1, vcard));
		assertWarnings(1, unmarshalObj.validate(VCardVersion.V3_0, vcard));
		assertWarnings(1, unmarshalObj.validate(VCardVersion.V4_0, vcard));

		assertWarnings(0, marshalObjText.validate(VCardVersion.V2_1, vcard));
		assertWarnings(0, marshalObjText.validate(VCardVersion.V3_0, vcard));
		assertWarnings(0, marshalObjText.validate(VCardVersion.V4_0, vcard));

		assertWarnings(1, marshalObjUri.validate(VCardVersion.V2_1, vcard));
		assertWarnings(1, marshalObjUri.validate(VCardVersion.V3_0, vcard));
		assertWarnings(0, marshalObjUri.validate(VCardVersion.V4_0, vcard));
	}

	@Test
	public void marshalSubTypes_2_1_text() {
		VCardVersion version = VCardVersion.V2_1;
		VCardSubTypes subTypes = marshalObjText.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
	}

	@Test
	public void marshalSubTypes_2_1_uri() {
		VCardVersion version = VCardVersion.V2_1;
		VCardSubTypes subTypes = marshalObjUri.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
	}

	@Test
	public void marshalSubTypes_3_0_text() {
		VCardVersion version = VCardVersion.V3_0;
		VCardSubTypes subTypes = marshalObjText.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
	}

	@Test
	public void marshalSubTypes_3_0_uri() {
		VCardVersion version = VCardVersion.V3_0;
		VCardSubTypes subTypes = marshalObjUri.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
	}

	@Test
	public void marshalSubTypes_4_0_text() {
		VCardVersion version = VCardVersion.V4_0;
		VCardSubTypes subTypes = marshalObjText.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(0, subTypes.size());
	}

	@Test
	public void marshalSubTypes_4_0_uri() {
		VCardVersion version = VCardVersion.V4_0;
		VCardSubTypes subTypes = marshalObjUri.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(1, subTypes.size());
		assertEquals(VCardDataType.URI, subTypes.getValue());
	}

	/**
	 * If a property contains a "TYPE=pref" parameter and it's being marshalled
	 * to 4.0, it should replace "TYPE=pref" with "PREF=1".
	 */
	@Test
	public void marshalSubTypes_type_pref_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		TelephoneType tel = new TelephoneType();
		tel.addType(TelephoneTypeParameter.PREF);
		VCardSubTypes subTypes = tel.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(1, subTypes.size());
		assertNull(subTypes.getPref());
		assertSetEquals(subTypes.getTypes(), TelephoneTypeParameter.PREF.getValue());
	}

	/**
	 * If a property contains a "TYPE=pref" parameter and it's being marshalled
	 * to 4.0, it should replace "TYPE=pref" with "PREF=1".
	 */
	@Test
	public void marshalSubTypes_type_pref_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		TelephoneType tel = new TelephoneType();
		tel.addType(TelephoneTypeParameter.PREF);
		VCardSubTypes subTypes = tel.marshalSubTypes(version, compatibilityMode, vcard);

		assertEquals(1, subTypes.size());
		assertNull(subTypes.getPref());
		assertSetEquals(subTypes.getTypes(), TelephoneTypeParameter.PREF.getValue());
	}

	/**
	 * If a property contains a "TYPE=pref" parameter and it's being marshalled
	 * to 4.0, it should replace "TYPE=pref" with "PREF=1".
	 */
	@Test
	public void marshalSubTypes_type_pref_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		TelephoneType tel = new TelephoneType();
		tel.addType(TelephoneTypeParameter.PREF);
		VCardSubTypes subTypes = tel.marshalSubTypes(version, compatibilityMode, new VCard());

		assertEquals(1, subTypes.size());
		assertIntEquals(1, subTypes.getPref());
		assertSetEquals(subTypes.getTypes());
	}

	/**
	 * If properties contain "PREF" parameters and they're being marshalled to
	 * 2.1/3.0, then it should find the type with the lowest PREF value and add
	 * "TYPE=pref" to it.
	 */
	@Test
	public void marshalSubTypes_pref_parameter_2_1() {
		VCardVersion version = VCardVersion.V2_1;

		VCard vcard = new VCard();
		TelephoneType tel1 = new TelephoneType();
		tel1.setPref(1);
		vcard.addTelephoneNumber(tel1);
		TelephoneType tel2 = new TelephoneType();
		tel2.setPref(2);
		vcard.addTelephoneNumber(tel2);

		VCardSubTypes subTypes = tel1.marshalSubTypes(version, compatibilityMode, vcard);
		assertEquals(1, subTypes.size());
		assertNull(subTypes.getPref());
		assertSetEquals(subTypes.getTypes(), TelephoneTypeParameter.PREF.getValue());

		subTypes = tel2.marshalSubTypes(version, compatibilityMode, vcard);
		assertEquals(0, subTypes.size());
		assertNull(subTypes.getPref());
		assertSetEquals(subTypes.getTypes());
	}

	/**
	 * If properties contain "PREF" parameters and they're being marshalled to
	 * 2.1/3.0, then it should find the type with the lowest PREF value and add
	 * "TYPE=pref" to it.
	 */
	@Test
	public void marshalSubTypes_pref_parameter_3_0() {
		VCardVersion version = VCardVersion.V3_0;

		VCard vcard = new VCard();
		TelephoneType tel1 = new TelephoneType();
		tel1.setPref(1);
		vcard.addTelephoneNumber(tel1);
		TelephoneType tel2 = new TelephoneType();
		tel2.setPref(2);
		vcard.addTelephoneNumber(tel2);

		VCardSubTypes subTypes = tel1.marshalSubTypes(version, compatibilityMode, vcard);
		assertEquals(1, subTypes.size());
		assertNull(subTypes.getPref());
		assertSetEquals(subTypes.getTypes(), TelephoneTypeParameter.PREF.getValue());

		subTypes = tel2.marshalSubTypes(version, compatibilityMode, vcard);
		assertEquals(0, subTypes.size());
		assertNull(subTypes.getPref());
		assertSetEquals(subTypes.getTypes());
	}

	/**
	 * If properties contain "PREF" parameters and they're being marshalled to
	 * 2.1/3.0, then it should find the type with the lowest PREF value and add
	 * "TYPE=pref" to it.
	 */
	@Test
	public void marshalSubTypes_pref_parameter_4_0() {
		VCardVersion version = VCardVersion.V4_0;

		VCard vcard = new VCard();
		TelephoneType tel1 = new TelephoneType();
		tel1.setPref(1);
		vcard.addTelephoneNumber(tel1);
		TelephoneType tel2 = new TelephoneType();
		tel2.setPref(2);
		vcard.addTelephoneNumber(tel2);

		version = VCardVersion.V4_0;
		VCardSubTypes subTypes = tel1.marshalSubTypes(version, compatibilityMode, vcard);
		assertEquals(1, subTypes.size());
		assertIntEquals(1, subTypes.getPref());
		assertSetEquals(subTypes.getTypes());

		subTypes = tel2.marshalSubTypes(version, compatibilityMode, vcard);
		assertEquals(1, subTypes.size());
		assertIntEquals(2, subTypes.getPref());
		assertSetEquals(subTypes.getTypes());
	}

	@Test
	public void marshalText_2_1_text() {
		VCardVersion version = VCardVersion.V2_1;
		String actual = marshalObjText.marshalText(version, compatibilityMode);

		assertEquals(number, actual);
	}

	@Test
	public void marshalText_2_1_uri() {
		VCardVersion version = VCardVersion.V2_1;
		String actual = marshalObjUri.marshalText(version, compatibilityMode);

		assertEquals(numberWithExt, actual);
	}

	@Test
	public void marshalText_3_0_text() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = marshalObjText.marshalText(version, compatibilityMode);

		assertEquals(number, actual);
	}

	@Test
	public void marshalText_3_0_uri() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = marshalObjUri.marshalText(version, compatibilityMode);

		assertEquals(numberWithExt, actual);
	}

	@Test
	public void marshalText_4_0_text() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = marshalObjText.marshalText(version, compatibilityMode);

		assertEquals(number, actual);
	}

	@Test
	public void marshalText_4_0_uri() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = marshalObjUri.marshalText(version, compatibilityMode);

		assertEquals(uriWithExt, actual);
	}

	@Test
	public void marshalXml_text() {
		assertMarshalXml(marshalObjText, "<text>" + number + "</text>");
	}

	@Test
	public void marshalXml_uri() {
		assertMarshalXml(marshalObjUri, "<uri>" + uriWithExt + "</uri>");
	}

	@Test
	public void marshalJson_text() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = marshalObjText.marshalJson(version);

		assertJCardValue(VCardDataType.TEXT, number, value);
	}

	@Test
	public void marshalJson_uri() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = marshalObjUri.marshalJson(version);

		assertJCardValue(VCardDataType.URI, uriWithExt, value);
	}

	@Test
	public void unmarshalText_2_1_text() {
		VCardVersion version = VCardVersion.V2_1;
		unmarshalObj.unmarshalText(subTypes, number, version, warnings, compatibilityMode);

		assertEquals(number, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_2_1_uri() {
		VCardVersion version = VCardVersion.V2_1;
		unmarshalObj.unmarshalText(subTypes, uri, version, warnings, compatibilityMode);

		assertEquals(uri, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_3_0_text() {
		VCardVersion version = VCardVersion.V3_0;
		unmarshalObj.unmarshalText(subTypes, number, version, warnings, compatibilityMode);

		assertEquals(number, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_3_0_uri() {
		VCardVersion version = VCardVersion.V3_0;
		unmarshalObj.unmarshalText(subTypes, uri, version, warnings, compatibilityMode);

		assertEquals(uri, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_4_0_text() {
		VCardVersion version = VCardVersion.V4_0;
		unmarshalObj.unmarshalText(subTypes, uri, version, warnings, compatibilityMode);

		assertEquals(uri, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_4_0_uri() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(VCardDataType.URI);
		unmarshalObj.unmarshalText(subTypes, uri, version, warnings, compatibilityMode);

		assertNull(unmarshalObj.getText());
		assertEquals(number, unmarshalObj.getUri().getNumber());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_4_0_uri_invalid() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(VCardDataType.URI);
		unmarshalObj.unmarshalText(subTypes, number, version, warnings, compatibilityMode);

		assertEquals(number, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());
		assertWarnings(1, warnings);
	}

	@Test
	public void unmarshalXml_text() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TelephoneType.NAME.toLowerCase());
		xe.append(VCardDataType.TEXT, number);
		unmarshalObj.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertEquals(number, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalXml_uri() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TelephoneType.NAME.toLowerCase());
		xe.append(VCardDataType.URI, uri);
		unmarshalObj.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertNull(unmarshalObj.getText());
		assertEquals(number, unmarshalObj.getUri().getNumber());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalXml_uri_invalid() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TelephoneType.NAME.toLowerCase());
		xe.append(VCardDataType.URI, number);
		unmarshalObj.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertEquals(number, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());
		assertWarnings(1, warnings);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalXml_no_value() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TelephoneType.NAME.toLowerCase());
		unmarshalObj.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalHtml() {
		//@formatter:off
		org.jsoup.nodes.Element element = HtmlUtils.toElement(
		"<div>" +
			"<span class=\"type\">home</span>" +
			"<span class=\"type\">cell</span>" +
			"<span class=\"type\">foo</span>" +
			"<span class=\"value\">" + number + "</span>" +
		"</div>");
		//@formatter:on

		unmarshalObj.unmarshalHtml(element, warnings);

		assertEquals(number, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());

		assertEquals(3, unmarshalObj.getSubTypes().size());
		assertSetEquals(unmarshalObj.getTypes(), TelephoneTypeParameter.HOME, TelephoneTypeParameter.CELL, TelephoneTypeParameter.get("foo"));

		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalHtml_href() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<a href=\"" + uri + "\">Call me</a>");

		unmarshalObj.unmarshalHtml(element, warnings);

		assertEquals(0, unmarshalObj.getSubTypes().size());
		assertEquals(number, unmarshalObj.getUri().getNumber());
		assertNull(unmarshalObj.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalHtml_invalid_href_value() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<a href=\"foo\">" + number + "</a>");

		unmarshalObj.unmarshalHtml(element, warnings);

		assertEquals(number, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalJson_text() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.TEXT, number);

		unmarshalObj.unmarshalJson(subTypes, value, version, warnings);

		assertEquals(0, unmarshalObj.getSubTypes().size());
		assertEquals(number, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalJson_uri() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.URI, uri);

		unmarshalObj.unmarshalJson(subTypes, value, version, warnings);

		assertEquals(0, unmarshalObj.getSubTypes().size());
		assertNull(unmarshalObj.getText());
		assertEquals(number, unmarshalObj.getUri().getNumber());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalJson_uri_invalid() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.URI, number);

		unmarshalObj.unmarshalJson(subTypes, value, version, warnings);

		assertEquals(0, unmarshalObj.getSubTypes().size());
		assertEquals(number, unmarshalObj.getText());
		assertNull(unmarshalObj.getUri());
		assertWarnings(1, warnings);
	}
}
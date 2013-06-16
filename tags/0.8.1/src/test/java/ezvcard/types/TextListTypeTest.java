package ezvcard.types;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import ezvcard.VCardSubTypes;
import ezvcard.VCardVersion;
import ezvcard.io.CompatibilityMode;
import ezvcard.util.JCardDataType;
import ezvcard.util.JCardValue;
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
public class TextListTypeTest {
	final List<String> warnings = new ArrayList<String>();
	final CompatibilityMode compatibilityMode = CompatibilityMode.RFC;
	final VCardSubTypes subTypes = new VCardSubTypes();
	final TextListTypeImpl zeroItems = new TextListTypeImpl();
	final TextListTypeImpl oneItem = new TextListTypeImpl();
	{
		oneItem.addValue("one");
	}
	final TextListTypeImpl multipleItems = new TextListTypeImpl();
	{
		multipleItems.addValue("one");
		multipleItems.addValue("two");
		multipleItems.addValue("three");
	}
	final TextListTypeImpl multipleItemsStructured = new TextListTypeImpl(';');
	{
		multipleItemsStructured.addValue("one");
		multipleItemsStructured.addValue("two");
		multipleItemsStructured.addValue("three");
	}
	final TextListTypeImpl specialChars = new TextListTypeImpl();
	{
		specialChars.addValue("on,e");
		specialChars.addValue("tw;o");
		specialChars.addValue("three");
	}
	TextListTypeImpl testObj;

	@Before
	public void before() {
		warnings.clear();
		testObj = new TextListTypeImpl();
	}

	@Test
	public void marshalText_zero_items() {
		VCardVersion version = VCardVersion.V2_1;
		String expected = "";
		String actual = zeroItems.marshalText(version, warnings, compatibilityMode);

		assertEquals(expected, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_one_item() {
		VCardVersion version = VCardVersion.V2_1;
		String expected = "one";
		String actual = oneItem.marshalText(version, warnings, compatibilityMode);

		assertEquals(expected, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_multiple_items() {
		VCardVersion version = VCardVersion.V2_1;
		String expected = "one,two,three";
		String actual = multipleItems.marshalText(version, warnings, compatibilityMode);

		assertEquals(expected, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_escape_special_chars() {
		VCardVersion version = VCardVersion.V2_1;
		String expected = "on\\,e,tw\\;o,three";
		String actual = specialChars.marshalText(version, warnings, compatibilityMode);

		assertEquals(expected, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalText_delimiter() {
		VCardVersion version = VCardVersion.V2_1;
		TextListType t = new TextListType("NAME", '*');
		t.addValue("one");
		t.addValue("two");
		t.addValue("three");
		String expected = "one*two*three";
		String actual = t.marshalText(version, warnings, compatibilityMode);

		assertEquals(expected, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalXml_zero_items() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TextListTypeImpl.NAME.toLowerCase());
		Document expected = xe.document();
		xe = new XCardElement(TextListTypeImpl.NAME.toLowerCase());
		Document actual = xe.document();
		zeroItems.marshalXml(xe.element(), version, warnings, compatibilityMode);

		assertXMLEqual(expected, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalXml_one_item() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TextListTypeImpl.NAME.toLowerCase());
		xe.text("one");
		Document expected = xe.document();
		xe = new XCardElement(TextListTypeImpl.NAME.toLowerCase());
		Document actual = xe.document();
		oneItem.marshalXml(xe.element(), version, warnings, compatibilityMode);

		assertXMLEqual(expected, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalXml_multiple_items() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TextListTypeImpl.NAME.toLowerCase());
		xe.text("one");
		xe.text("two");
		xe.text("three");
		Document expected = xe.document();
		xe = new XCardElement(TextListTypeImpl.NAME.toLowerCase());
		Document actual = xe.document();
		multipleItems.marshalXml(xe.element(), version, warnings, compatibilityMode);

		assertXMLEqual(expected, actual);
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalJson_zero_items() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = zeroItems.marshalJson(version, warnings);
		assertEquals(JCardDataType.TEXT, value.getDataType());
		assertFalse(value.isStructured());

		//@formatter:off
		@SuppressWarnings("unchecked")
		List<List<Object>> expectedValues = Arrays.asList();
		//@formatter:on
		assertEquals(expectedValues, value.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalJson_one_item() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = oneItem.marshalJson(version, warnings);
		assertEquals(JCardDataType.TEXT, value.getDataType());
		assertFalse(value.isStructured());

		//@formatter:off
		@SuppressWarnings("unchecked")
		List<List<Object>> expectedValues = Arrays.asList(
			Arrays.asList(new Object[]{"one"})
		);
		//@formatter:on
		assertEquals(expectedValues, value.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalJson_multiple_items() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = multipleItems.marshalJson(version, warnings);
		assertEquals(JCardDataType.TEXT, value.getDataType());
		assertFalse(value.isStructured());

		//@formatter:off
		@SuppressWarnings("unchecked")
		List<List<Object>> expectedValues = Arrays.asList(
			Arrays.asList(new Object[]{ "one" }),
			Arrays.asList(new Object[]{ "two" }),
			Arrays.asList(new Object[]{ "three" })
		);
		//@formatter:on
		assertEquals(expectedValues, value.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void marshalJson_structured() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = multipleItemsStructured.marshalJson(version, warnings);
		assertEquals(JCardDataType.TEXT, value.getDataType());
		assertTrue(value.isStructured());

		//@formatter:off
		@SuppressWarnings("unchecked")
		List<List<Object>> expectedValues = Arrays.asList(
			Arrays.asList(new Object[]{ "one" }),
			Arrays.asList(new Object[]{ "two" }),
			Arrays.asList(new Object[]{ "three" })
		);
		//@formatter:on
		assertEquals(expectedValues, value.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalText_zero_items() {
		VCardVersion version = VCardVersion.V2_1;
		testObj.unmarshalText(subTypes, "", version, warnings, compatibilityMode);

		assertEquals(Arrays.asList(), testObj.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalText_one_item() {
		VCardVersion version = VCardVersion.V2_1;
		testObj.unmarshalText(subTypes, "one", version, warnings, compatibilityMode);

		assertEquals(Arrays.asList("one"), testObj.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalText_multiple_items() {
		VCardVersion version = VCardVersion.V2_1;
		testObj.unmarshalText(subTypes, "one,two,three", version, warnings, compatibilityMode);

		assertEquals(Arrays.asList("one", "two", "three"), testObj.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalText_unescape_special_chars() {
		VCardVersion version = VCardVersion.V2_1;
		testObj.unmarshalText(subTypes, "on\\,e,tw\\;o,three", version, warnings, compatibilityMode);

		assertEquals(Arrays.asList("on,e", "tw;o", "three"), testObj.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalXml_zero_values() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TextListTypeImpl.NAME.toLowerCase());
		testObj.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertEquals(Arrays.asList(), testObj.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalXml_one_value() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TextListTypeImpl.NAME.toLowerCase());
		xe.text("one");
		testObj.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertEquals(Arrays.asList("one"), testObj.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalXml_multiple_values() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(TextListTypeImpl.NAME.toLowerCase());
		xe.text("one");
		xe.text("two");
		xe.text("three");
		testObj.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);

		assertEquals(Arrays.asList("one", "two", "three"), testObj.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalJson_zero_items() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = new JCardValue();
		value.setDataType(JCardDataType.TEXT);

		testObj.unmarshalJson(subTypes, value, version, warnings);

		assertEquals(Arrays.asList(), testObj.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalJson_one_item() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = new JCardValue();
		value.addValues("one");
		value.setDataType(JCardDataType.TEXT);

		testObj.unmarshalJson(subTypes, value, version, warnings);

		assertEquals(Arrays.asList("one"), testObj.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void unmarshalJson_multiple_items() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = new JCardValue();
		value.addValues("one", "two", "three");
		value.setDataType(JCardDataType.TEXT);

		testObj.unmarshalJson(subTypes, value, version, warnings);

		assertEquals(Arrays.asList("one", "two", "three"), testObj.getValues());
		assertEquals(0, warnings.size());
	}

	@Test
	public void removeValue() {
		testObj.addValue("one");
		testObj.addValue("two");
		testObj.addValue("three");
		testObj.removeValue("two");
		testObj.removeValue("four");
		assertEquals(Arrays.asList("one", "three"), testObj.getValues());
	}

	private class TextListTypeImpl extends TextListType {
		public static final String NAME = "NAME";

		public TextListTypeImpl() {
			this(',');
		}

		public TextListTypeImpl(char separator) {
			super(NAME, separator);
		}
	}
}
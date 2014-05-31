package ezvcard.types;

import static ezvcard.util.TestUtils.assertJCardValue;
import static ezvcard.util.TestUtils.assertMarshalXml;
import static ezvcard.util.TestUtils.assertWarnings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import ezvcard.VCard;
import ezvcard.VCardDataType;
import ezvcard.VCardSubTypes;
import ezvcard.VCardVersion;
import ezvcard.io.CannotParseException;
import ezvcard.io.CompatibilityMode;
import ezvcard.util.HtmlUtils;
import ezvcard.util.JCardValue;
import ezvcard.util.PartialDate;
import ezvcard.util.XCardElement;
import ezvcard.util.XmlUtils;

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
public class DateOrTimeTypeTest {
	private final List<String> warnings = new ArrayList<String>();
	private final CompatibilityMode compatibilityMode = CompatibilityMode.RFC;
	private final VCard vcard = new VCard();
	private final VCardSubTypes subTypes = new VCardSubTypes();

	private final Date date;
	{
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR, 1980);
		c.set(Calendar.MONTH, Calendar.JUNE);
		c.set(Calendar.DAY_OF_MONTH, 5);
		date = c.getTime();
	}
	private final String dateStr = "19800605";
	private final String dateStrExtended = "1980-06-05";

	private final Date dateTime;
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 13);
		c.set(Calendar.MINUTE, 10);
		c.set(Calendar.SECOND, 20);
		dateTime = c.getTime();
	}
	private final String dateTimeRegex = dateStr + "T131020[-\\+]\\d{4}"; //account for local machine's timezone
	private final String dateTimeExtendedRegex = dateStrExtended + "T13:10:20[-\\+]\\d{2}:\\d{2}";

	private final PartialDate reducedAccuracyDate = PartialDate.date(null, 6, 5);
	private final PartialDate reducedAccuracyDateTime = PartialDate.dateTime(null, 6, 5, 13, 10, 20);

	private final String text = "Sometime in, ;1980;";
	private final String textEscaped = "Sometime in\\, \\;1980\\;";

	/////////////////////////////////////////////////////////////////////////////////////////

	private final DateOrTimeTypeImpl dateType = new DateOrTimeTypeImpl();
	{
		dateType.setDate(date, false);
	}
	private DateOrTimeTypeImpl type;

	@Before
	public void before() {
		type = new DateOrTimeTypeImpl();
		warnings.clear();
		subTypes.clear();
	}

	@Test
	public void validate() {
		assertWarnings(1, type.validate(VCardVersion.V2_1, vcard));
		assertWarnings(1, type.validate(VCardVersion.V3_0, vcard));
		assertWarnings(1, type.validate(VCardVersion.V4_0, vcard));

		assertWarnings(0, dateType.validate(VCardVersion.V2_1, vcard));
		assertWarnings(0, dateType.validate(VCardVersion.V3_0, vcard));
		assertWarnings(0, dateType.validate(VCardVersion.V4_0, vcard));

		DateOrTimeTypeImpl partialDateType = new DateOrTimeTypeImpl();
		partialDateType.setPartialDate(reducedAccuracyDate);
		assertWarnings(1, partialDateType.validate(VCardVersion.V2_1, vcard));
		assertWarnings(1, partialDateType.validate(VCardVersion.V3_0, vcard));
		assertWarnings(0, partialDateType.validate(VCardVersion.V4_0, vcard));

		DateOrTimeTypeImpl textDateType = new DateOrTimeTypeImpl();
		textDateType.setText(text);
		assertWarnings(1, textDateType.validate(VCardVersion.V2_1, vcard));
		assertWarnings(1, textDateType.validate(VCardVersion.V3_0, vcard));
		assertWarnings(0, textDateType.validate(VCardVersion.V4_0, vcard));
	}

	@Test
	public void marshalText_date_2_1() {
		VCardVersion version = VCardVersion.V2_1;

		String actual = dateType.marshalText(version, compatibilityMode);
		assertEquals(dateStr, actual);

		VCardSubTypes subTypes = dateType.marshalSubTypes(version, compatibilityMode, vcard);
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalText_date_3_0() {
		VCardVersion version = VCardVersion.V3_0;

		String actual = dateType.marshalText(version, compatibilityMode);
		assertEquals(dateStr, actual);

		VCardSubTypes subTypes = dateType.marshalSubTypes(version, compatibilityMode, vcard);
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalText_date_4_0() {
		VCardVersion version = VCardVersion.V4_0;

		String actual = dateType.marshalText(version, compatibilityMode);
		assertEquals(dateStr, actual);

		VCardSubTypes subTypes = dateType.marshalSubTypes(version, compatibilityMode, vcard);
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalXml_date() {
		assertMarshalXml(dateType, "<date>" + dateStr + "</date>");
	}

	@Test
	public void marshalJson_date() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = dateType.marshalJson(version);

		assertJCardValue(VCardDataType.DATE, dateStrExtended, value);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	final DateOrTimeTypeImpl dateTimeType = new DateOrTimeTypeImpl();
	{
		dateTimeType.setDate(dateTime, true);
	}

	@Test
	public void marshalText_datetime_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		String actual = dateTimeType.marshalText(version, compatibilityMode);
		assertTrue(actual.matches(dateTimeRegex));

		VCardSubTypes subTypes = dateTimeType.marshalSubTypes(version, compatibilityMode, vcard);
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalText_datetime_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = dateTimeType.marshalText(version, compatibilityMode);
		assertTrue(actual.matches(dateTimeRegex));

		VCardSubTypes subTypes = dateTimeType.marshalSubTypes(version, compatibilityMode, vcard);
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalText_datetime_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = dateTimeType.marshalText(version, compatibilityMode);
		assertTrue(actual.matches(dateTimeRegex));
		VCardSubTypes subTypes = dateTimeType.marshalSubTypes(version, compatibilityMode, vcard);

		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalXml_datetime() {
		VCardVersion version = VCardVersion.V4_0;
		XCardElement xe = new XCardElement(DateOrTimeTypeImpl.NAME.toLowerCase());
		Element element = xe.element();
		dateTimeType.marshalXml(element, version, compatibilityMode);

		assertTrue(XmlUtils.getFirstChildElement(element).getTextContent().matches(dateTimeRegex));
	}

	@Test
	public void marshalJson_datetime() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = dateTimeType.marshalJson(version);

		assertEquals(VCardDataType.DATE_TIME, value.getDataType());
		assertEquals(1, value.getValues().size());
		String valueStr = (String) value.getValues().get(0).getValue();
		assertTrue(valueStr, valueStr.matches(dateTimeExtendedRegex));
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	final DateOrTimeTypeImpl reducedAccuracyDateType = new DateOrTimeTypeImpl();
	{
		reducedAccuracyDateType.setPartialDate(reducedAccuracyDate);
	}

	@Test
	public void marshalText_reducedAccuracy_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		String actual = reducedAccuracyDateType.marshalText(version, compatibilityMode);
		assertEquals("", actual);

		VCardSubTypes subTypes = reducedAccuracyDateType.marshalSubTypes(version, compatibilityMode, vcard);
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalText_reducedAccuracy_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = reducedAccuracyDateType.marshalText(version, compatibilityMode);
		assertEquals("", actual);

		VCardSubTypes subTypes = reducedAccuracyDateType.marshalSubTypes(version, compatibilityMode, vcard);
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalText_reducedAccuracy_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = reducedAccuracyDateType.marshalText(version, compatibilityMode);
		assertEquals(reducedAccuracyDate.toDateAndOrTime(false), actual);

		VCardSubTypes subTypes = reducedAccuracyDateType.marshalSubTypes(version, compatibilityMode, vcard);
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalXml_reducedAccuracy() {
		assertMarshalXml(reducedAccuracyDateType, "<date>" + reducedAccuracyDate.toDateAndOrTime(false) + "</date>");
	}

	@Test
	public void marshalJson_reducedAccuracy() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = reducedAccuracyDateType.marshalJson(version);

		assertJCardValue(VCardDataType.DATE, reducedAccuracyDate.toDateAndOrTime(true), value);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	final DateOrTimeTypeImpl reducedAccuracyDateTimeType = new DateOrTimeTypeImpl();
	{
		reducedAccuracyDateTimeType.setPartialDate(reducedAccuracyDateTime);
	}

	@Test
	public void marshalText_reducedAccuracyDateTime_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		String actual = reducedAccuracyDateTimeType.marshalText(version, compatibilityMode);
		assertEquals("", actual);

		VCardSubTypes subTypes = reducedAccuracyDateTimeType.marshalSubTypes(version, compatibilityMode, vcard);
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalText_reducedAccuracyDateTime_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = reducedAccuracyDateTimeType.marshalText(version, compatibilityMode);
		assertEquals("", actual);

		VCardSubTypes subTypes = reducedAccuracyDateTimeType.marshalSubTypes(version, compatibilityMode, vcard);
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalText_reducedAccuracyDateTime_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = reducedAccuracyDateTimeType.marshalText(version, compatibilityMode);
		assertEquals(reducedAccuracyDateTime.toDateAndOrTime(false), actual);

		VCardSubTypes subTypes = reducedAccuracyDateTimeType.marshalSubTypes(version, compatibilityMode, vcard);
		assertNull(subTypes.getValue());
	}

	@Test
	public void marshalXml_reducedAccuracyDateTime() {
		assertMarshalXml(reducedAccuracyDateTimeType, "<date-time>" + reducedAccuracyDateTime.toDateAndOrTime(false) + "</date-time>");
	}

	@Test
	public void marshalJson_reducedAccuracyDateTime() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = reducedAccuracyDateTimeType.marshalJson(version);

		assertJCardValue(VCardDataType.DATE_TIME, reducedAccuracyDateTime.toDateAndOrTime(true), value);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	final DateOrTimeTypeImpl textType = new DateOrTimeTypeImpl();
	{
		textType.setText(text);
	}

	@Test
	public void marshalText_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		String actual = textType.marshalText(version, compatibilityMode);
		assertEquals("", actual);
	}

	@Test
	public void marshalText_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		String actual = textType.marshalText(version, compatibilityMode);
		assertEquals("", actual);
	}

	@Test
	public void marshalText_text_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		String actual = textType.marshalText(version, compatibilityMode);
		assertEquals(textEscaped, actual);
	}

	@Test
	public void marshalXml_text() {
		assertMarshalXml(textType, "<text>" + text + "</text>");
	}

	@Test
	public void marshalJson_text() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = textType.marshalJson(version);

		assertJCardValue(VCardDataType.TEXT, text, value);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	final DateOrTimeTypeImpl nothingType = new DateOrTimeTypeImpl();

	@Test
	public void marshalText_nothing_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		String value = nothingType.marshalText(version, compatibilityMode);

		assertEquals("", value);
	}

	@Test
	public void marshalText_nothing_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		String value = nothingType.marshalText(version, compatibilityMode);

		assertEquals("", value);
	}

	@Test
	public void marshalText_nothing_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		String value = nothingType.marshalText(version, compatibilityMode);

		assertEquals("", value);
	}

	@Test
	public void marshalXml_nothing() {
		assertMarshalXml(nothingType, "<date-and-or-time/>");
	}

	@Test
	public void marshalJson_nothing() {
		VCardVersion version = VCardVersion.V4_0;
		JCardValue value = nothingType.marshalJson(version);

		assertJCardValue(VCardDataType.DATE_AND_OR_TIME, "", value);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void unmarshalText_date_2_1() {
		VCardVersion version = VCardVersion.V2_1;

		type.unmarshalText(subTypes, dateStrExtended, version, warnings, compatibilityMode);

		assertEquals(date, type.getDate());
		assertNull(type.getPartialDate());
		assertNull(type.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_date_3_0() {
		VCardVersion version = VCardVersion.V3_0;

		type.unmarshalText(subTypes, dateStrExtended, version, warnings, compatibilityMode);

		assertEquals(date, type.getDate());
		assertNull(type.getPartialDate());
		assertNull(type.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_date_4_0() {
		VCardVersion version = VCardVersion.V4_0;

		type.unmarshalText(subTypes, dateStrExtended, version, warnings, compatibilityMode);

		assertEquals(date, type.getDate());
		assertNull(type.getPartialDate());
		assertNull(type.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_date_invalid_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		type.unmarshalText(subTypes, "invalid", version, warnings, compatibilityMode);

		assertNull(type.getDate());
		assertNull(type.getPartialDate());
		assertEquals("invalid", type.getText());
		assertWarnings(1, warnings);
	}

	@Test
	public void unmarshalXml_date() {
		VCardVersion version = VCardVersion.V4_0;

		XCardElement xe = new XCardElement(DateOrTimeTypeImpl.NAME.toLowerCase());
		xe.append("date", dateStrExtended);
		Element input = xe.element();
		type.unmarshalXml(subTypes, input, version, warnings, compatibilityMode);

		assertEquals(date, type.getDate());
		assertNull(type.getPartialDate());
		assertNull(type.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalXml_date_invalid() {
		VCardVersion version = VCardVersion.V4_0;

		XCardElement xe = new XCardElement(DateOrTimeTypeImpl.NAME.toLowerCase());
		xe.append("date", "invalid");
		Element input = xe.element();
		type.unmarshalXml(subTypes, input, version, warnings, compatibilityMode);

		assertNull(type.getDate());
		assertNull(type.getPartialDate());
		assertEquals("invalid", type.getText());
		assertWarnings(1, warnings);
	}

	@Test
	public void unmarshalJson_date() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.DATE, dateStrExtended);

		type.unmarshalJson(subTypes, value, version, warnings);

		assertEquals(date, type.getDate());
		assertNull(type.getPartialDate());
		assertNull(type.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalJson_date_invalid() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.DATE, "invalid");

		type.unmarshalJson(subTypes, value, version, warnings);

		assertNull(type.getDate());
		assertNull(type.getPartialDate());
		assertEquals("invalid", type.getText());
		assertWarnings(1, warnings);
	}

	@Test
	public void unmarshalHtml_date_in_attribute() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<time datetime=\"" + dateStrExtended + "\">June 5, 1980</time>");

		type.unmarshalHtml(element, warnings);

		assertEquals(date, type.getDate());
		assertWarnings(0, warnings);
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalHtml_date_in_text_content() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<time>" + dateStrExtended + "</time>");

		type.unmarshalHtml(element, warnings);

		assertEquals(date, type.getDate());
		assertWarnings(0, warnings);
		assertWarnings(0, warnings);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalHtml_date_invalid() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<time>June 5, 1980</time>");

		type.unmarshalHtml(element, warnings);
	}

	@Test
	public void unmarshalHtml_date_not_time_tag() {
		org.jsoup.nodes.Element element = HtmlUtils.toElement("<div>" + dateStrExtended + "</div>");

		type.unmarshalHtml(element, warnings);

		assertEquals(date, type.getDate());
		assertWarnings(0, warnings);
		assertWarnings(0, warnings);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void unmarshalText_datetime_2_1() {
		VCardVersion version = VCardVersion.V2_1;

		type.unmarshalText(subTypes, dateStr, version, warnings, compatibilityMode);
		assertEquals(date, type.getDate());
		assertNull(type.getPartialDate());
		assertNull(type.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_datetime_3_0() {
		VCardVersion version = VCardVersion.V3_0;

		type.unmarshalText(subTypes, dateStr, version, warnings, compatibilityMode);
		assertEquals(date, type.getDate());
		assertNull(type.getPartialDate());
		assertNull(type.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_datetime_4_0() {
		VCardVersion version = VCardVersion.V4_0;

		type.unmarshalText(subTypes, dateStr, version, warnings, compatibilityMode);
		assertEquals(date, type.getDate());
		assertNull(type.getPartialDate());
		assertNull(type.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalXml_datetime() {
		VCardVersion version = VCardVersion.V4_0;

		XCardElement xe = new XCardElement(DateOrTimeTypeImpl.NAME.toLowerCase());
		xe.append("date-time", dateStr);
		type.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);
		assertEquals(date, type.getDate());
		assertNull(type.getPartialDate());
		assertNull(type.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalXml_date_and_or_time() {
		VCardVersion version = VCardVersion.V4_0;

		XCardElement xe = new XCardElement(DateOrTimeTypeImpl.NAME.toLowerCase());
		xe.append(VCardDataType.DATE_AND_OR_TIME, dateStr);
		type.unmarshalXml(subTypes, xe.element(), version, warnings, compatibilityMode);
		assertEquals(date, type.getDate());
		assertNull(type.getPartialDate());
		assertNull(type.getText());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalJson_datetime() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.DATE, dateStr);

		type.unmarshalJson(subTypes, value, version, warnings);
		assertEquals(date, type.getDate());
		assertNull(type.getPartialDate());
		assertNull(type.getText());
		assertWarnings(0, warnings);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	@Test(expected = CannotParseException.class)
	public void unmarshalText_reducedAccuracyDate_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		type.unmarshalText(subTypes, reducedAccuracyDate.toDateAndOrTime(false), version, warnings, compatibilityMode);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalText_reducedAccuracyDate_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		type.unmarshalText(subTypes, reducedAccuracyDate.toDateAndOrTime(false), version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalText_reducedAccuracyDate_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		type.unmarshalText(subTypes, reducedAccuracyDate.toDateAndOrTime(false), version, warnings, compatibilityMode);

		assertNull(type.getDate());
		assertEquals(reducedAccuracyDate, type.getPartialDate());
		assertNull(type.getText());
		assertTrue(warnings.isEmpty());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalXml_reducedAccuracyDate() {
		VCardVersion version = VCardVersion.V4_0;

		XCardElement xe = new XCardElement(DateOrTimeTypeImpl.NAME.toLowerCase());
		xe.append(VCardDataType.DATE_AND_OR_TIME, reducedAccuracyDate.toDateAndOrTime(false));
		Element input = xe.element();
		type.unmarshalXml(subTypes, input, version, warnings, compatibilityMode);

		assertNull(type.getDate());
		assertEquals(reducedAccuracyDate, type.getPartialDate());
		assertNull(type.getText());
		assertTrue(warnings.isEmpty());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalJson_reducedAccuracyDate() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.DATE, reducedAccuracyDate.toDateAndOrTime(true));

		type.unmarshalJson(subTypes, value, version, warnings);

		assertNull(type.getDate());
		assertEquals(reducedAccuracyDate, type.getPartialDate());
		assertNull(type.getText());
		assertTrue(warnings.isEmpty());
		assertWarnings(0, warnings);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	@Test(expected = CannotParseException.class)
	public void unmarshalText_reducedAccuracyDateTime_2_1() {
		VCardVersion version = VCardVersion.V2_1;
		type.unmarshalText(subTypes, reducedAccuracyDateTime.toDateAndOrTime(false), version, warnings, compatibilityMode);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalText_reducedAccuracyDateTime_3_0() {
		VCardVersion version = VCardVersion.V3_0;
		type.unmarshalText(subTypes, reducedAccuracyDateTime.toDateAndOrTime(false), version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalText_reducedAccuracyDateTime_4_0() {
		VCardVersion version = VCardVersion.V4_0;
		type.unmarshalText(subTypes, reducedAccuracyDateTime.toDateAndOrTime(false), version, warnings, compatibilityMode);

		assertNull(type.getDate());
		assertEquals(reducedAccuracyDateTime, type.getPartialDate());
		assertNull(type.getText());
		assertTrue(warnings.isEmpty());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalXml_reducedAccuracyDateTime() {
		VCardVersion version = VCardVersion.V4_0;

		XCardElement xe = new XCardElement(DateOrTimeTypeImpl.NAME.toLowerCase());
		xe.append(VCardDataType.DATE_AND_OR_TIME, reducedAccuracyDateTime.toDateAndOrTime(false));
		Element input = xe.element();
		type.unmarshalXml(subTypes, input, version, warnings, compatibilityMode);

		assertNull(type.getDate());
		assertEquals(reducedAccuracyDateTime, type.getPartialDate());
		assertNull(type.getText());
		assertTrue(warnings.isEmpty());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalJson_reducedAccuracyDateTime() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.DATE_TIME, reducedAccuracyDateTime.toDateAndOrTime(true));

		type.unmarshalJson(subTypes, value, version, warnings);

		assertNull(type.getDate());
		assertEquals(reducedAccuracyDateTime, type.getPartialDate());
		assertNull(type.getText());
		assertTrue(warnings.isEmpty());
		assertWarnings(0, warnings);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	@Test(expected = CannotParseException.class)
	public void unmarshalText_text_2_1() {
		VCardVersion version = VCardVersion.V2_1;

		type.unmarshalText(subTypes, textEscaped, version, warnings, compatibilityMode);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalText_text_3_0() {
		VCardVersion version = VCardVersion.V3_0;

		type.unmarshalText(subTypes, textEscaped, version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalText_text_4_0_with_value_parameter() {
		VCardVersion version = VCardVersion.V4_0;
		subTypes.setValue(VCardDataType.TEXT);

		type.unmarshalText(subTypes, textEscaped, version, warnings, compatibilityMode);

		assertNull(type.getDate());
		assertNull(type.getPartialDate());
		assertEquals(text, type.getText());
		assertTrue(warnings.isEmpty());
		assertWarnings(0, warnings);
	}

	@Test
	public void unmarshalText_text_4_0_without_value_parameter() {
		VCardVersion version = VCardVersion.V4_0;

		type.unmarshalText(subTypes, textEscaped, version, warnings, compatibilityMode);

		assertNull(type.getDate());
		assertNull(type.getPartialDate());
		assertEquals(text, type.getText());
		assertWarnings(1, warnings);
	}

	@Test
	public void unmarshalXml_text() {
		VCardVersion version = VCardVersion.V4_0;

		XCardElement xe = new XCardElement(DateOrTimeTypeImpl.NAME.toLowerCase());
		xe.append(VCardDataType.TEXT, text);
		Element input = xe.element();
		type.unmarshalXml(subTypes, input, version, warnings, compatibilityMode);

		assertNull(type.getDate());
		assertNull(type.getPartialDate());
		assertEquals(text, type.getText());
		assertTrue(warnings.isEmpty());
		assertWarnings(0, warnings);
	}

	@Test(expected = CannotParseException.class)
	public void unmarshalXml_empty() {
		VCardVersion version = VCardVersion.V4_0;

		XCardElement xe = new XCardElement(DateOrTimeTypeImpl.NAME.toLowerCase());
		Element input = xe.element();
		type.unmarshalXml(subTypes, input, version, warnings, compatibilityMode);
	}

	@Test
	public void unmarshalJson_text() {
		VCardVersion version = VCardVersion.V4_0;

		JCardValue value = JCardValue.single(VCardDataType.TEXT, text);

		type.unmarshalJson(subTypes, value, version, warnings);

		assertNull(type.getDate());
		assertNull(type.getPartialDate());
		assertEquals(text, type.getText());
		assertTrue(warnings.isEmpty());
		assertWarnings(0, warnings);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	private static class DateOrTimeTypeImpl extends DateOrTimeType {
		public static final String NAME = "DATE";

		public DateOrTimeTypeImpl() {
			super(NAME);
		}
	}
}
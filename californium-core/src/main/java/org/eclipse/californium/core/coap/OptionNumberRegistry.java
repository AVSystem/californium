/*******************************************************************************
 * Copyright (c) 2015 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 *    Martin Lanter - architect and re-implementation
 *    Dominique Im Obersteg - parsers and initial implementation
 *    Daniel Pauli - parsers and initial implementation
 *    Kai Hudalla - logging
 ******************************************************************************/
package org.eclipse.californium.core.coap;

import org.eclipse.californium.core.network.CoapEndpoint.Builder;

/**
 * This class describes the CoAP Option Number Registry as defined in RFC 7252,
 * Section 12.2 and other CoAP extensions.
 * 
 * <a href=
 * "https://www.iana.org/assignments/core-parameters/core-parameters.xhtml#option-numbers">
 * IANA - CoAP Option Numbers</a>.
 */
public final class OptionNumberRegistry {
	public static final int UNKNOWN			= -1;

	// RFC 7252
	public static final int RESERVED_0		= 0;
	public static final int IF_MATCH		= 1;
	public static final int URI_HOST		= 3;
	public static final int ETAG			= 4;
	public static final int IF_NONE_MATCH	= 5;
	public static final int URI_PORT		= 7;
	public static final int LOCATION_PATH	= 8;
	public static final int URI_PATH		= 11;
	public static final int CONTENT_FORMAT	= 12;
	public static final int MAX_AGE			= 14;
	public static final int URI_QUERY		= 15;
	public static final int ACCEPT			= 17;
	public static final int LOCATION_QUERY	= 20;
	public static final int PROXY_URI		= 35;
	public static final int PROXY_SCHEME	= 39;
	public static final int SIZE1			= 60;
	public static final int RESERVED_1		= 128;
	public static final int RESERVED_2		= 132;
	public static final int RESERVED_3		= 136;
	public static final int RESERVED_4		= 140;

	// RFC 7641
	public static final int OBSERVE			= 6;

	// RFC 7959
	public static final int BLOCK2			= 23;
	public static final int BLOCK1			= 27;
	public static final int SIZE2			= 28;

	// RFC 8613
	public static final int OSCORE			= 9;

	// RFC 7967
	public static final int NO_RESPONSE		= 258;

	/**
	 * Option names.
	 */
	public static class Names {
		public static final String Reserved			= "Reserved";

		public static final String If_Match			= "If-Match";
		public static final String Uri_Host			= "Uri-Host";
		public static final String ETag				= "ETag";
		public static final String If_None_Match	= "If-None-Match";
		public static final String Uri_Port			= "Uri-Port";
		public static final String Location_Path	= "Location-Path";
		public static final String Uri_Path			= "Uri-Path";
		public static final String Content_Format	= "Content-Format";
		public static final String Max_Age			= "Max-Age";
		public static final String Uri_Query		= "Uri-Query";
		public static final String Accept			= "Accept";
		public static final String Location_Query	= "Location-Query";
		public static final String Proxy_Uri		= "Proxy-Uri";
		public static final String Proxy_Scheme		= "Proxy-Scheme";
		public static final String Size1			= "Size1";

		public static final String Observe			= "Observe";

		public static final String Block2			= "Block2";
		public static final String Block1			= "Block1";
		public static final String Size2			= "Size2";

		public static final String Object_Security	= "Object-Security";

		public static final String No_Response		= "No-Response";
	}

	/**
	 * Option default values.
	 */
	public static class Defaults {
		
		/** The default Max-Age. */
		public static final long MAX_AGE = 60L;
	}

	/**
	 * The format types of CoAP options.
	 */
	public static enum OptionFormat {
		INTEGER, STRING, OPAQUE, UNKNOWN, EMPTY
	}

	/**
	 * Returns the option format based on the option number.
	 * 
	 * @param optionNumber
	 *            The option number
	 * @return The option format corresponding to the option number
	 */
	public static OptionFormat getFormatByNr(int optionNumber) {
		switch (optionNumber) {
		case CONTENT_FORMAT:
		case MAX_AGE:
		case URI_PORT:
		case OBSERVE:
		case BLOCK2:
		case BLOCK1:
		case SIZE2:
		case SIZE1:
		case ACCEPT:
		case NO_RESPONSE:
			return OptionFormat.INTEGER;
		case IF_NONE_MATCH:
			return OptionFormat.EMPTY;
		case URI_HOST:
		case URI_PATH:
		case URI_QUERY:
		case LOCATION_PATH:
		case LOCATION_QUERY:
		case PROXY_URI:
		case PROXY_SCHEME:
			return OptionFormat.STRING;
		case ETAG:
		case IF_MATCH:
		case OSCORE:
			return OptionFormat.OPAQUE;
		default:
			return OptionFormat.UNKNOWN;
		}
	}

	/**
	 * Checks whether an option is critical.
	 * 
	 * @param optionNumber
	 *            The option number to check
	 * @return {@code true} if the option is critical
	 */
	public static boolean isCritical(int optionNumber) {
		return (optionNumber & 1) != 0;
	}

	/**
	 * Checks whether an option is elective.
	 * 
	 * @param optionNumber
	 *            The option number to check
	 * @return {@code true} if the option is elective
	 */
	public static boolean isElective(int optionNumber) {
		return (optionNumber & 1) == 0;
	}

	/**
	 * Checks whether an option is unsafe.
	 * 
	 * @param optionNumber
	 *            The option number to check
	 * @return {@code true} if the option is unsafe
	 */
	public static boolean isUnsafe(int optionNumber) {
		// When bit 6 is 1, an option is Unsafe
		return (optionNumber & 2) > 0;
	}

	/**
	 * Checks whether an option is safe.
	 * 
	 * @param optionNumber
	 *            The option number to check
	 * @return {@code true} if the option is safe
	 */
	public static boolean isSafe(int optionNumber) {
		return !isUnsafe(optionNumber);
	}

	/**
	 * Checks whether an option is not a cache-key.
	 * 
	 * @param optionNumber
	 *            The option number to check
	 * @return {@code true} if the option is not a cache-key
	 */
	public static boolean isNoCacheKey(int optionNumber) {
		/*
		 * When an option is not Unsafe, it is not a Cache-Key (NoCacheKey) if
		 * and only if bits 3-5 are all set to 1; all other bit combinations
		 * mean that it indeed is a Cache-Key
		 * 
		 * https://tools.ietf.org/html/rfc7252#page-40
		 * 
		 * Critical = (onum & 1);
		 * UnSafe = (onum & 2);
		 * NoCacheKey = ((onum & 0x1e) == 0x1c);
		 * 
		 *    Figure 11: Determining Characteristics from an Option Number
		 */
		return (optionNumber & 0x1E) == 0x1C;
	}

	/**
	 * Checks whether an option is a cache-key.
	 * 
	 * @param optionNumber
	 *            The option number to check
	 * @return {@code true} if the option is a cache-key
	 */
	public static boolean isCacheKey(int optionNumber) {
		return !isNoCacheKey(optionNumber);
	}

	/**
	 * Checks whether an option is a custom option.
	 * 
	 * CoAP may be extended by custom options. If critical custom option are
	 * considered, such option numbers must be provided with
	 * {@link Builder#setCriticalCustomOptions}.
	 * 
	 * @param optionNumber
	 *            the option number
	 * @return {@code true} if the option is a custom option
	 * @since 3.7
	 */
	public static boolean isCustomOption(int optionNumber) {
		switch (optionNumber) {
		case CONTENT_FORMAT:
		case MAX_AGE:
		case PROXY_URI:
		case PROXY_SCHEME:
		case URI_HOST:
		case URI_PORT:
		case IF_NONE_MATCH:
		case OBSERVE:
		case ACCEPT:
		case OSCORE:
		case BLOCK1:
		case BLOCK2:
		case SIZE1:
		case SIZE2:
		case NO_RESPONSE:
		case ETAG:
		case IF_MATCH:
		case URI_PATH:
		case URI_QUERY:
		case LOCATION_PATH:
		case LOCATION_QUERY:
			return false;
		default:
			return true;
		}
	}

	/**
	 * Checks whether an option has a single value.
	 * 
	 * @param optionNumber
	 *            the option number
	 * @return {@code true} if the option has a single value
	 */
	public static boolean isSingleValue(int optionNumber) {
		switch (optionNumber) {
		case CONTENT_FORMAT:
		case MAX_AGE:
		case PROXY_URI:
		case PROXY_SCHEME:
		case URI_HOST:
		case URI_PORT:
		case IF_NONE_MATCH:
		case OBSERVE:
		case ACCEPT:
		case OSCORE:
		case BLOCK1:
		case BLOCK2:
		case SIZE1:
		case SIZE2:
		case NO_RESPONSE:
		default:
			return true;
		case ETAG:
		case IF_MATCH:
		case URI_PATH:
		case URI_QUERY:
		case LOCATION_PATH:
		case LOCATION_QUERY:
			return false;
		}
	}

	/**
	 * Assert, that the value matches the options's definition.
	 * 
	 * See <a href="https://tools.ietf.org/html/rfc7252#page-53" target="_blank">RFC7252, 5.10.
	 * Option Definitions </a>.
	 * 
	 * @param optionNumber option's number
	 * @param value value to check
	 * @throws IllegalArgumentException if value doesn't match the definition
	 * @since 3.0
	 */
	public static void assertValue(int optionNumber, long value) {
		try {
			int length = (Long.SIZE - Long.numberOfLeadingZeros(value) + 7) / Byte.SIZE;
			assertValueLength(optionNumber, length);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(ex.getMessage() + " Value " + value);
		}
	}

	/**
	 * Assert, that the value length matches the options's definition.
	 * 
	 * See <a href="https://tools.ietf.org/html/rfc7252#page-53" target="_blank">RFC7252, 5.10.
	 * Option Definitions </a>.
	 * 
	 * @param optionNumber option's number
	 * @param valueLength value length
	 * @throws IllegalArgumentException if value length doesn't match the
	 *             definition
	 * @since 3.0
	 */
	public static void assertValueLength(int optionNumber, int valueLength) {
		int min = 0;
		int max = 65535 + 269;
		switch (optionNumber) {
		case IF_MATCH:
			max = 8;
			break;
		case URI_HOST:
		case PROXY_SCHEME:
			min = 1;
			max = 255;
			break;
		case ETAG:
			min = 1;
			max = 8;
			break;
		case IF_NONE_MATCH:
			max = 0;
			break;
		case URI_PORT:
		case CONTENT_FORMAT:
		case ACCEPT:
			max = 2;
			break;
		case NO_RESPONSE:
			max = 1;
			break;
		case URI_PATH:
		case URI_QUERY:
		case LOCATION_PATH:
		case LOCATION_QUERY:
		case OSCORE:
			max = 255;
			break;

		case MAX_AGE:
		case SIZE1:
		case SIZE2:
			max = 4;
			break;

		case PROXY_URI:
			min = 1;
			max = 1034;
			break;
		case OBSERVE:
		case BLOCK1:
		case BLOCK2:
			max = 3;
			break;
		default:
			// empty, already min/max already initialized.
		}
		if (valueLength < min || valueLength > max) {
			String name = toString(optionNumber);
			if (min == max) {
				if (min == 0) {
					throw new IllegalArgumentException(
							"Option " + name + " value of " + valueLength + " bytes must be empty.");
				} else {
					throw new IllegalArgumentException(
							"Option " + name + " value of " + valueLength + " bytes must be " + min + " bytes.");
				}
			} else {
				throw new IllegalArgumentException("Option " + name + " value of " + valueLength
						+ " bytes must be in range of [" + min + "-" + max + "] bytes.");
			}
		}
	}

	/**
	 * Checks if is uri option.
	 * 
	 * @param optionNumber
	 *            the option number
	 * @return {@code true} if is uri option
	 */
	public static boolean isUriOption(int optionNumber) {
		boolean result = optionNumber == URI_HOST || optionNumber == URI_PATH || optionNumber == URI_PORT || optionNumber == URI_QUERY;
		return result;
	}

	/**
	 * Returns a string representation of the option number.
	 * 
	 * @param optionNumber
	 *            the option number to describe
	 * @return a string describing the option number
	 */
	public static String toString(int optionNumber) {
		switch (optionNumber) {
		case RESERVED_0:
		case RESERVED_1:
		case RESERVED_2:
		case RESERVED_3:
		case RESERVED_4:
			return Names.Reserved;
		case IF_MATCH:
			return Names.If_Match;
		case URI_HOST:
			return Names.Uri_Host;
		case ETAG:
			return Names.ETag;
		case IF_NONE_MATCH:
			return Names.If_None_Match;
		case URI_PORT:
			return Names.Uri_Port;
		case LOCATION_PATH:
			return Names.Location_Path;
		case URI_PATH:
			return Names.Uri_Path;
		case CONTENT_FORMAT:
			return Names.Content_Format;
		case MAX_AGE:
			return Names.Max_Age;
		case URI_QUERY:
			return Names.Uri_Query;
		case ACCEPT:
			return Names.Accept;
		case LOCATION_QUERY:
			return Names.Location_Query;
		case PROXY_URI:
			return Names.Proxy_Uri;
		case PROXY_SCHEME:
			return Names.Proxy_Scheme;
		case OBSERVE:
			return Names.Observe;
		case BLOCK2:
			return Names.Block2;
		case BLOCK1:
			return Names.Block1;
		case SIZE2:
			return Names.Size2;
		case SIZE1:
			return Names.Size1;
		case OSCORE:
			return Names.Object_Security;
		case NO_RESPONSE:
			return Names.No_Response;
		default:
			return String.format("Unknown (%d)", optionNumber);
		}
	}

	public static int toNumber(String name) {
		if (Names.If_Match.equals(name))			return IF_MATCH;
		else if (Names.Uri_Host.equals(name))		return URI_HOST;
		else if (Names.ETag.equals(name))			return ETAG;
		else if (Names.If_None_Match.equals(name))	return IF_NONE_MATCH;
		else if (Names.Uri_Port.equals(name))		return URI_PORT;
		else if (Names.Location_Path.equals(name))	return LOCATION_PATH;
		else if (Names.Uri_Path.equals(name))		return URI_PATH;
		else if (Names.Content_Format.equals(name))	return CONTENT_FORMAT;
		else if (Names.Max_Age.equals(name))		return MAX_AGE;
		else if (Names.Uri_Query.equals(name))		return URI_QUERY;
		else if (Names.Accept.equals(name))			return ACCEPT;
		else if (Names.Location_Query.equals(name))	return LOCATION_QUERY;
		else if (Names.Proxy_Uri.equals(name))		return PROXY_URI;
		else if (Names.Proxy_Scheme.equals(name))	return PROXY_SCHEME;
		else if (Names.Observe.equals(name))		return OBSERVE;
		else if (Names.Block2.equals(name))			return BLOCK2;
		else if (Names.Block1.equals(name))			return BLOCK1;
		else if (Names.Size2.equals(name))			return SIZE2;
		else if (Names.Size1.equals(name))			return SIZE1;
		else if (Names.Object_Security.equals(name)) return OSCORE;
		else if (Names.No_Response.equals(name))	return NO_RESPONSE;
		else return UNKNOWN;
	}

	private OptionNumberRegistry() {
	}
}

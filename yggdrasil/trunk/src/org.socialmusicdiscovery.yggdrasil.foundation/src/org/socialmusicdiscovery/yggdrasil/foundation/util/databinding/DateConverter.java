/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.yggdrasil.foundation.util.databinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.databinding.conversion.IConverter;
import org.socialmusicdiscovery.yggdrasil.foundation.error.FatalApplicationException;

/**
 * @author Peer Törngren
 *
 */
public abstract class DateConverter implements IConverter {

	private static class DateToYearConverter extends DateConverter {

		private DateToYearConverter() {
			super(Date.class, String.class);
		}

		@Override
		public Object doConvert(Object fromObject) {
			return fromObject==null ? "" : new SimpleDateFormat("yyyy").format((Date) fromObject);
		}

	}

	private static class YearToDateConverter extends DateConverter {

		private YearToDateConverter() {
			super(String.class, Date.class);
		}

		@Override
		public Object doConvert(Object fromObject) throws ParseException {
			return "".equals(fromObject) ? null : new SimpleDateFormat("yyyy").parseObject((String) fromObject);
		}

	}
	public static final DateConverter dateToYear() {
		return new DateToYearConverter();
	}
	
	public static final DateConverter yearToDate() {
		return  new YearToDateConverter();
	}
	
	private final Class fromType;
	private final Class toType;
	
	private DateConverter(Class fromType, Class toType) {
		this.fromType = fromType;
		this.toType = toType;
	}

	@Override
	public Object getFromType() {
		return fromType;
	}

	@Override
	public Object getToType() {
		return toType;
	}

	@Override
	public Object convert(Object fromObject) {
		try {
			return doConvert(fromObject);
		} catch (ParseException e) {
			throw new FatalApplicationException("Unable to convert: "+fromObject, e);  //$NON-NLS-1$
		}
	}

	protected abstract Object doConvert(Object fromObject) throws ParseException;
}

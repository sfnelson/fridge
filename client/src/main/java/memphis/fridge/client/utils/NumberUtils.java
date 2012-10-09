package memphis.fridge.client.utils;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 8/10/12
 */
public class NumberUtils {

	public static SafeHtml printCurrency(int cents) {
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		if (cents < 0) {
			sb.append('-');
			cents = -cents;
		}
		sb.append('$');
		sb.append(cents / 100);
		sb.append('.');
		if (cents % 100 < 10) sb.append('0');
		sb.append(cents % 100);
		return sb.toSafeHtml();
	}
}

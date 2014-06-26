package au.com.sixtree.mule.modules.xero;

/**
 * @author Sixtree
 *
 */
@SuppressWarnings("serial")
public class XeroConnectorClientException extends Exception {
	
	public XeroConnectorClientException(Exception exception) {
		super(exception);
	}
}

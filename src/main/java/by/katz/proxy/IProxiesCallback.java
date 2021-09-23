package by.katz.proxy;

import java.util.List;

public interface IProxiesCallback {

    /**
     * @param goodProxies list from proxies
     */
    void onCompleteProxiesCheck(List<ProxyItem> goodProxies);

    /**
     * @param count count of proxies
     */
    void onGetProxiesList(int count);

    /**
     * @param error any error, while getting or checking proxies
     */
    void onError(String error);

    void onOneProxyCheck();

    void onOneFullValidProxyFound();
}
